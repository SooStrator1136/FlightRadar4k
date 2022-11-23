package dev.soostrator.flightradar24

import kotlinx.serialization.json.jsonObject
import org.junit.jupiter.api.Test

/**
 * I definitely love these
 *
 * @author SooStrator1136
 */
internal class FlightRadarApiTest {

    @Test
    fun getZonesJsonTest() {
        val zones = FlightRadarApiBlocking.getZonesJson()
        assert(zones.isNotEmpty())
        val zone = (zones[zones.keys.last()] ?: return).jsonObject
        assert(zone.containsKey("tl_y"))
        assert(zone.containsKey("tl_x"))
        assert(zone.containsKey("br_y"))
        assert(zone.containsKey("br_x"))
    }

    @Test
    fun getZonesTest() {
        assert(FlightRadarApiBlocking.getZones().isNotEmpty())
    }

    @Test
    fun getFlightsTest() {
        assert(FlightRadarApiBlocking.getFlights("DAL", null).isNotEmpty())
        assert(FlightRadarApiBlocking.getFlights(null, FlightRadarApiBlocking.getZones()[0]).isNotEmpty())
        assert(FlightRadarApiBlocking.getFlights(null, null).isNotEmpty())
    }

    @Test
    fun getAirlinesTest() {
        assert(FlightRadarApiBlocking.getAirlines().size > 100)
    }

    @Test
    fun getAirlineLogoTest() {
        assert(FlightRadarApiBlocking.getAirlineLogo("WN", "SWA") != null)
    }

    @Test
    fun getAirportTest() {
        assert(FlightRadarApiBlocking.getAirports().size > 100)
    }

    @Test
    fun getAirportsTest() {
        assert(FlightRadarApiBlocking.getAirports().size > 100)
    }

    @Test
    fun getCountryFlagTest() {
        assert(FlightRadarApiBlocking.getCountryFlag("United States") != null)
    }

}