package io2.hobbymatch.network.endpoints

object EventApiEndpoints {
    // Base path
    const val BASE_PATH = "events"

    // Specific endpoints
    const val CREATE_EVENT = BASE_PATH
    const val UPDATE_EVENT = "$BASE_PATH/{eventId}"
    const val GET_EVENT = "$BASE_PATH/{eventId}"
    const val GET_ALL_EVENTS = BASE_PATH
    const val DELETE_EVENT = "$BASE_PATH/{eventId}"
    const val ENROLL_EVENT = "$BASE_PATH/{eventId}/enroll"
    const val WITHDRAW_EVENT = "$BASE_PATH/{eventId}/withdraw"
}