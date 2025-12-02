package com.example.krogerdanieltalks.api

import com.example.krogerdanieltalks.apiModels.token.productId.ProductId
import com.example.krogerdanieltalks.apiModels.token.Token
import com.example.krogerdanieltalks.apiModels.token.productTerm.ProductTerm
import com.example.krogerdanieltalks.utils.Constants.Companion.CREDENTIALS_ENCODED
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
interface KrogerAPIService {



    @Headers("Content-Type: application/x-www-form-urlencoded",
        "Authorization: Basic $CREDENTIALS_ENCODED")
    @POST("/v1/connect/oauth2/token")
    suspend fun getToken(
        @Query("grant_type") clientCredentials: String,
        @Query("scope") scope: String,
    ) : Token

    @Headers("Accept: application/json")
    @GET("v1/products/{id}")
    suspend fun getProduct(@Path(value = "id", encoded = true) id: String,
                      /* @Path(value = "LOCATION_ID", encoded = true) locationID : String,*/
                           @Header("Authorization") token: String,
                           @Query("filter.locationId") locationId: String) : ProductId

    @Headers("Accept: application/json")
    @GET("/v1/products")
    suspend fun searchProductByTerm(@Header("Authorization") token: String,
//                                    @Query("filter.brand") brand: String,
                                    @Query("filter.term") term: String,
                                    @Query("filter.locationId") locationId: String,
                                    @Query("filter.limit") limit: String) : ProductTerm

}