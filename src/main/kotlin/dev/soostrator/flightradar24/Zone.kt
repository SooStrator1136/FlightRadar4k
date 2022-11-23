package dev.soostrator.flightradar24

/**
 * @author SooStrator1136
 */
data class Zone(val name: String, val tl_y: Float, val tl_x: Float, val br_y: Float, val br_x: Float) {

    val subZones = ArrayList<Zone>()

}