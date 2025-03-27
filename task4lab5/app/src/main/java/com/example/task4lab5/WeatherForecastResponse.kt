package com.example.task4lab5

import com.google.gson.annotations.SerializedName


class WeatherForecastResponse {
    @SerializedName("location")
    private val location: Location? = null

    @SerializedName("forecast")
    val forecast: Forecast? = null

    // Геттеры и сеттеры
    class Location {
        @SerializedName("name")
        private val name: String? = null // Геттеры и сеттеры
    }

    class Forecast {
        // Геттеры и сеттеры
        @SerializedName("forecastday")
        val forecastDays: List<ForecastDay>? = null

    }

    class ForecastDay {
        @SerializedName("date")
        val date: String? = null

        // Геттеры и сеттеры
        @SerializedName("day")
        val day: Day? = null

    }

    class Day {
        @SerializedName("maxtemp_c")
        val maxTempC = 0.0

        @SerializedName("mintemp_c")
        val minTempC = 0.0

        // Геттеры и сеттеры
        @SerializedName("condition")
        val condition: Condition? = null

    }

    class Condition {
        // Геттеры и сеттеры
        @SerializedName("text")
        val text: String? = null

    }
}