package io2.hobbymatch.login.domain.usecase

import io2.hobbymatch.login.data.repository.AuthRepository
import io2.hobbymatch.login.data.repository.TokenRepository

/**
 * Use case to validate a Google ID token and save the resulting app JWT.
 */
class ValidateGoogleTokenUseCase(
    private val authRepository: AuthRepository,
    private val tokenRepository: TokenRepository
) {
    suspend operator fun invoke(googleIdToken: String): Result<Unit> {
        return authRepository.validateGoogleToken(googleIdToken).mapCatching { appJwt ->
            tokenRepository.saveAppToken(appJwt)
        }
    }
}