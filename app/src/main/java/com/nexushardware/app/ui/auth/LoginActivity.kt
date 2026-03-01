package com.nexushardware.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import com.nexushardware.app.ui.main.MainActivity
import com.nexushardware.app.data.local.NexusBDHelper
import com.nexushardware.app.databinding.ActivityLoginBinding
import android.util.Patterns
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nexushardware.app.R
import androidx.lifecycle.lifecycleScope
import com.nexushardware.app.utils.resources.Resource
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
//    private lateinit var dbHelper: NexusBDHelper


    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel = AuthViewModel()
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                account.idToken?.let {
                    viewModel.signInWithGoogle(it)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Inicalizamos la bd
      //  dbHelper = NexusBDHelper(this)

        setupListeners()
        binding.lottieLoadingLogin.visibility = View.GONE
        setupGoogleSignIn()
        observeAuthState()
        setupClick()
    }

    private fun setupListeners() {
        //limpia los errores visuales cuando se escribee
        binding.etEmail.doOnTextChanged { _, _, _, _ -> binding.tilEmail.error = null }
        binding.etPassword.doOnTextChanged { _, _, _, _ -> binding.tilPassword.error = null }

        //btn ingresar
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()

            var esValidoParaEnviar = true

            //validar correo visualmente
            if (email.isEmpty()) {
                binding.tilEmail.error = "Ingrese su correo"
                esValidoParaEnviar = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Ingrese un correo válido"
                esValidoParaEnviar = false
            }

            //validar contraseña visualmente
            if (pass.isEmpty()) {
                binding.tilPassword.error = "Ingrese su contraseña"
                esValidoParaEnviar = false
            }

            //si hay errores en las cajas detenemos el proceso aqui
            if (!esValidoParaEnviar) return@setOnClickListener


            viewModel.login(email, pass) { success, error ->

                if (success) {
                    Toast.makeText(this,
                        "Bienvenido, $email",
                        Toast.LENGTH_SHORT).show()
                    //exitoso
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish() //cerramos Login para que no pueda volver atrás
                } else {
                    Snackbar.make(binding.root, "❌ Correo o contraseña incorrectos", Snackbar.LENGTH_LONG)
                        .setBackgroundTint(android.graphics.Color.parseColor("#CF6679"))
                        .setTextColor(android.graphics.Color.BLACK)
                        .show()
                }
            }

        }

        //btn registrarse
        binding.tvRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun setupClick() {
        binding.btnGoogle.setOnClickListener {
            launcher.launch(googleSignInClient.signInIntent)
        }
    }

    private fun observeAuthState() {
        lifecycleScope.launch {
            viewModel.authState.collect { state ->
                when (state) {

                    is Resource.Idle -> {
                        binding.lottieLoadingLogin.visibility = View.GONE
                        Log.e("progresbar00: ","abierto-00ABIERTO")

                    }
                    is Resource.Loading -> {
                        binding.lottieLoadingLogin.visibility = View.VISIBLE
                        Log.e("progresbar01: ","abierto-01ABIERTO")

                    }

                    is Resource.Success -> {
                        goToMain()
                    }

                    is Resource.Error -> {
                        binding.lottieLoadingLogin.visibility = View.GONE
                        Toast.makeText(this@LoginActivity, state.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    //Envia al main
    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onStart() {
        super.onStart()
        // Auto-login si ya hay sesión
        if (viewModel.getCurrentUser() != null) {
            goToMain()
        }
    }
}