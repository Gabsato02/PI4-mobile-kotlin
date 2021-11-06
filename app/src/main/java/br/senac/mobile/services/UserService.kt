package br.senac.mobile.services

import br.senac.mobile.models.User
import retrofit2.Call
import retrofit2.http.GET

interface UserService {

    // TODO: O ID do usuário deve vir dinamicamente assim que o login for realizado
    @GET("user/list/1")
    fun getUser(): Call<User>
}