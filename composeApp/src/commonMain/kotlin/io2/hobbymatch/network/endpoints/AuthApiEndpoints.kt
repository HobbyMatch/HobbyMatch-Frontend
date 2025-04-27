package io2.hobbymatch.network.endpoints

object AuthApiEndpoints {
    const val BASE_PATH = "auth"

    const val LOGIN = "$BASE_PATH/login"
    const val REGISTER = "$BASE_PATH/register"
    const val REFRESH = "$BASE_PATH/refresh"
}