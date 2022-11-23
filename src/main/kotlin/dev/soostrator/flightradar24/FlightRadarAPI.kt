package dev.soostrator.flightradar24

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.content.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.serialization.json.*
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

/**
 * The API object
 *
 * @see FlightRadarApiBlocking
 * @author SooStrator1136
 */
object FlightRadarAPI {

    internal val client = HttpClient(CIO)

    /**
     * Tracker config used in [getFlights].
     */
    @Suppress("SpellCheckingInspection")
    val realtimeTrackerConfig = mutableMapOf(
        "faa" to "1",
        "satellite" to "1",
        "mlat" to "1",
        "flarm" to "1",
        "adsb" to "1",
        "gnd" to "1",
        "air" to "1",
        "vehicles" to "1",
        "estimated" to "1",
        "maxage" to "14400",
        "gliders" to "100",
        "stats" to "1",
        "limit" to "5000"
    )

    /**
     * Gets all flights inside the [bounds] and matching the [airlineIcao] using the [realtimeTrackerConfig],
     * if both aren't set you will still get 1500 seemingly random flights.
     *
     * @see getZones
     * @see getAirlines
     * @return The flights found matching your parameters.
     */
    suspend fun getFlights(airlineIcao: String? = null, bounds: Zone? = null): List<Flight> {
        val flights = ArrayList<Flight>()

        val flightsJson = Json.parseToJsonElement(
            client.get(
                buildString {
                    append(Core.realtimeFlightTrackerDataUrl)

                    var index = 0
                    realtimeTrackerConfig.forEach { (name, value) ->
                        append(if (index == 0) "?" else "&")
                        append("$name=$value")
                        index++
                    }

                    if (airlineIcao != null) {
                        append("&airline=$airlineIcao")
                    }
                    if (bounds != null) {
                        append("&bounds=${bounds.tl_y}%2C${bounds.br_y}%2C${bounds.tl_x}%2C${bounds.br_x}")
                    }
                }
            ).bodyAsText()
        ).jsonObject.toMutableMap()

        Core.unneededProperties.forEach {
            flightsJson.remove(it)
        }

        flightsJson.keys.forEach {
            flights.add(Flight(it, (flightsJson[it] ?: return@forEach).jsonArray))
        }

        return flights
    }

    /**
     * Gets all available airlines.
     *
     * @see getFlights
     * @see getAirlineLogo
     * @return a [JsonArray] filled with information about airlines.
     */
    suspend fun getAirlines() = Json.parseToJsonElement(
        client.get(Core.airlinesDataUrl).bodyAsText()
    ).jsonObject["rows"]!!.jsonArray

    /**
     * Gets the logo of a given airline.
     *
     * @see getAirlines
     * @return the logo of the airline in form of a [BufferedImage], null in case of no connection or no image being found,
     */
    suspend fun getAirlineLogo(code: String, icao: String): BufferedImage? {
        runCatching {
            return ImageIO.read(client.get(Core.getAirlineLogoUrl(code, icao)).readBytes().inputStream())
        }.onFailure {
            runCatching {
                return ImageIO.read(client.get(Core.getAlternativeAirlineLogoUrl(icao)).readBytes().inputStream())
            }
        }

        return null
    }

    /**
     * Gets all available airports in a [List].
     *
     * @return a [List] of filled with [Airport] objects.
     */
    suspend fun getAirports() = Json.parseToJsonElement(
        client.get(Core.airportsDataUrl).bodyAsText()
    ).jsonObject["rows"]!!.jsonArray.map { Json.decodeFromJsonElement<Airport>(it) }

    /**
     * Gets the flag of a country by its name.
     *
     * @return the countries flag in form of a [BufferedImage], null in case of no connection.
     */
    suspend fun getCountryFlag(country: String): BufferedImage? {
        runCatching {
            return ImageIO.read(
                client.get(
                    Core.countryFlagUrl.replaceFirst("{}", country.lowercase().replace(' ', '-'))
                ).readBytes().inputStream()
            )
        }

        return null
    }

    /**
     * Gets all available zones.
     *
     * @see getZones
     * @return zones as [JsonObject].
     */
    suspend fun getZonesJson() = Json.parseToJsonElement(
        client.get(Core.zonesDataUrl).bodyAsText()
    ).jsonObject

    /**
     * Gets all available zones.
     *
     * @see getZonesJson
     * @return a [List] of [Zone] objects.
     */
    suspend fun getZones(): List<Zone> {
        val zonesJson = getZonesJson()

        val zones = ArrayList<Zone>()

        zonesJson.keys.forEach outer@{ zoneName ->
            if (zoneName == "version") return@outer

            val currZone = (zonesJson[zoneName] ?: return@outer).jsonObject
            val zone = Zone(
                zoneName,
                (currZone["tl_y"] ?: return@outer).jsonPrimitive.float,
                (currZone["tl_x"] ?: return@outer).jsonPrimitive.float,
                (currZone["br_y"] ?: return@outer).jsonPrimitive.float,
                (currZone["br_x"] ?: return@outer).jsonPrimitive.float,
            )

            @Suppress("SpellCheckingInspection")
            if (currZone.containsKey("subzones")) {
                val innerZonesJson = (currZone["subzones"] ?: return@outer).jsonObject
                innerZonesJson.keys.forEach {
                    val innerZoneJson = (innerZonesJson[it] ?: return@forEach).jsonObject
                    zone.subZones.add(
                        Zone(
                            it,
                            (innerZoneJson["tl_y"] ?: return@forEach).jsonPrimitive.float,
                            (innerZoneJson["tl_x"] ?: return@forEach).jsonPrimitive.float,
                            (innerZoneJson["br_y"] ?: return@forEach).jsonPrimitive.float,
                            (innerZoneJson["br_x"] ?: return@forEach).jsonPrimitive.float
                        )
                    )
                }
            }

            zones.add(zone)
        }

        return zones
    }

}