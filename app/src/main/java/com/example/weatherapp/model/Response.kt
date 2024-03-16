package com.example.weatherapp.model

import kotlin.collections.List


//class Coord {
//    val lon = 0.0
//    val lat = 0.0
//    override fun toString(): String {
//        return "Coord(lon=$lon, lat=$lat)"
//    }
//}
data class Weather(var id: Int = 0, var main: String = "",
                   var description: String = "", var icon: String = "") {
    override fun toString(): String {
        return "Weather(id=$id, main='$main', description='$description', icon='$icon')"
    }
}
data class Main(var temp: Double = 0.0, var temp_min: Double = 0.0,
                var temp_max: Double = 0.0, var pressure: Int = 0,
                var humidity: Int = 0 ){
    override fun toString(): String {
        return "Main(temp=$temp, tempMin: $temp_min, tempMax: $temp_max pressure=$pressure, humidity=$humidity)"
    }
}
data class Wind(var speed: Double = 0.0){
    override fun toString(): String {
        return "Wind(speed=$speed)"
    }
}
class CurrentWeather {
    val weather: List<Weather> = listOf()
    val main =  Main()
    val wind = Wind()
    val clouds = Clouds()
    val visibility = 0
    override fun toString(): String {
        return "Response(weather=$weather, main=$main, wind=$wind)"
    }
}
data class FiveDayForecast (var list    : ArrayList<com.example.weatherapp.model.List> = arrayListOf())

data class List (
    var dt         : Long               = 0,
    var main       : Main              = Main(),
    var weather    : ArrayList<Weather> = arrayListOf(),
    var clouds     : Clouds            = Clouds(),
    var wind       : Wind              = Wind(),
    var visibility : Int               = 0,
)

data class Clouds(var all: Int = 0)

data class HourlyWeather(var hourly: List<Hourly>)

data class Hourly(
    var dt         : Long,
    var temp       : Double,
    var weather    : List<Weather>
)