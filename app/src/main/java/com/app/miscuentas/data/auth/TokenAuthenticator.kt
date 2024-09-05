package com.app.miscuentas.data.auth

class TokenAuthenticator {
    var token: String? = null

    fun saveToken(newToken: String) {
        token = newToken
    }

    fun getToken(): String? = token
}