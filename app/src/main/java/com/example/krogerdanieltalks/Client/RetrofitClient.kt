package com.example.krogerdanieltalks.Client

import com.example.krogerdanieltalks.api.KrogerAPIService
import com.example.krogerdanieltalks.utils.Constants
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.internal.http2.ConnectionShutdownException
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.jvm.Throws

object RetrofitClient {

    private val loggingCrashes = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
    private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor(loggingCrashes)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    val krogerAPIService : KrogerAPIService by lazy {
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okHttpClient)
            .build()
            .create(KrogerAPIService::class.java)
    }

    //Below code created by Amir Raza from stackoverflow
    class LoggingInterceptor : Interceptor
    {
        @Throws(Exception::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            try {
                val response = chain.proceed(request)

                val bodyString = response.body.toString()

                return response.newBuilder()
                    .body(bodyString.toResponseBody(response.body?.contentType()))
                    .build()
            } catch (e: Exception) {
                e.printStackTrace()
                var msg = ""
                when (e) {
                    is retrofit2.HttpException -> {
                        msg = "Bad HTTP Request"
                    }
                    
                    is SocketTimeoutException -> {
                        msg = "Timeout - Please check your internet connection"
                    }

                    is UnknownHostException -> {
                        msg = "Unable to make a connection. Please check your internet"
                    }

                    is ConnectionShutdownException -> {
                        msg = "Connection shutdown. Please check your internet"
                    }

                    is IOException -> {
                        msg = "Server is unreachable, please try again later."
                    }

                    is IllegalStateException -> {
                        msg = "${e.message}"
                    }

                    else -> {
                        msg = "Other exception: ${e.message}"
                    }
                }

                return Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .code(999)
                    .message(msg)
                    .body("{${e}}".toResponseBody(null))
                    .build()
            }
        }
    }
}