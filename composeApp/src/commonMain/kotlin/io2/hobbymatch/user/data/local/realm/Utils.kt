package io2.hobbymatch.user.data.local.realm

import io2.hobbymatch.user.domain.Hobby
import io2.hobbymatch.user.domain.User

fun UserProfileRealm.toDomainModel(): User {
    return User(
        id = this.id,
        name = this.name,
        email = this.email,
        hobbies = this.hobbies.map { Hobby(it.name) }
    )
}

fun User.toRealmObject(): UserProfileRealm {
    return UserProfileRealm().apply {
        id = this@toRealmObject.id
        name = this@toRealmObject.name
        email = this@toRealmObject.email
        hobbies.clear()
        hobbies.addAll(
            this@toRealmObject.hobbies.map { HobbyRealm().apply { name = it.name } }
        )
    }
}