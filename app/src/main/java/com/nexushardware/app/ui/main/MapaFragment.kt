package com.nexushardware.app.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.nexushardware.app.R

class MapaFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    // Ubicación exacta de la tienda (Compupalace)
    private val ubicacionTienda = LatLng(-12.055109153156392, -77.03775208526304)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_mapa, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configurar el botón de "CÓMO LLEGAR"
        view.findViewById<Button>(R.id.btnIrTienda).setOnClickListener {
            abrirRutaEnGoogleMaps()
        }

        // Iniciar el mapa
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // 1. Aplicar el tema oscuro Premium (Dark Tech)
        try {
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style_dark)
            )
            if (!success) {
                Log.e("Mapa", "Fallo al cargar el estilo del mapa.")
            }
        } catch (e: Exception) {
            Log.e("Mapa", "No se encontró el archivo de estilo JSON: ", e)
        }

        // 2. Agregar el marcador
        googleMap.addMarker(
            MarkerOptions()
                .position(ubicacionTienda)
                .title("Nexus Hardware")
        )

        // 3. Posicionar la cámara (Zoom nivel 16 para vista de calles)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionTienda, 16f))

        // 4. Configurar controles UI para un aspecto limpio
        googleMap.uiSettings.apply {
            isZoomControlsEnabled = false // Quitamos los botones +/- por defecto
            isMapToolbarEnabled = false // Quitamos la barra superior para usar nuestro propio botón
            isCompassEnabled = true
        }
    }

    private fun abrirRutaEnGoogleMaps() {
        // Generar URI de navegación ("google.navigation:q=lat,lng")
        val gmmIntentUri = Uri.parse("google.navigation:q=${ubicacionTienda.latitude},${ubicacionTienda.longitude}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
        mapIntent.setPackage("com.google.android.apps.maps")

        // Verificar si el dispositivo tiene Google Maps instalado
        if (mapIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(mapIntent)
        } else {
            // Si no tiene la app instalada, lo abrimos en el navegador web
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${ubicacionTienda.latitude},${ubicacionTienda.longitude}"))
            startActivity(browserIntent)
        }
    }
}