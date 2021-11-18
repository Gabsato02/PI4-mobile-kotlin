package br.senac.mobile.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class API {
    val baseUrl = "http://192.168.0.13:8080/api/"
    private val timeout = 30L

    private val retrofit: Retrofit
        get() {
            val okHTTP = OkHttpClient.Builder()
                .readTimeout(timeout, TimeUnit.SECONDS)
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHTTP)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

    val category: CategoryService
        get() {
            return retrofit.create(CategoryService::class.java)
        }

    val item: ItemService
        get() {
            return retrofit.create(ItemService::class.java)
        }

    val order: OrderService
        get() {
            return retrofit.create(OrderService::class.java)
        }

    val user: UserService
        get() {
            return retrofit.create(UserService::class.java)
        }
}