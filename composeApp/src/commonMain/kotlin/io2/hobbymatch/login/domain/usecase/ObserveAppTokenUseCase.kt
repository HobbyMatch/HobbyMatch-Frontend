package io2.hobbymatch.login.domain.usecase

import io2.hobbymatch.login.data.repository.AppJwt
import io2.hobbymatch.login.data.repository.TokenRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case to observe changes to the locally stored application JWT.
 */
class ObserveAppTokenUseCase(private val tokenRepository: TokenRepository) {
    operator fun invoke(): Flow<AppJwt?> {
        return tokenRepository.getAppTokenFlow()
    }
}