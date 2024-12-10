package com.example.krogerdanieltalks.api

import com.example.krogerdanieltalks.apiModels.token.Token
import com.example.krogerdanieltalks.utils.Constants.Companion.CLIENT_ID
import com.example.krogerdanieltalks.utils.Constants.Companion.CLIENT_ID_ENCODED
import com.example.krogerdanieltalks.utils.Constants.Companion.CLIENT_SECRET
import com.example.krogerdanieltalks.utils.Constants.Companion.CLIENT_SECRET_ENCODED
import com.example.krogerdanieltalks.utils.Constants.Companion.CREDENTIALS_ENCODED
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
interface KrogerAPIService {

    @Headers("Content-Type: application/x-www-form-urlencoded",
        "Authorization: $CREDENTIALS_ENCODED",
        "User-Agent: productlocator",
        "client_id: $CLIENT_ID_ENCODED",
        "client_secret: $CLIENT_SECRET_ENCODED")
    @POST("/v1/connect/oauth2/token")
    suspend fun getToken(
        @Query("grant_type") clientCredentials: String,
        @Query("scope") scope: String,
    ) : Token

    @Headers("Accept: application/json",
        "Authorization: ")
    @GET("/v1/products")
    suspend fun getProduct()
}