package org.sonicboom.githubapplication.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.sonicboom.githubapplication.constant.BASE_URL
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiService {
    private var retrofit: Retrofit? = null
    private val okHttpBuilder =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

    fun <S> createService(serviceClass:Class<S>?):S{
        if (retrofit==null){
            retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL).client(okHttpBuilder).build()
        }
        return retrofit!!.create(serviceClass!!)
    }

    val serviceGithub : GithubService by lazy {
        createService(GithubService::class.java)
    }
}

