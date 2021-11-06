package br.senac.mobile.models

data class Store(
    var id: Int,
    var name: String,
    var description: String,
    var owner_id: Int,
    var items: List<Item>,
)
