package br.senac.mobile.services

import br.senac.mobile.models.User
import retrofit2.Call
import retrofit2.http.*

interface UserService {

    // TODO: O ID do usuário deve vir dinamicamente assim que o login for realizado
    @GET("user/list/1")
    fun getUser(): Call<User>

    @POST("user/create")
    fun createUser(@Body user: User): Call<User>

    @PUT("user/update/{userId}")
    fun updateUser(@Path("userId") userId: Int, @Body user: User): Call<User>
}