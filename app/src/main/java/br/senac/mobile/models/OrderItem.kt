package br.senac.mobile.models

data class OrderItem(
    var id: Int,
    var price: Int,
    var quantity: Int,
    var order_id: Int,
    var item_id: Int,
    var item: Item,
)
