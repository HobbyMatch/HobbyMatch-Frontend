package io2.hobbymatch.network

data class ApiConfig(
    val baseUrl: String,
    val apiVersion: String,
    val timeout: Long = 30000,
    val useHttps: Boolean = true,
    val headers: Map<String, String> = emptyMap()
) {
    // Computed property to get the full base URL with protocol
    private val fullBaseUrl: String
        get() = "${if (useHttps) "https" else "http"}://$baseUrl/api/$apiVersion"

    // Helper method to get endpoints
    fun getEndpoint(path: String): String = "$fullBaseUrl/$path"

    companion object {
        // Predefined environments
        val DEVELOPMENT = ApiConfig(
            baseUrl = PlatformApiConfig.getDevelopmentBaseUrl(),
            apiVersion = "v1",
            useHttps = false
        )

        val PRODUCTION = ApiConfig(
            baseUrl = "192.168.0.47:8080",
            apiVersion = "v1",
            useHttps = false
        )
    }
}
