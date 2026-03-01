package com.nexushardware.app.ui.auth


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.nexushardware.app.data.repository.AuthRepository
import com.nexushardware.app.domain.usecase.SignInWithGoogleUseCase
import com.nexushardware.app.utils.resources.Resource

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
class AuthViewModel : ViewModel(){
    private val repository = AuthRepository()
    private val signInUseCase = SignInWithGoogleUseCase(repository)



    private val _authState = MutableStateFlow<Resource<Any>>(Resource.Idle())
    val authState: StateFlow<Resource<Any>> = _authState

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = Resource.Loading()
            try {
                val user = signInUseCase(idToken)

                if (user != null) {
                    _authState.value = Resource.Success(user)
                } else {
                    _authState.value = Resource.Error("Error de autenticación")
                }
            } catch (e: Exception) {
                _authState.value = Resource.Error(e.message ?: "Error desconocido")
            }
        }
    }
    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        repository.login(email, password, onResult)
    }
    fun register(nombre: String, email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        repository.register(nombre, email, password, onResult)
    }
    fun getCurrentUser() = repository.getCurrentUser()
}