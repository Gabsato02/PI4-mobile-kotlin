package br.senac.mobile.interfaces

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.senac.mobile.models.Cart

@Dao
interface CartDAO {
    @Insert
    fun insert(cart: Cart)

    @Query("SELECT * FROM cart")
    fun list(): List<Cart>
}