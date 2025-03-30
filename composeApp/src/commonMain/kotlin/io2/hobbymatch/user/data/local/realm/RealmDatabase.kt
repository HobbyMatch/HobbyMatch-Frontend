package io2.hobbymatch.user.data.local.realm

import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

object RealmDatabase {

    // 1. Configure Realm
    private val configuration: RealmConfiguration by lazy { // Use lazy initialization
        RealmConfiguration.Builder(schema = setOf(UserProfileRealm::class))
            // Add other configurations if needed:
            // .name("hobbymatch.realm")
            // .deleteRealmIfMigrationNeeded()
            .build()
    }

    // 2. Provide the opened Realm instance
    // NOTE: Realm.open() is technically suspend, but opening it lazily
    // like this often works for simple cases. For complex startup or
    // background thread needs, a more robust async initialization is better.
    // This simple approach might block briefly on the first access.
    val instance: Realm by lazy {
        Realm.open(configuration)
    }

    // Optional: Add a function to close Realm if needed during app shutdown
    // fun close() {
    //     if (this::instance.isInitialized && !instance.isClosed()) {
    //         instance.close()
    //     }
    // }
}