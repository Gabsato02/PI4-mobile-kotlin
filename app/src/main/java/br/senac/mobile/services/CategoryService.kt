package br.senac.mobile.services

import br.senac.mobile.models.Category
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CategoryService {

    @GET("category/list")
    fun getCategories(@Query("items") Items: Boolean): Call<List<Category>>

    @GET("category/list/{categoryId}?items=true")
    fun getCategoryWithItems(@Path("categoryId") CategoryId: Int): Call<Category>
}