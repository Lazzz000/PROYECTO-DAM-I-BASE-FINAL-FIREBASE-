package com.nexushardware.app.domain.usecase

import com.nexushardware.app.data.repository.AuthRepository

class SignInWithGoogleUseCase (
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String) = repository.firebaseAuthWithGoogle(idToken)
}
