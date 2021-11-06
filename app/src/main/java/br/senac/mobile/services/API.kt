package br.senac.mobile.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class API {
    val baseUrl = "http://192.168.15.12:8080/api/"
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
}