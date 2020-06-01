package com.kailiu.spaceship.cloud


import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

object OkHttpClientHelper {

    fun createOkHttpClientBuilderWithAuthenticator(enableHttpLogger: Boolean): OkHttpClient.Builder {
        return createOkHttpClientBuilder(enableHttpLogger)
    }

    fun createOkHttpClientBuilder(enableHttpLogger: Boolean): OkHttpClient.Builder {

        val okHttpClientBuilder = OkHttpClient.Builder()
        if (enableHttpLogger) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            okHttpClientBuilder.addInterceptor(logging)
        }

        return okHttpClientBuilder
    }


}