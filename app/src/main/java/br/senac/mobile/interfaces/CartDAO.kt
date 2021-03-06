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

    @Query("DELETE FROM cart WHERE itemId = :itemId")
    fun delete(itemId: Int)

    @Query("DELETE FROM cart WHERE id != 0")
    fun deleteAll()

    @Query("UPDATE cart SET quantity = :quantity WHERE id = :id")
    fun update(quantity: Int, id: Int)
}
