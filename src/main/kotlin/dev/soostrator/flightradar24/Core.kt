package dev.soostrator.flightradar24

/**
 * Class used to store core information used internaly by the API.
 *
 * @author SooStrator1136
 */
@Suppress("SpellCheckingInspection")
internal object Core {

    private const val cdnFlightradarBaseUrl = "https://cdn.flightradar24.com"
    private const val flightradarBaseUrl = "https://www.flightradar24.com"
    private const val dataLiveBaseUrl = "https://data-live.flightradar24.com"
    private const val dataCloudBaseUrl = "https://data-cloud.flightradar24.com"

    const val realtimeFlightTrackerDataUrl = "$dataCloudBaseUrl/zones/fcgi/feed.js"
    const val flightDataUrl = "$dataLiveBaseUrl/clickhandler/?flight="

    const val airportDataUrl = "$flightradarBaseUrl/airports/traffic-stats/?airport={}"
    const val airportsDataUrl = "$flightradarBaseUrl/_json/airports.php"

    const val airlinesDataUrl = "$flightradarBaseUrl/_json/airlines.php"

    const val zonesDataUrl = "$flightradarBaseUrl/js/zones.js.php"

    const val countryFlagUrl = "$flightradarBaseUrl/static/images/data/flags-small/{}.gif"

    fun getAirlineLogoUrl(code: String, icao: String): String {
        return "$cdnFlightradarBaseUrl/assets/airlines/logotypes/${code}_${icao}.png"
    }

    fun getAlternativeAirlineLogoUrl(icao: String): String {
        return "$flightradarBaseUrl/static/images/data/operators/${icao}_logo0.png"
    }

    val unneededProperties = arrayOf(
        "stats",
        "version",
        "full_count"
    )

}