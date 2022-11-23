package dev.soostrator.flightradar24

import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.*
import println

/**
 * Class describing a flight.
 *
 * Instances should be gathered using [FlightRadarAPI.getFlights]
 *
 * @see details
 * @author SooStrator1136
 */
@Suppress("MemberVisibilityCanBePrivate")
class Flight internal constructor(val id: String, info: JsonArray) {

    val icao: String
    val latitude: Float
    val longitude: Float
    val heading: Int
    val altitudeFT: Int
    val altitudeM: Float
    val groundSpeedKts: Int
    val groundSpeedKmh: Float
    val aircraftCode: String
    val registration: String
    val time: Long
    var originAirportIata: String
    var destinationAirportIata: String
    val number: String
    val callSign: String
    val airlineIcao: String

    init {
        fun getChecked(index: Int) = if (index >= info.size) {
            Int.MAX_VALUE.toString()
        } else info[index]!!.jsonPrimitive.content.ifBlank { Int.MAX_VALUE.toString() }

        icao = getChecked(0)
        latitude = getChecked(1).toFloat()
        longitude = getChecked(2).toFloat()
        heading = getChecked(3).toInt()
        altitudeFT = getChecked(4).toInt()
        altitudeM = (altitudeFT * 0.3048).toFloat()
        groundSpeedKts = getChecked(5).toInt()
        groundSpeedKmh = (groundSpeedKts * 1.852).toFloat()
        aircraftCode = getChecked(8)
        registration = getChecked(9)
        time = getChecked(10).toLong()
        originAirportIata = getChecked(11)
        destinationAirportIata = getChecked(12)
        number = getChecked(13)
        callSign = getChecked(16)
        airlineIcao = getChecked(18)
    }

    @Serializable
    data class Details(
        val aircraft: Aircraft,
        val airline: Airline,
        val airport: Airports,
        val availability: Array<String>,
        val time: Times,
        val trail: Array<Trail>,
        val firstTimestamp: Long
    ) {

        @Serializable
        data class Aircraft(
            val model: Model,
            val countryId: Int,
            val registration: String,
            val images: Images,
            val hex: String
        ) {

            @Serializable
            data class Model(val code: String, val text: String)

            @Serializable
            data class Images(
                val thumbnails: Array<Thumbnail>,
                val medium: Array<Thumbnail>,
                val large: Array<Thumbnail>
            ) {

                @Serializable
                data class Thumbnail(val src: String, val link: String, val copyright: String, val source: String)

                override fun equals(other: Any?): Boolean {
                    if (this === other) return true
                    if (javaClass != other?.javaClass) return false

                    other as Images

                    if (!thumbnails.contentEquals(other.thumbnails)) return false

                    return true
                }

                override fun hashCode(): Int {
                    return thumbnails.contentHashCode()
                }

            }

        }

        @Serializable
        data class Airline(val name: String, val short: String, val code: Code, val url: String) {

            @Serializable
            data class Code(val iata: String, val icao: String)

        }

        @Serializable
        data class Airports(val origin: Airport, val destination: Airport, val real: Airport?) {

            @Serializable
            data class Airport(
                val name: String,
                val code: Code,
                val position: Position,
                val timezone: Timezone,
                val website: String,
                val info: Info
            ) {

                @Serializable
                data class Code(val iata: String, val icao: String)

                @Serializable
                data class Position(
                    val latitude: Float,
                    val longitude: Float,
                    val altitude: Int,
                    val country: Country
                ) {

                    @Serializable
                    data class Country(val id: Int, val name: String, val code: String, val codeLong: String)

                }

                @Serializable
                data class Info(val terminal: String, val baggage: String?, val gate: String)

            }

        }

        @Serializable
        data class Times(
            val scheduled: FlightTime,
            val real: FlightTime,
            val estimated: FlightTime,
            val historical: Historical
        ) {

            @Serializable
            data class FlightTime(val departure: Long?, val arrival: Long?)

            @Serializable
            data class Historical(val flighttime: Int, val delay: Int)

        }

        @Serializable
        data class Trail(val lat: Float, val lng: Float, val alt: Int, val spd: Int, val ts: Long, val hd: Int)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Details

            if (aircraft != other.aircraft) return false
            if (airline != other.airline) return false
            if (airport != other.airport) return false
            if (!availability.contentEquals(other.availability)) return false
            if (time != other.time) return false
            if (!trail.contentEquals(other.trail)) return false
            if (firstTimestamp != other.firstTimestamp) return false

            return true
        }

        override fun hashCode(): Int {
            var result = aircraft.hashCode()
            result = 31 * result + airline.hashCode()
            result = 31 * result + airport.hashCode()
            result = 31 * result + availability.contentHashCode()
            result = 31 * result + time.hashCode()
            result = 31 * result + trail.contentHashCode()
            result = 31 * result + firstTimestamp.hashCode()
            return result
        }

    }

    /**
     * The details of the flight.
     */
    val details by lazy {
        runBlocking {
            @Suppress("JSON_FORMAT_REDUNDANT") //lazy delegate so this doesn't matter
            Json {
                ignoreUnknownKeys = true
                useAlternativeNames = false
            }.decodeFromString<Details>(
                FlightRadarAPI.client.get(Core.flightDataUrl + id).bodyAsText()
            )
        }
    }

    override fun toString(): String {
        return "Flight(id='$id', icao='$icao', latitude=$latitude, longitude=$longitude, heading=$heading, altitudeFT=$altitudeFT, altitudeM=$altitudeM, groundSpeedKts=$groundSpeedKts, groundSpeedKmh=$groundSpeedKmh, aircraftCode='$aircraftCode', registration='$registration', time=$time, originAirportIata='$originAirportIata', destinationAirportIata='$destinationAirportIata', number='$number', callSign='$callSign', airlineIcao='$airlineIcao')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Flight

        if (id != other.id) return false
        if (icao != other.icao) return false
        if (latitude != other.latitude) return false
        if (longitude != other.longitude) return false
        if (heading != other.heading) return false
        if (altitudeFT != other.altitudeFT) return false
        if (altitudeM != other.altitudeM) return false
        if (groundSpeedKts != other.groundSpeedKts) return false
        if (groundSpeedKmh != other.groundSpeedKmh) return false
        if (aircraftCode != other.aircraftCode) return false
        if (registration != other.registration) return false
        if (time != other.time) return false
        if (originAirportIata != other.originAirportIata) return false
        if (destinationAirportIata != other.destinationAirportIata) return false
        if (number != other.number) return false
        if (callSign != other.callSign) return false
        if (airlineIcao != other.airlineIcao) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + icao.hashCode()
        result = 31 * result + latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + heading
        result = 31 * result + altitudeFT
        result = 31 * result + altitudeM.hashCode()
        result = 31 * result + groundSpeedKts
        result = 31 * result + groundSpeedKmh.hashCode()
        result = 31 * result + aircraftCode.hashCode()
        result = 31 * result + registration.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + originAirportIata.hashCode()
        result = 31 * result + destinationAirportIata.hashCode()
        result = 31 * result + number.hashCode()
        result = 31 * result + callSign.hashCode()
        result = 31 * result + airlineIcao.hashCode()
        return result
    }

}