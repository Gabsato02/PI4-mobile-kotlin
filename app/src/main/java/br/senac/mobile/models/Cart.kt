package br.senac.mobile.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Cart(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    var name: String,
    var price: String,
    var quantity: String,
)
