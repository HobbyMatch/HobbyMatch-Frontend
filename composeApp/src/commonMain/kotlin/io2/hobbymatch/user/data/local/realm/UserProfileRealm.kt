package io2.hobbymatch.user.data.local.realm

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class UserProfileRealm : RealmObject {
    @PrimaryKey
    var id: String = "SINGLE_USER_PROFILE" // Unique profile storage ID
    var name: String = ""
    var email: String = ""
    var hobbies: RealmList<HobbyRealm> = realmListOf() // List of hobbies as Realm objects
}

class HobbyRealm : RealmObject {
    var name: String = "" // Hobby name as a String
}