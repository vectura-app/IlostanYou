package io.github.tomaszk8266.ilostan.api.extractors

import io.github.tomaszk8266.ilostan.api.client
import io.github.tomaszk8266.ilostan.api.getAndParse
import io.github.tomaszk8266.ilostan.api.types.Photo
import kotlin.text.Regex

suspend fun getAndExtractRecentPhots() = client.getAndParse("https://ilostan.forumkolejowe.pl")
    .select("div.text:has(span.nav:contains(Ostatnio dodane fotografie)) tbody td[align=\"center\"]").mapNotNull {
        val vehicleNameElement = it.selectFirst("a.kat")!!

        Photo(
            id = Regex("""^index.php\?nav=foto&id=(\d+)""")
                .find(it.selectFirst("a.foto")!!.attr("href"))
                ?.groupValues[1]?.toInt()
                ?: return@mapNotNull null,
            date = null,
            description = vehicleNameElement.text(),
            vehicleId = Regex("""^index.php\?nav=lok&id=(\d+)""")
                .find(vehicleNameElement.attr("href"))
                ?.groupValues[1]?.toIntOrNull()
        )
    }