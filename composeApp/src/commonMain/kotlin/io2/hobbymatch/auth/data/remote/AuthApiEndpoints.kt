package io2.hobbymatch.auth.data.remote

object AuthApiEndpoints {
    const val BASE_PATH = "auth"

    const val GOOGLE_LOGIN = "$BASE_PATH/mobile/google"
    const val LOGIN = "$BASE_PATH/login"
    const val REGISTER = "$BASE_PATH/register"
    const val REFRESH = "$BASE_PATH/refresh"
    const val ME = "$BASE_PATH/me"
}
