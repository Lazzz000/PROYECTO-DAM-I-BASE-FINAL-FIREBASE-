package com.nexushardware.app.utils.session

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nexushardware.app.data.model.User

object SessionManager {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // retorna el usuario autenticado
    fun getCurrentUser(): User? {
        val user = auth.currentUser
        return user?.let {
            User(
                id = it.uid,
                email = it.email,
                nombreCompleto = it.displayName,
                telefono = it.phoneNumber,
                photo = it.photoUrl?.toString(),
                esAdmin = false
            )
        }
    }

    // verifica si hay un usuario autenticado
    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // cierra sesion del usuario autenticado
    fun signOut() {
        auth.signOut()
    }
}