package br.senac.mobile.models

data class Category(
    val id: Int,
    val name: String,
    val image: String,
    val items: List<Item>,
    val deleted_at: String,
)
