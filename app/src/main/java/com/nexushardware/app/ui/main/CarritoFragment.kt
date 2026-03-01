package com.nexushardware.app.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.nexushardware.app.R
import com.nexushardware.app.data.model.CarritoItem
import com.nexushardware.app.data.local.NexusBDHelper
import com.nexushardware.app.databinding.FragmentCarritoBinding
import com.nexushardware.app.ui.auth.AuthViewModel
import com.nexushardware.app.utils.adapters.CarritoAdapter
import com.nexushardware.app.utils.session.SessionManager
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch

import java.text.NumberFormat
import java.util.Locale

class CarritoFragment : Fragment() {

    private var _binding: FragmentCarritoBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbHelper: NexusBDHelper
    private lateinit var adapter: CarritoAdapter
    private var listaItems = mutableListOf<CarritoItem>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCarritoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = NexusBDHelper(requireContext())

        cargarDatos()


        // Botón Checkout
        binding.btnCheckout.setOnClickListener {

            if (listaItems.isEmpty()) {
                Toast.makeText(requireContext(),
                    "El carrito está vacío",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val total = calcularTotal()

            val bottomSheet = ConfirmOrderBottomSheet(
                listaItems,
                total
            ) {
                //  Vaciar base local (MUY IMPORTANTE)
                val userId = SessionManager.getCurrentUser()?.id ?: ""

                //  Vaciar lista en memoria
                listaItems.clear()
                adapter.notifyDataSetChanged()

                //  Actualizar UI
                binding.rvCarrito.visibility = View.GONE
                binding.layoutVacio.visibility = View.VISIBLE
                binding.tvTotalPagar.text = "S/ 0.00"

                Toast.makeText(requireContext(),
                    "Carrito vaciado correctamente 🛒",
                    Toast.LENGTH_SHORT).show()
            }

            bottomSheet.show(parentFragmentManager, "ConfirmOrder")
        }

        //Lógica del btn explorar catálogo
        binding.btnExplorar.setOnClickListener {
            //Buscamos la barra de navegación en el MainActivity y cambiamos a la pestaña de productos
            val bottomNav =
                requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigation)
            bottomNav.selectedItemId = R.id.nav_productos
        }
    }

    private fun cargarDatos() {
        val userId: String = SessionManager.getCurrentUser()?.id ?: ""

        listaItems = dbHelper.obtenerCarrito(userId).toMutableList()

        if (listaItems.isEmpty()) {
            binding.rvCarrito.visibility = View.GONE
            binding.layoutVacio.visibility = View.VISIBLE // Usamos el nuevo layout

            //esto asegura de que el textoy color vuelvan a la normalidsd si se vació manualmente
            binding.tvTituloVacio.text = "Tu carrito está vacío"
            binding.tvTituloVacio.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))

            binding.tvTotalPagar.text = "S/ 0.00"
        } else {
            binding.rvCarrito.visibility = View.VISIBLE
            binding.layoutVacio.visibility = View.GONE

            completarProductoCarritoFirebase(listaItems)

        }
    }

    private fun completarProductoCarritoFirebase(listaLocal: List<CarritoItem>) {
        lifecycleScope.launch {

            // 3. Recorreremos la lista y completamos los campos del firebase
            val listaCompletada = listaLocal.map { itemLocal ->
                try {
                    // Buscamos el documento en Firestore por el ID que ya tenemos
                    val doc = FirebaseFirestore.getInstance()
                        .collection("productos")
                        .document(itemLocal.idProducto)
                        .get()
                        .await()

                    if (doc.exists()) {
                        // Creamos una COPIA del objeto original con los campos de Firebase
                        itemLocal.copy(
                            nombre = doc.getString("nombre") ?: "",
                            precio = doc.getDouble("precio") ?: 0.0,
                            urlImagen = doc.getString("urlImagen") ?: ""
                        )
                    } else {
                        itemLocal
                    }
                } catch (e: Exception) {
                    Log.e("FirebaseError", "Error al completar item: ${itemLocal.idProducto}")
                    Log.e("FirebaseError", e.message.toString())
                    itemLocal
                }
            }.toMutableList()

            if (_binding != null && isAdded) {
                listaItems = listaCompletada
                setupRecyclerView()
                calcularTotal()
            }

        }
    }

    private fun setupRecyclerView() {
        adapter = CarritoAdapter(listaItems) { idCarrito, position ->
            eliminarItem(idCarrito)
        }
        binding.rvCarrito.layoutManager = LinearLayoutManager(context)
        binding.rvCarrito.adapter = adapter
    }

    private fun calcularTotal(): Double {
        var total = 0.0
        for (item in listaItems) {
            total += (item.precio * item.cantidad)
        }

        val format = NumberFormat.getCurrencyInstance(Locale("es", "PE"))
        binding.tvTotalPagar.text = format.format(total)

        return total
    }

    private fun eliminarItem(idCarrito: Int) {

        // val itemBorrado = listaItems[position]
        val itemBorrado = listaItems.find { it.idCarrito == idCarrito }

        val filas = dbHelper.eliminarItemCarrito(idCarrito)

        if (filas > 0) {
            itemBorrado?.let {
                // otenemos la posición real en este preciso momento
                val position = listaItems.indexOf(it)
                Log.e("posicion01:", position.toString())
                if (position != -1) {
                    // eiminamos de la fuente de datos
                    adapter.eliminarItem(position)
                    calcularTotal()

                }
            }
            if (listaItems.isEmpty()) {
                binding.rvCarrito.visibility = View.GONE
                binding.layoutVacio.visibility = View.VISIBLE //aqui muestro el nuevo layout
                binding.tvTituloVacio.text = "Tu carrito está vacío"
                binding.tvTituloVacio.setTextColor(android.graphics.Color.parseColor("#FFFFFF"))
            }

            Snackbar.make(binding.root, "${itemBorrado?.nombre} eliminado", Snackbar.LENGTH_LONG)
                .setAction("Deshacer") {
                    dbHelper.agregarAlCarrito(
                        "",
                        itemBorrado?.idProducto ?: "",
                        itemBorrado?.cantidad ?: 0
                    )
                    cargarDatos()
                }
                .setActionTextColor(android.graphics.Color.parseColor("#03DAC5"))
                .show()
        }
    }

    private fun realizarCompra() {
        val productosComprados = dbHelper.procesarCompra(1)

        if (productosComprados > 0) {
            listaItems.clear()
            adapter.notifyDataSetChanged()

            //Actualizamos la interfaz para mostrar el estado vacío pero con éxito
            binding.rvCarrito.visibility = View.GONE
            binding.layoutVacio.visibility = View.VISIBLE

            //reutilizamos el título del estado vacío para felicitar al usuario
            binding.tvTituloVacio.text = "¡Compra exitosa!\nEstamos preparando tu pedido."
            binding.tvTituloVacio.setTextColor(android.graphics.Color.parseColor("#03DAC5"))

            binding.tvTotalPagar.text = "S/ 0.00"

            Snackbar.make(
                binding.root,
                "Se procesaron $productosComprados productos.",
                Snackbar.LENGTH_LONG
            )
                .setBackgroundTint(android.graphics.Color.parseColor("#03DAC5"))
                .setTextColor(android.graphics.Color.BLACK)
                .show()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}