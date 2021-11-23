package br.senac.mobile.models

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val password: String,
    val newPassword: String? = null,
    var image: String? = null,
    val role: String,
)

data class Login(
    val email: String,
    val password: String,
)
