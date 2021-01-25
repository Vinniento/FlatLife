package fh.wfp2.flatlife.other

object Constants {

    const val DATABASE_NAME = "flatlife_db"

    const val KEY_LOGGED_IN_USERNAME = "KEY_LOGGED_IN_USERNAME"
    const val KEY_PASSWORD = "KEY_PASSWORD"

    const val NO_USERNAME = "NO_USERNAME"
    const val NO_PASSWORD = "NO_PASSWORD"

    const val ENCRYPTED_SHARED_PREF_NAME = "enc_shared_pref"

    const val BASE_URL = "https://10.0.2.2:8002"

    val IGNORE_AUTH_URLS = listOf("/login", "/register")
}