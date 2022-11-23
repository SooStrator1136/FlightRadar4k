package dev.soostrator.flightradar24

import kotlinx.coroutines.runBlocking
import java.awt.image.BufferedImage

/**
 * Provides blocking wrapper calls to [FlightRadarAPI], meant for uses in languages other than kotlin.
 *
 * @see FlightRadarAPI
 * @author SooStrator1136
 */
object FlightRadarApiBlocking {

    /**
     * Wrapper for [FlightRadarAPI.realtimeTrackerConfig] for convenience purpose.
     */
    @JvmStatic
    fun getRealtimeTrackerConfig() = FlightRadarAPI.realtimeTrackerConfig

    /**
     * Blocking wrapper for [FlightRadarAPI.getFlights].
     */
    @JvmStatic
    fun getFlights(airlineIcao: String?, bounds: Zone?) = runBlocking {
        FlightRadarAPI.getFlights(airlineIcao, bounds)
    }

    /**
     * Blocking wrapper for [FlightRadarAPI.getAirlines].
     */
    @JvmStatic
    fun getAirlines() = runBlocking {
        FlightRadarAPI.getAirlines()
    }

    /**
     * Blocking wrapper for [FlightRadarAPI.getAirlineLogo].
     */
    @JvmStatic
    fun getAirlineLogo(code: String, icao: String): BufferedImage? = runBlocking {
        FlightRadarAPI.getAirlineLogo(code, icao)
    }

    /**
     * Blocking wrapper for [FlightRadarAPI.getAirports].
     */
    @JvmStatic
    fun getAirports() = runBlocking {
        FlightRadarAPI.getAirports()
    }

    /**
     * Blocking wrapper for [FlightRadarAPI.getCountryFlag].
     */
    @JvmStatic
    fun getCountryFlag(country: String): BufferedImage? = runBlocking {
        FlightRadarAPI.getCountryFlag(country)
    }

    /**
     * Blocking wrapper for [FlightRadarAPI.getZonesJson].
     */
    @JvmStatic
    fun getZonesJson() = runBlocking {
        FlightRadarAPI.getZonesJson()
    }

    /**
     * Blocking wrapper for [FlightRadarAPI.getZones].
     */
    @JvmStatic
    fun getZones(): List<Zone> = runBlocking {
        FlightRadarAPI.getZones()
    }

}