package br.senac.mobile.database

import android.content.Context
import androidx.room.Room

class Database {
    fun databaseBuilder(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "appdb").build()
    }
}