package br.senac.mobile.services

import br.senac.mobile.models.Category
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryService {

    @GET("category/list")
    fun getCategory(): Call<List<Category>>
}