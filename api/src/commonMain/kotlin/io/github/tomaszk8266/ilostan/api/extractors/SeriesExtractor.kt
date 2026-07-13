package io.github.tomaszk8266.ilostan.api.extractors

import io.github.tomaszk8266.ilostan.api.client
import io.github.tomaszk8266.ilostan.api.getAndParse
import io.github.tomaszk8266.ilostan.api.parseDate
import io.github.tomaszk8266.ilostan.api.trimQuotes
import io.github.tomaszk8266.ilostan.api.types.Photo
import io.github.tomaszk8266.ilostan.api.types.Series
import io.github.tomaszk8266.ilostan.api.types.Vehicle

suspend fun getAndExtractSeries(id: Int) = Series(
    vehicles = client.getAndParse("https://ilostan.forumkolejowe.pl/?nav=serie&seria=$id")
        .select("div.main > div.text:nth-child(7) > div tbody > tr.wiersz").mapNotNull {
            val nameLink = it.selectFirst("td:first-child > a")!!
            val idRegex = Regex("""^index\.php\?nav=lok&id=(\d+)""")
            val id = idRegex.find(nameLink.attr("href"))!!.groupValues[1].toInt()

            val manufacturingData = it.selectFirst("td:first-child")
                ?.textNodes()[0]!!.text()
                .trimQuotes().trim()
            val manufacturingDataRegex = Regex("""(?:([a-zA-Z0-9-/_\s]+)\s)?(?:\((\d{4})\))?""")
            val manufacturingDataExtracted = manufacturingDataRegex.find(manufacturingData)?.groupValues
                ?: return@mapNotNull null

            val ownershipTableCell = it.selectFirst("td:nth-child(2)")

            val owner = ownershipTableCell?.ownText()?.trimQuotes()
            val carrier = ownershipTableCell?.selectFirst("a")?.text()

            Vehicle(
                id = id,
                name = nameLink.text(),
                factoryNumber = manufacturingDataExtracted[1],
                manufacturingYear = manufacturingDataExtracted[2].toIntOrNull()
                    ?: return@mapNotNull null,
                statusHistory = emptyList(),
                eventHistory = emptyList(),
                ownershipHistory = listOf(
                    Vehicle.OwnershipEntry(
                        transferDate = null,
                        owner = owner ?: carrier!!,
                        carrier = carrier.takeIf { owner != null }
                    )
                ),
                repairHistory = emptyList(),
                photos = emptyList()
            )
        },
    photos = client.getAndParse("https://ilostan.forumkolejowe.pl/?nav=nowe_foto&seria=$id")
        .select("div.main > div.text:nth-child(8) tbody > tr:not(.naglowektab)").map { row ->
            Photo(
                id = Regex("""^foto/(\d+)\.""")
                    .find(row.select("td.foto img").attr("src"))!!
                    .groupValues[1].toInt(),
                date = row.selectFirst("td.tab_koment b")?.text()?.parseDate()!!,
                description = row.selectFirst("td.tab_koment")!!.textNodes()[0].text()
                    .substringAfter(" - ")
            )
        }
)
