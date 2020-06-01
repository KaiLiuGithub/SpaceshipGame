package com.kailiu.spaceship.cloud

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClientInstance {
    companion object {
        private var retrofit: Retrofit? = null
        private const val BASE_URL = "https://jsonplaceholder.typicode.com"

        fun getRetrofitInstance(): Retrofit {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(OkHttpClientHelper.createOkHttpClientBuilderWithAuthenticator(true).build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(ResourceCallAdapter())
                    .build()
            }
            return retrofit!!
        }
    }
}