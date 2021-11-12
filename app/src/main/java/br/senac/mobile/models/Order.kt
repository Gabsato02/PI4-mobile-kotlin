package br.senac.mobile.models

data class Order(
    val id: Int,
    val store_id: Int,
    val store: Store,
    val user_id: Int,
    val order_items: List<OrderItem>,
    var created_at: String,
    var updated_at: String? = null,
    var deleted_at: String? = null,
)

data class OrderItem(
    var id: Int,
    var price: Int,
    var quantity: Int,
    var order_id: Int,
    var item_id: Int,
    var item: Item,
)

data class OrderCreate(
    val store_id: Int,
    val user_id: Int
)

data class OrderAddItem(
    val order_id: Int,
    val item_id: Int,
    val price: Int,
    val quantity: Int
)

