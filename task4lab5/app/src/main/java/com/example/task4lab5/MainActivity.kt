package com.example.task4lab5

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter
    private val cities: MutableList<String> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        cities.add("Minsk")
        cities.add("Vitebsk")
        adapter = WeatherAdapter(cities, this)
        recyclerView.adapter = adapter

        // Adding a new city
        val addCityButton = findViewById<Button>(R.id.addCityButton)
        addCityButton.setOnClickListener { _: View? ->
            val cityInput = findViewById<EditText>(R.id.cityInput)
            val newCity = cityInput.text.toString()
            if (newCity.isNotEmpty()) {
                cities.add(newCity)
                adapter.notifyDataSetChanged()
                cityInput.setText("")
            }
        }
    }

    fun showWeatherDetails(city: String) {
        val api: WeatherApi = ApiClient.client?.create(WeatherApi::class.java)!!
        val call = api.getCurrentWeather("5f6696d556544ec0ab0165849251703", city, "no")
        call?.enqueue(object : Callback<WeatherResponse?> {  // <-- Добавлен "?" к типу
            override fun onResponse(
                call: Call<WeatherResponse?>,
                response: Response<WeatherResponse?>
            ) {
                if (response.isSuccessful) {
                    val weatherResponse = response.body()
                    if (weatherResponse != null) {
                        val details = """
                        City: ${weatherResponse.location?.name}
                        Temperature: ${weatherResponse.current?.tempC}°C
                        Condition: ${weatherResponse.current?.condition?.text}
                    """.trimIndent()

                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("Weather Details")
                            .setMessage(details)
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<WeatherResponse?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    fun showWeatherForecast(city: String) {
        val api: WeatherApi = ApiClient.client?.create(WeatherApi::class.java)!!
        val call = api.getWeatherForecast("5f6696d556544ec0ab0165849251703", city, 7)
        call?.enqueue(object : Callback<WeatherForecastResponse?> {  // <-- Добавлен "?" к типу
            override fun onResponse(
                call: Call<WeatherForecastResponse?>,
                response: Response<WeatherForecastResponse?>
            ) {
                if (response.isSuccessful) {
                    val forecastResponse = response.body()
                    if (forecastResponse != null) {
                        val forecastDetails = StringBuilder()
                        forecastResponse.forecast?.forecastDays?.forEach { day ->
                            forecastDetails.append("Date: ${day.date}\n")
                                .append("Max Temp: ${day.day?.maxTempC}°C\n")
                                .append("Min Temp: ${day.day?.minTempC}°C\n")
                                .append("Condition: ${day.day?.condition?.text}\n\n")
                        }
                        AlertDialog.Builder(this@MainActivity)
                            .setTitle("7-Day Forecast")
                            .setMessage(forecastDetails.toString())
                            .setPositiveButton("OK", null)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<WeatherForecastResponse?>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}