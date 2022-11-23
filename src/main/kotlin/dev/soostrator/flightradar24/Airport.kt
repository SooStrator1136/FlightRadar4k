package dev.soostrator.flightradar24

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.cio.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject

/**
 * @author SooStrator1136
 */
@Serializable
data class Airport(
    val name: String,
    val iata: String,
    val icao: String,
    @SerialName("lat") val latitude: Float,
    @SerialName("lon") val longitude: Float,
    val country: String,
    @SerialName("alt") val altitude: Int
) {

    @Serializable
    data class Details(val timezone: Timezone, val visible: Boolean, val website: String?)

    val details by lazy {
        runBlocking {
            val json = Json { ignoreUnknownKeys = true }
            json.decodeFromJsonElement<Details>(
                json.parseToJsonElement(
                    FlightRadarAPI.client.get(Core.airportDataUrl.replaceFirst("{}", iata)).bodyAsText()
                ).jsonObject["details"]!!.jsonObject
            )
        }
    }

}