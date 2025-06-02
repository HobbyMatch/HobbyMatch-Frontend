package io2.hobbymatch.business.data.remote

object BusinessClientApiEndpoints {
    private const val BCLIENTS_BASE_PATH = "businessclients"

    const val GET_BCLIENT = "$BCLIENTS_BASE_PATH/{clientId}"
    const val UPDATE_BCLIENT = "$BCLIENTS_BASE_PATH/{clientId}"

    private const val VENUES_BASE_PATH = "venues"

    const val ADD_VENUE = VENUES_BASE_PATH
    const val GET_ALL_VENUES = VENUES_BASE_PATH
    const val GET_VENUE = "$VENUES_BASE_PATH/{venueId}"
    const val DELETE_VENUE = "$VENUES_BASE_PATH/{venueId}"
    const val UPDATE_VENUE = "$VENUES_BASE_PATH/{venueId}"
}