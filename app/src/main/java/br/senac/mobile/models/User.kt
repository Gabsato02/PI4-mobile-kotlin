package br.senac.mobile.models

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val newPassword: String? = null,
    val image: String? = null,
    val role: String,
)
