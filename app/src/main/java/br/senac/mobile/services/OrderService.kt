package br.senac.mobile.services

import br.senac.mobile.models.Order
import retrofit2.Call
import retrofit2.http.GET

interface OrderService {

    // TODO: O ID do usuário deve vir dinamicamente assim que o login for realizado
    @GET("order/list/user/1")
    fun getOrder(): Call<List<Order>>
}