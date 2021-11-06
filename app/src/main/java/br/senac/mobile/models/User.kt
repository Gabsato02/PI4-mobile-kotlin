package br.senac.mobile.models

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val image: String,
    val role: String,
)
