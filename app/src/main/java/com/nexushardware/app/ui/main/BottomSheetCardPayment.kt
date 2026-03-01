package com.nexushardware.app.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.nexushardware.app.R
import com.nexushardware.app.data.local.NexusBDHelper
import java.util.Calendar

class BottomSheetCardPayment : BottomSheetDialogFragment() {

    interface OnCardValidatedListener {
        fun onCardValidated()
    }

    private var listener: OnCardValidatedListener? = null

    fun setOnCardValidatedListener(listener: OnCardValidatedListener) {
        this.listener = listener
    }

    private lateinit var etCardNumber: TextInputEditText
    private lateinit var etCardName: TextInputEditText
    private lateinit var etExpiry: TextInputEditText
    private lateinit var etCVV: TextInputEditText
    private lateinit var btnConfirmarTarjeta: MaterialButton


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        return inflater.inflate(R.layout.bottomsheet_card_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etCardNumber = view.findViewById(R.id.etCardNumber)
        etCardName = view.findViewById(R.id.etCardName)
        etExpiry = view.findViewById(R.id.etExpiry)
        etCVV = view.findViewById(R.id.etCVV)
        btnConfirmarTarjeta = view.findViewById(R.id.btnConfirmarTarjeta)


        setupExpiryAutoFormat()


        btnConfirmarTarjeta.setOnClickListener {
            validarTarjeta()
        }
    }

    //  Agrega "/" automáticamente
    private fun setupExpiryAutoFormat() {

        etExpiry.addTextChangedListener(object : android.text.TextWatcher {

            private var isUpdating = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: android.text.Editable?) {

                if (isUpdating) return

                val text = s.toString().replace("/", "")

                if (text.length > 2) {

                    val month = text.substring(0, 2)
                    val year = text.substring(2)

                    val formatted = "$month/$year"

                    isUpdating = true
                    etExpiry.setText(formatted)
                    etExpiry.setSelection(formatted.length)
                    isUpdating = false
                }
            }
        })
    }

    private fun validarTarjeta() {

        val numero = etCardNumber.text.toString().trim()
        val nombre = etCardName.text.toString().trim()
        val fecha = etExpiry.text.toString().trim()
        val cvv = etCVV.text.toString().trim()

        // Número
        if (numero.length != 16 || !numero.all { it.isDigit() }) {
            etCardNumber.error = "Número inválido"
            return
        }

        // Nombre
        if (nombre.isEmpty()) {
            etCardName.error = "Ingrese el nombre"
            return
        }

        // Fecha MM/AA
        if (!fecha.matches(Regex("\\d{2}/\\d{2}"))) {
            etExpiry.error = "Formato correcto: MM/AA"
            return
        }

        val mes = fecha.substring(0, 2).toInt()
        val año = fecha.substring(3, 5).toInt()

        if (mes < 1 || mes > 12) {
            etExpiry.error = "Mes inválido"
            return
        }

        // Validar que no sea fecha pasada
        val calendario = Calendar.getInstance()
        val añoActual = calendario.get(Calendar.YEAR) % 100
        val mesActual = calendario.get(Calendar.MONTH) + 1

        if (año < añoActual || (año == añoActual && mes < mesActual)) {
            etExpiry.error = "Tarjeta vencida"
            return
        }

        // CVV
        if (cvv.length != 3 || !cvv.all { it.isDigit() }) {
            etCVV.error = "CVV inválido"
            return
        }

        // Todo válido
        Toast.makeText(
            requireContext(),
            "Tarjeta validada correctamente ✅",
            Toast.LENGTH_SHORT
        ).show()

        listener?.onCardValidated()
        dismiss()
    }
}