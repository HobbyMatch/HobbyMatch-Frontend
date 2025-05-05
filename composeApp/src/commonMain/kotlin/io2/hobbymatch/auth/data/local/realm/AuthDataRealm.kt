package io2.hobbymatch.auth.data.local.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class AuthDataRealm : RealmObject {
    @PrimaryKey
    var id: String = "LOGIN_DATA"
    var idToken: String = ""
    var accessToken: String = ""
    var refreshToken: String = ""
    var userId: Long = 0
    var email: String = ""
    var name: String = ""
}