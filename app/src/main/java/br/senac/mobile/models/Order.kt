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
