package com.nexushardware.app.ui.main

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.nexushardware.app.R

class ContactoFragment : Fragment() {

    private val numero = "51906426390"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_contacto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnEnviar = view.findViewById<Button>(R.id.btnEnviarMensaje)
        val btnLlamar = view.findViewById<Button>(R.id.btnLlamar)

        btnEnviar.setOnClickListener {
            abrirWhatsApp()
        }

        btnLlamar.setOnClickListener {
            hacerLlamada()
        }
    }

    private fun abrirWhatsApp() {

        val mensaje = """
            Hola 👋 Nexus Hardware,

            Me gustaría recibir información sobre sus productos y servicios.
            Quedo atento(a) a su respuesta.

            Gracias.
        """.trimIndent()

        val url = "https://wa.me/$numero?text=${Uri.encode(mensaje)}"

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                requireContext(),
                "No tienes WhatsApp instalado",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun hacerLlamada() {

        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:$numero")

        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                requireContext(),
                "No se pudo abrir el marcador",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}