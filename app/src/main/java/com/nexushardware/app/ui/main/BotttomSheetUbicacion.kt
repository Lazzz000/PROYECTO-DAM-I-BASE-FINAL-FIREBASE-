package com.nexushardware.app.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.nexushardware.app.R
import java.util.Locale
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

class BottomSheetUbicacion(
    private val onUbicacionSeleccionada: (String) -> Unit
) : BottomSheetDialogFragment(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var marcador: Marker? = null
    private lateinit var etBuscar: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottomsheet_ubicacion, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etBuscar = view.findViewById(R.id.etBuscar)

        // 🔎 Buscar
        etBuscar.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarDireccion()
                true
            } else false
        }

        // 🗺 Agregar mapa correctamente al contenedor
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commitNow()

        mapFragment.getMapAsync(this)

        view.findViewById<MaterialButton>(R.id.btnConfirmarUbicacion)
            .setOnClickListener {
                confirmarUbicacion()
            }

        view.findViewById<MaterialButton>(R.id.btnMiUbicacion)
            .setOnClickListener {
                irAMiUbicacion()
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {

        map = googleMap

        val inicial = LatLng(-12.0464, -77.0428)

        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(inicial, 15f)
        )

        marcador = map.addMarker(
            MarkerOptions()
                .position(inicial)
                .draggable(true)
        )

        map.setOnMarkerDragListener(object :
            GoogleMap.OnMarkerDragListener {

            override fun onMarkerDragStart(marker: Marker) {}
            override fun onMarkerDrag(marker: Marker) {}

            override fun onMarkerDragEnd(marker: Marker) {
                marcador = marker
            }
        })
    }

    private fun irAMiUbicacion() {

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
            return
        }

        val client =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        client.lastLocation.addOnSuccessListener { location ->

            location?.let {

                val latLng = LatLng(it.latitude, it.longitude)

                map.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(latLng, 17f)
                )

                marcador?.position = latLng
            }
        }
    }

    private fun confirmarUbicacion() {

        val position = marcador?.position ?: return

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val resultados =
            geocoder.getFromLocation(position.latitude, position.longitude, 1)

        if (!resultados.isNullOrEmpty()) {

            val direccion = resultados[0].getAddressLine(0)

            onUbicacionSeleccionada(direccion)
            dismiss()
        }
    }

    private fun buscarDireccion() {

        val texto = etBuscar.text.toString().trim()
        if (texto.isEmpty()) return

        val geocoder = Geocoder(requireContext(), Locale.getDefault())

        val resultados =
            geocoder.getFromLocationName(texto, 1)

        if (!resultados.isNullOrEmpty()) {

            val location = resultados[0]

            val latLng = LatLng(
                location.latitude,
                location.longitude
            )

            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(latLng, 17f)
            )

            marcador?.position = latLng
        }
    }
}