package br.senac.mobile.services

import br.senac.mobile.models.CustomResponse
import br.senac.mobile.models.Order
import br.senac.mobile.models.OrderAddItem
import br.senac.mobile.models.OrderCreate
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrderService {

    @GET("order/list/user/{userId}")
    fun getOrder(@Path("userId") userId: Int): Call<List<Order>>

    @POST("order/create")
    fun postOrder(@Body orderCreate: OrderCreate): Call<Int>

    @POST("order/add-item")
    fun postAddItemToOrder(@Body orderAddItem: OrderAddItem): Call<CustomResponse>
}