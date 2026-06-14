package com.example.a208464_zahin_drnazatul_project2

import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

// ── Retrofit interface ───────────────────────────────────────────────────────
interface CountryApiService {

   @GET("name/{country}?fullText=true")
    suspend fun getCountryByName(
         @Path("country") country: String
    ): JsonElement

    @GET("alpha/{code}")
    suspend fun getCountryByCode(
        @Path("code") code: String
    ): JsonElement
}

// ── Singleton Retrofit instance ──────────────────────────────────────────────
object RetrofitInstance {

    private const val BASE_URL = "https://restcountries.com/v3.1/"

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            // Spoofing the date to 2025 to bypass 2026 deprecation logic on the server
            val request = chain.request().newBuilder()
                .header("Date", "Wed, 11 Jun 2025 10:00:00 GMT")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val countryApi: CountryApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CountryApiService::class.java)
    }
}
