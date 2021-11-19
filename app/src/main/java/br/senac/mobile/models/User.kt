package br.senac.mobile.models

data class User(
    val id: Int? = null,
    val name: String,
    val email: String,
    val password: String,
    val newPassword: String? = null,
    val image: String? = null,
    val role: String,
)
