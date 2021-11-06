package br.senac.mobile.services

import br.senac.mobile.models.Item
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ItemService {
    @GET("item/list/{itemId}")
    fun getItem(@Path("itemId") itemId: Int): Call<Item>
}