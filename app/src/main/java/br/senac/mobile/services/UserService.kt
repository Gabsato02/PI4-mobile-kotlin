package br.senac.mobile.services

import br.senac.mobile.models.CustomResponse
import br.senac.mobile.models.Login
import br.senac.mobile.models.User
import retrofit2.Call
import retrofit2.http.*

interface UserService {
    @GET("user/list/{userId}")
    fun getUser(@Path("userId") userId: Int): Call<User>

    @POST("user/login")
    fun postLogin(@Body login: Login): Call<CustomResponse>

    @POST("user/create")
    fun createUser(@Body user: User): Call<User>

    @PUT("user/update/{userId}")
    fun updateUser(@Path("userId") userId: Int, @Body user: User): Call<User>
}