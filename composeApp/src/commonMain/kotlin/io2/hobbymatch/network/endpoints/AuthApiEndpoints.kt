package io2.hobbymatch.network.endpoints

object AuthApiEndpoints {
    const val BASE_PATH = "auth"

    const val GOOGLE_LOGIN = "$BASE_PATH/mobile/google" // TODO: add google login endpoint
    const val LOGIN = "$BASE_PATH/login"
    const val REGISTER = "$BASE_PATH/register"
    const val REFRESH = "$BASE_PATH/refresh"
}