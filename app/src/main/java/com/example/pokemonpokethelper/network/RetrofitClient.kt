package com.example.pokemonpokethelper.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://ead2ca2pokemon20250430235934-fqg2d6guacanf2g3.canadacentral-01.azurewebsites.net/api/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)
}
