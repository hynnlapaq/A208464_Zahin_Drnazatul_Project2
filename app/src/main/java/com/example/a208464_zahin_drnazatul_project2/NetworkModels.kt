package com.example.a208464_zahin_drnazatul_project2


import com.google.gson.annotations.SerializedName

// ── REST Countries API response model ───────────────────────────────────────
// Endpoint used: https://restcountries.com/v3.1/name/malaysia
// Free, no API key needed

data class CountryResponse(
    val name: CountryName,
    val population: Long,
    val region: String,
    val subregion: String,
    val capital: List<String>?,
    val currencies: Map<String, Currency>?,
    val languages: Map<String, String>?,
    val flags: CountryFlags,
    @SerializedName("gini") val giniIndex: Map<String, Double>?,
    val area: Double
)

data class CountryName(
    val common: String,
    val official: String
)

data class Currency(
    val name: String,
    val symbol: String
)

data class CountryFlags(
    val png: String,
    val svg: String,
    val alt: String?
)

// ── Simplified UI model (only what we display on screen) ────────────────────
data class MalaysiaStats(
    val population: String,        // e.g. "33.6 million"
    val capital: String,           // "Kuala Lumpur"
    val currency: String,          // "Malaysian Ringgit (MYR)"
    val region: String,            // "Asia"
    val area: String,              // "329,847 km²"
    val languages: String,         // "Malay, English"
    val latestGini: String         // "41.0 (2015)" — income inequality index
)

fun CountryResponse.toMalaysiaStats(): MalaysiaStats {
    val pop = population / 1_000_000.0
    val popStr = "%.1f million".format(pop)

    val currencyEntry = currencies?.values?.firstOrNull()
    val currencyStr = if (currencyEntry != null)
        "${currencyEntry.name} (${currencyEntry.symbol})" else "—"

    val langStr = languages?.values?.take(3)?.joinToString(", ") ?: "—"
    val capStr  = capital?.firstOrNull() ?: "—"
    val areaStr = "%,.0f km²".format(area)

    val giniEntry = giniIndex?.entries?.maxByOrNull { it.key }
    val giniStr   = if (giniEntry != null) "${giniEntry.value} (${giniEntry.key})" else "—"

    return MalaysiaStats(
        population  = popStr,
        capital     = capStr,
        currency    = currencyStr,
        region      = "$region · $subregion",
        area        = areaStr,
        languages   = langStr,
        latestGini  = giniStr
    )
}
