package io2.hobbymatch.events.data.remote

object AuthApiEndpoints {
    const val BASE_PATH = "events"

    const val CREATE_EVENT = "$BASE_PATH"
    const val UPDATE_EVENT = "$BASE_PATH/{eventId}"
    const val GET_EVENT = "$BASE_PATH/{eventId}"
    const val GET_ALL_EVENTS = "$BASE_PATH"
    const val DELETE_EVENT = "$BASE_PATH/{eventId}"
    const val JOIN_EVENT = "$BASE_PATH/{eventId}/enroll"
    const val LEAVE_EVENT = "$BASE_PATH/{eventId}/withdraw"
}