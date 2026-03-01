package com.nexushardware.app.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nexushardware.app.R
import com.nexushardware.app.databinding.FragmentInicioBinding
import com.nexushardware.app.databinding.FragmentNosotrosBinding
import com.nexushardware.app.utils.session.SessionManager


class NosotrosFragment : Fragment() {
    private var _binding: FragmentNosotrosBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentNosotrosBinding.inflate(inflater, container, false)

        if (SessionManager.getCurrentUser() != null) {
            val nombreCompleto = SessionManager.getCurrentUser()?.nombreCompleto
            val primerNombre = nombreCompleto
                ?.split(" ")
                ?.first()
                ?.lowercase()
                ?.replaceFirstChar { it.uppercase() }

            binding.tvWelcome.text = "Bienvenido a Nexus, $primerNombre"
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}