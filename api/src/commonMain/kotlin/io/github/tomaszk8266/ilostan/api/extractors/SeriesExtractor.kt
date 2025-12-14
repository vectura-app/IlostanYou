package io.github.tomaszk8266.ilostan.api.extractors

import com.fleeksoft.ksoup.Ksoup
import io.github.tomaszk8266.ilostan.api.client
import io.github.tomaszk8266.ilostan.api.trimQuotes
import io.github.tomaszk8266.ilostan.api.types.Vehicle
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

suspend fun getAndExtractSeries(id: Int) =
    Ksoup.parse(client.get("https://ilostan.forumkolejowe.pl/index.php?nav=serie&seria=$id")
        .bodyAsText(Charsets.UTF_8))
        .body().select("div.main > div.text:nth-child(8) > div tbody > tr.wiersz").map {
            val nameLink = it.selectFirst("td:first-child > a")!!
            val idRegex = Regex("""^index.php\?nav=lok&id=(\d+)""")
            val id = idRegex.find(nameLink.attr("href"))!!.groupValues[1].toInt()

            val manufacturingData = it.selectFirst("td:first-child")
                ?.textNodes()[0]!!.text()
                .trimQuotes().trim()
            val manufacturingDataRegex = Regex("""([a-zA-Z0-9-_]+) (?:\((\d{4})\))?""")
            val manufacturingDataExtracted = manufacturingDataRegex.find(manufacturingData)!!.groupValues

            val ownershipTableCell = it.selectFirst("td:nth-child(2)")!!

            Vehicle(
                id = id,
                name = nameLink.text(),
                factoryNumber = manufacturingDataExtracted[1],
                manufacturingYear = manufacturingDataExtracted[2].toInt(),
                statusHistory = emptyList(),
                eventHistory = emptyList(),
                ownershipHistory = listOf(
                    Vehicle.OwnershipEntry(
                        transferDate = null,
                        owner = ownershipTableCell.selectFirst("a")!!.text(),
                        carrier = ownershipTableCell.ownText().trimQuotes(),
                    )
                ),
                repairHistory = emptyList(),
                photos = emptyList()
            )
        }