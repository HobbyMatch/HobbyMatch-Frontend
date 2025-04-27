package io2.hobbymatch.login.data.local.realm

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class LoginDataRealm : RealmObject {
    @PrimaryKey
    var id: String = "LOGIN_DATA"
    var idToken: String = ""
    var jwtToken: String = ""
}
