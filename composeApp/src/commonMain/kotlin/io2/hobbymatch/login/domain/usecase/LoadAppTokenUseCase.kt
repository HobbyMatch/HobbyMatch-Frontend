package io2.hobbymatch.login.domain.usecase

import io2.hobbymatch.login.data.repository.AppJwt
import io2.hobbymatch.login.data.repository.TokenRepository

/**
 * Use case to load the locally stored application JWT.
 */
class LoadAppTokenUseCase(private val tokenRepository: TokenRepository) {
    suspend operator fun invoke(): AppJwt? {
        return tokenRepository.loadAppToken()
    }
}