package com.example.task4lab5

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApi {
    @GET("current.json")
    fun getCurrentWeather(
        @Query("key") apiKey: String?,  // Ключ API
        @Query("q") city: String?,  // Город
        @Query("aqi") aqi: String? // Опционально: качество воздуха (no/yes)
    ): Call<WeatherResponse?>?

    @GET("forecast.json")
    fun getWeatherForecast(
        @Query("key") apiKey: String?,  // Ключ API
        @Query("q") city: String?,  // Город
        @Query("days") days: Int // Количество дней для прогноза (1-10)
    ): Call<WeatherForecastResponse?>?
}