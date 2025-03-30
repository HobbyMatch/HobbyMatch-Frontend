package io2.hobbymatch.user.data.local.realm // Or your chosen package

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

// Define the Realm object corresponding to the user profile data
class UserProfileRealm : RealmObject {
    // Using a fixed primary key for simplicity, assuming only one profile is stored locally.
    // For multi-user scenarios, use a unique ID derived from the logged-in user.
    @PrimaryKey
    var id: String = "SINGLE_USER_PROFILE"
    var email: String = ""
    var username: String = ""
    var name: String = ""
    var surname: String = ""
    var hobbies: RealmList<String> = realmListOf() // Realm uses RealmList
    var birthday: String = "" // Consider RealmInstant for proper date handling if needed
    var gender: String = ""
    var bio: String = ""
}