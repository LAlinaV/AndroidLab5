package com.example.task4lab5

import com.google.gson.annotations.SerializedName


class WeatherResponse {
    @SerializedName("location")
    val location: Location? = null

    @SerializedName("current")
    val current: Current? = null

    // Геттеры и сеттеры
    class Location {
        // Геттеры и сеттеры
        @SerializedName("name")
        val name: String? = null

    }

    class Current {
        @SerializedName("temp_c")
        val tempC = 0.0

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