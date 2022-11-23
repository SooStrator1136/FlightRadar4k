package dev.soostrator.flightradar24

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author SooStrator1136
 */
@Serializable
data class Timezone(
    val name: String,
    val offset: Long,
    val offsetHours: String,
    @SerialName("abbr") val abbreviation: String,
    @SerialName("abbrName") val abbreviationName: String,
    val isDst: Boolean
)