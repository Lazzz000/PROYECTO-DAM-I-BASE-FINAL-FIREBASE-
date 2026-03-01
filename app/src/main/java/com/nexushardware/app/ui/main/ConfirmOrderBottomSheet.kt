package com.nexushardware.app.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nexushardware.app.R
import com.nexushardware.app.data.local.NexusBDHelper
import com.nexushardware.app.data.model.CarritoItem
import java.util.Locale

class ConfirmOrderBottomSheet(
    private val listaCarrito: List<CarritoItem>,
    private val total: Double,
    private val onPedidoCompletado: () -> Unit
) : BottomSheetDialogFragment() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private lateinit var etNombre: TextInputEditText
    private lateinit var etTelefono: TextInputEditText
    private lateinit var etDireccion: TextInputEditText
    private lateinit var spinnerPago: AutoCompleteTextView
    private lateinit var btnConfirmar: MaterialButton
    private lateinit var btnUbicacion: MaterialButton

    private lateinit var dbHelper: NexusBDHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottomsheet_confirm_order, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbHelper = NexusBDHelper(requireContext())
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())


        initViews(view)
        setupSpinner()
        setupListeners()
    }

    private fun initViews(view: View) {
        etNombre = view.findViewById(R.id.etNombre)
        etTelefono = view.findViewById(R.id.etTelefono)
        etDireccion = view.findViewById(R.id.etDireccion)
        spinnerPago = view.findViewById(R.id.spinnerPago)
        btnConfirmar = view.findViewById(R.id.btnConfirmarPedido)
        btnUbicacion = view.findViewById(R.id.btnUbicacion)
    }

    private fun setupSpinner() {
        val metodos = listOf("Tarjeta", "Contraentrega")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            metodos
        )

        spinnerPago.setAdapter(adapter)
    }

    private fun setupListeners() {

        btnUbicacion.setOnClickListener {
            abrirSelectorUbicacion()
        }

        btnConfirmar.setOnClickListener {
            validarAntesDeEnviar()
        }
    }

    private fun validarAntesDeEnviar() {

        val nombre = etNombre.text.toString().trim()
        val telefono = etTelefono.text.toString().trim()
        val direccion = etDireccion.text.toString().trim()
        val metodo = spinnerPago.text.toString()

        if (nombre.isEmpty() || telefono.isEmpty()
            || direccion.isEmpty() || metodo.isEmpty()
        ) {
            Toast.makeText(requireContext(),
                "Completa todos los campos",
                Toast.LENGTH_SHORT).show()
            return
        }

        if (metodo == "Tarjeta") {
            val bottomSheet = BottomSheetCardPayment()

            bottomSheet.setOnCardValidatedListener(object :
                BottomSheetCardPayment.OnCardValidatedListener {

                override fun onCardValidated() {

                    guardarPedido("Tarjeta")
                }
            })

            bottomSheet.show(parentFragmentManager, "CardPayment")
            return
        } else {
            guardarPedido("Contraentrega")
        }
    }

    private fun abrirBottomSheetTarjeta() {

        val bottomSheet = BottomSheetCardPayment()

        bottomSheet.setOnCardValidatedListener(object :
            BottomSheetCardPayment.OnCardValidatedListener {

            override fun onCardValidated() {
                guardarPedido("Tarjeta")
            }
        })

        bottomSheet.show(parentFragmentManager, "CardPayment")
    }
    private fun guardarPedido(metodo: String) {

        val user = auth.currentUser

        if (user == null) {
            Toast.makeText(
                requireContext(),
                "Debes iniciar sesión",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Estado dinámico según método de pago
        val estadoPedido = if (metodo == "Tarjeta") {
            "Pagado"
        } else {
            "Pendiente"
        }

        //  Crear pedido
        val order = hashMapOf(
            "userId" to user.uid,
            "nombre" to etNombre.text.toString().trim(),
            "telefono" to etTelefono.text.toString().trim(),
            "direccion" to etDireccion.text.toString().trim(),
            "metodoPago" to metodo,
            "total" to total,
            "fecha" to System.currentTimeMillis(),
            "estado" to estadoPedido
        )

        firestore.collection("orders")
            .add(order)
            .addOnSuccessListener { document ->

                //  Guardar productos del carrito
                for (item in listaCarrito) {

                    val itemData = hashMapOf(
                        "productId" to item.idProducto,
                        "nombre" to item.nombre,
                        "quantity" to item.cantidad,
                        "price" to item.precio
                    )

                    document.collection("items")
                        .add(itemData)
                }

                //  Mensaje según método
                val mensaje = if (metodo == "Tarjeta") {
                    "Pago exitosamente, pedido en camino 🚚"
                } else {
                    "Pedido en camino 🚚"
                }

                Toast.makeText(

                    requireContext(),
                    mensaje,
                    Toast.LENGTH_LONG
                ).show()

                listaCarrito.forEach { c->
                    dbHelper.eliminarItemCarrito(c.idCarrito)
                }
                onPedidoCompletado()
                dismiss()
            }
            .addOnFailureListener { e ->

                Toast.makeText(
                    requireContext(),
                    "Error al guardar pedido: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
    private fun obtenerUbicacion() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )

        } else {
            obtenerUltimaUbicacion()
        }
    }

    @SuppressLint("MissingPermission")
    private fun obtenerUltimaUbicacion() {

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {

                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    val direcciones = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )

                    if (!direcciones.isNullOrEmpty()) {
                        etDireccion.setText(direcciones[0].getAddressLine(0))
                    }
                }
            }
    }

    private fun abrirSelectorUbicacion() {

        val bottomSheet = BottomSheetUbicacion { direccion ->

            etDireccion.setText(direccion)
        }

        bottomSheet.show(parentFragmentManager, "Ubicacion")
    }
}