package br.senac.mobile.models

data class Item(
    val id: Int,
    val name: String,
    val price: Int,
    val description: String,
    val volume: String,
    val category_id: Int,
    val image: String,
    val category: Category,
    val traits: List<Trait>,
    val characteristics: List<Characteristic>,
    val deleted_at: String,
)
