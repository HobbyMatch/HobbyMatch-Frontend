package io2.hobbymatch.business.data.remote

object BusinessClientApiEndpoints {
    const val BASE_PATH = "business"

    const val ADD_VENUE = "$BASE_PATH/venue/addVenue"
    const val GET_BCLIENT = "$BASE_PATH/{clientId}"
    const val UPDATE_BCLIENT = "$BASE_PATH/{clientId}"
    const val GET_VENUE = "$BASE_PATH/venue/{venueId}"
}