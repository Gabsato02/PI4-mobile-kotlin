package br.senac.mobile.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.senac.mobile.interfaces.CartDAO
import br.senac.mobile.models.Cart

@Database(entities = [Cart::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun cartDao(): CartDAO
}