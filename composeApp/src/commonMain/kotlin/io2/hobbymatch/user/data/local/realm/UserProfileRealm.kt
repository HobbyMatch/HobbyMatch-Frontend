package io2.hobbymatch.user.data.local.realm // Or your chosen package

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

// Define the Realm object corresponding to the user profile data
class UserProfileRealm : RealmObject {
    @PrimaryKey
    var id: String = "SINGLE_USER_PROFILE"
    var email: String = ""
    var name: String = ""
    var hobbies: RealmList<HobbyRealm> = realmListOf() // Changed to store `HobbyRealm` objects.
}

// Define a Realm object for Hobby to match List<Hobby> in the User model
class HobbyRealm : RealmObject {
    var name : String = ""   // Name of the hobby
}
