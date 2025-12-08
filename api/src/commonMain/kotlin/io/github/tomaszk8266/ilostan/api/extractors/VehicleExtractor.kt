package io.github.tomaszk8266.ilostan.api.extractors

import com.fleeksoft.ksoup.Ksoup
import io.github.tomaszk8266.ilostan.api.parseDate
import io.github.tomaszk8266.ilostan.api.parseSections
import io.github.tomaszk8266.ilostan.api.trimQuotes
import io.github.tomaszk8266.ilostan.api.types.Vehicle
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

val client = HttpClient()

suspend fun getAndExtractVehicle(id: Int) =
    Ksoup.parse(client.get("https://ilostan.forumkolejowe.pl/index.php?nav=lok&id=$id")
        .bodyAsText(Charsets.UTF_8))
        .body().selectFirst("div.main > div.text:nth-child(8)")!!.let { content ->
            val header = content.selectFirst("div.container_fluid")!!
            val sections = parseSections(content)

            Vehicle(
                name = header.selectFirst("div.row:first-child > div:first-child > h1 > b")!!.text(),
                factoryNumber = header.selectFirst("div.row:nth-child(3) > div:first-child > h4")?.ownText()
                    ?.takeIf { it.isNotBlank() }
                    ?.trimQuotes(),
                manufacturingYear = header.selectFirst("div.row:nth-child(3) > div:nth-child(2) > h4")!!.ownText().toInt(),
                statusHistory = sections["Historia statusów"]?.select("tbody > tr")?.mapNotNull {
                    val detailsColumn = it.selectFirst("td:nth-child(2)")?.textNodes()

                    Vehicle.StatusEntry(
                        status = when(detailsColumn?.getOrNull(0)?.text()) {
                            else -> Vehicle.Status.Other
                        },
                        comment = detailsColumn?.getOrNull(1)?.text(),
                        date = it.selectFirst("td:nth-child(1)")?.text()
                            ?.parseDate() ?: return@mapNotNull null
                    )
                }.orEmpty(),
                eventHistory = sections["Historia zdarzeń"]?.select("tbody > tr")?.mapNotNull {
                    Vehicle.EventEntry(
                        event = it.selectFirst("td:nth-child(3)")?.text() ?: return@mapNotNull null,
                        description = it.selectFirst("td:nth-child(2)")?.text() ?: return@mapNotNull null,
                        date = it.selectFirst("td:first-child")?.text()
                            ?.parseDate() ?: return@mapNotNull null
                    )
                }.orEmpty(),
                ownershipHistory = sections["Historia przydziałów"]?.select("tbody > tr")?.mapNotNull {
                    val owner = it.selectFirst("td:nth-child(2)")?.text()
                        ?.takeIf { t -> t != "-----------" }
                    val carrier = it.selectFirst("td:nth-child(3)")?.text()

                    Vehicle.OwnershipEntry(
                        owner = owner ?: carrier ?: return@mapNotNull null,
                        carrier = carrier ?: return@mapNotNull null,
                        transferDate = it.selectFirst("td:nth-child(1)")?.text()
                            ?.parseDate() ?: return@mapNotNull null
                    )
                }.orEmpty(),
                repairHistory = sections["Historia napraw"]?.select("tbody > tr")?.mapNotNull {
                    Vehicle.RepairEntry(
                        finishDate = it.selectFirst("td:nth-child(1)")?.text()
                            ?.parseDate() ?: return@mapNotNull null,
                        zntk = it.selectFirst("td:nth-child(2)")?.text() ?: return@mapNotNull null,
                        colors = it.selectFirst("td:nth-child(3)")?.text() ?: return@mapNotNull null,
                        type = it.selectFirst("td:nth-child(4)")?.text() ?: return@mapNotNull null
                    )
                }.orEmpty(),
                photos = sections["Fotografie"]?.select("tbody > tr")?.mapNotNull {
                    val details = it.selectFirst("td.tab_koment")
                    val relativeUrl = it.selectFirst("td.foto > a")?.attr("href")
                        ?: return@mapNotNull null
                    val regex = Regex("""index.php\?nav=foto&id=(\d+)""")

                    Vehicle.Photo(
                        date = details?.selectFirst("h3:first-child")?.ownText()
                            ?.trimQuotes()
                            ?.trim()
                            ?.parseDate() ?: return@mapNotNull null,
                        id = regex.find(relativeUrl)?.groupValues[1].toString().toInt(),
                        description = details.selectFirst("p")?.text() ?: return@mapNotNull null
                    )
                }.orEmpty()
            )
        }