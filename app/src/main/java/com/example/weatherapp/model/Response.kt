package com.example.weatherapp.model

//class Coord {
//    val lon = 0.0
//    val lat = 0.0
//    override fun toString(): String {
//        return "Coord(lon=$lon, lat=$lat)"
//    }
//}
class Weather {
    val id = 0
    val main = ""
    val description = ""
    val icon = ""
    override fun toString(): String {
        return "Weather(id=$id, main='$main', description='$description', icon='$icon')"
    }
}
data class Main(var temp: Double = 0.0, var pressure: Int = 0,
                var humidity: Int = 0 ){
    override fun toString(): String {
        return "Main(temp=$temp, pressure=$pressure, humidity=$humidity)"
    }
}
data class Wind(var speed: Double = 0.0, var deg: Int = 0){
    override fun toString(): String {
        return "Wind(speed=$speed, deg=$deg)"
    }
}
class Response {
    val weather: List<Weather> = listOf()
    val main =  Main()
    val wind = Wind()
    override fun toString(): String {
        return "Response(weather=$weather, main=$main, wind=$wind)"
    }
}