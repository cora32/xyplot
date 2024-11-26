package io.iskopasi.xyplot.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.Strictness
import io.iskopasi.xyplot.pojo.PointsResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

const val BASE_URL = "https://hr-challenge.dev.tapyou.com/"

fun getClient(): OkHttpClient {
    val interceptor = HttpLoggingInterceptor().apply {
        setLevel(HttpLoggingInterceptor.Level.BODY)
    }
    return OkHttpClient.Builder().addInterceptor(interceptor).build()
}

private var gson: Gson = GsonBuilder()
    .setStrictness(Strictness.LENIENT)
    .create()

fun getRetrofit(): Retrofit = Retrofit.Builder()
    .client(getClient())
    .addConverterFactory(GsonConverterFactory.create(gson))
    .baseUrl(BASE_URL).build()

interface RestApi {
    @GET("api/test/points")
    suspend fun requestsDots(@Query("count") count: Int): Response<PointsResponse>
}