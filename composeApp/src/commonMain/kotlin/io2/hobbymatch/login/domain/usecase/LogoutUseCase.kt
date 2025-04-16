package io2.hobbymatch.login.domain.usecase

import io2.hobbymatch.login.data.repository.TokenRepository

/**
 * Use case to log the user out by clearing the local app token.
 */
class LogoutUseCase(private val tokenRepository: TokenRepository) {
    suspend operator fun invoke() {
        tokenRepository.clearAppToken()
    }
}