package org.sonicboom.githubapplication.network

import org.sonicboom.githubapplication.model.ResponseDetailUser
import org.sonicboom.githubapplication.model.ResponseSearchUsers
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubService {
    @GET("search/users")
    @Headers("Authorization: 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")
    suspend fun getUserSearch(
        @Query("q") query:String,
        @Query("page") page:Int,
        @Query("per_page") perPage:Int
    ): Response<ResponseSearchUsers>

    @GET("users/{user}")
    @Headers("Authorization: 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")
    suspend fun getDetailUser(
        @Path("user") user:String
    ): ResponseDetailUser
}