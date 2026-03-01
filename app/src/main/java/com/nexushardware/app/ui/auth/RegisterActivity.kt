package com.nexushardware.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.nexushardware.app.R
import com.nexushardware.app.ui.main.MainActivity

class RegisterActivity: AppCompatActivity(){

    private lateinit var etNombre: com.google.android.material.textfield.TextInputEditText
    private lateinit var etEmail: com.google.android.material.textfield.TextInputEditText
    private lateinit var etPassword: com.google.android.material.textfield.TextInputEditText
    private lateinit var etConfirmPassword: com.google.android.material.textfield.TextInputEditText
    private lateinit var btnRegister: com.google.android.material.button.MaterialButton
    private lateinit var lottieLoading: LottieAnimationView

    private lateinit var viewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        etNombre = findViewById(R.id.etNombre)
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)
        lottieLoading = findViewById(R.id.lottieLoading)

        btnRegister.setOnClickListener {
            registrar()
        }
    }

    private fun registrar() {

        val nombre = etNombre.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val pass = etPassword.text.toString()
        val confirm = etConfirmPassword.text.toString()

        // VALIDACIONES
        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            toast("Completa todos los campos")
            return
        }

        if (nombre.length < 3) {
            toast("Nombre muy corto")
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("Correo inválido")
            return
        }

        if (pass.length < 6) {
            toast("La contraseña debe tener mínimo 6 caracteres")
            return
        }

        if (pass != confirm) {
            toast("Las contraseñas no coinciden")
            return
        }

        // MOSTRAR LOADING
        lottieLoading.visibility = View.VISIBLE
        btnRegister.isEnabled = false

        // REGISTRO EN FIREBASE
        viewModel.register(nombre, email, pass) { success, error ->

            lottieLoading.visibility = View.GONE
            btnRegister.isEnabled = true

            if (success) {

                toast("Registro exitoso")


                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)



            } else {
                toast(error ?: "Error al registrar")
            }
        }
    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

