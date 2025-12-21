package io.github.tomaszk8266.ilostan.api.extractors

import com.fleeksoft.ksoup.Ksoup
import io.github.tomaszk8266.ilostan.api.client
import io.github.tomaszk8266.ilostan.api.parseDate
import io.github.tomaszk8266.ilostan.api.parseSections
import io.github.tomaszk8266.ilostan.api.trimQuotes
import io.github.tomaszk8266.ilostan.api.types.Photo
import io.github.tomaszk8266.ilostan.api.types.Vehicle
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

suspend fun getAndExtractVehicle(id: Int) =
    Ksoup.parse(client.get("https://ilostan.forumkolejowe.pl/index.php?nav=lok&id=$id")
        .bodyAsText(Charsets.UTF_8))
        .body().selectFirst("div.main > div.text:nth-child(8)")!!.let { content ->
            val header = content.selectFirst("div.container_fluid")!!
            val sections = parseSections(content)

            Vehicle(
                id = id,
                name = header.selectFirst("div.row:first-child > div:first-child > h1 > b")!!.text(),
                factoryNumber = header.selectFirst("div.row:nth-child(3) > div:first-child > h4")?.ownText()
                    ?.takeIf { it.isNotBlank() }
                    ?.trimQuotes(),
                manufacturingYear = header.selectFirst("div.row:nth-child(3) > div:nth-child(2) > h4")!!.ownText().toInt(),
                statusHistory = sections["Historia statusów"]?.select("tbody > tr")?.map {
                    val detailsColumn = it.selectFirst("td:nth-child(2)")?.textNodes()

                    Vehicle.StatusEntry(
                        status = when(detailsColumn?.getOrNull(0)?.text()) {
                            else -> Vehicle.Status.Other
                        },
                        comment = detailsColumn?.getOrNull(1)?.text(),
                        date = it.selectFirst("td:nth-child(1)")?.text()?.parseDate()!!
                    )
                }.orEmpty(),
                eventHistory = sections["Historia zdarzeń"]?.select("tbody > tr")?.map {
                    Vehicle.EventEntry(
                        event = it.selectFirst("td:nth-child(3)")!!.text(),
                        description = it.selectFirst("td:nth-child(2)")!!.text(),
                        date = it.selectFirst("td:first-child")?.text()?.parseDate()!!
                    )
                }.orEmpty(),
                ownershipHistory = sections["Historia przydziałów"]?.select("tbody > tr")?.map {
                    val owner = it.selectFirst("td:nth-child(2)")?.text()
                        ?.takeIf { t -> t != "-----------" }
                    val carrier = it.selectFirst("td:nth-child(3)")?.text()

                    Vehicle.OwnershipEntry(
                        owner = owner ?: carrier!!,
                        carrier = carrier,
                        transferDate = it.selectFirst("td:nth-child(1)")?.text()?.parseDate()
                    )
                }.orEmpty(),
                repairHistory = sections["Historia napraw"]?.select("tbody > tr")?.map {
                    Vehicle.RepairEntry(
                        finishDate = it.selectFirst("td:nth-child(1)")?.text()?.parseDate()!!,
                        zntk = it.selectFirst("td:nth-child(2)")!!.text(),
                        colors = it.selectFirst("td:nth-child(3)")!!.text(),
                        type = it.selectFirst("td:nth-child(4)")!!.text()
                    )
                }.orEmpty(),
                photos = sections["Fotografie"]?.select("tbody > tr")?.map {
                    val details = it.selectFirst("td.tab_koment")
                    val relativeUrl = it.selectFirst("td.foto > a")!!.attr("href")
                    val regex = Regex("""^index.php\?nav=foto&id=(\d+)""")

                    Photo(
                        id = regex.find(relativeUrl)!!.groupValues[1].toInt(),
                        date = details?.selectFirst("h3:first-child")?.ownText()
                            ?.trimQuotes()?.trim()
                            ?.parseDate()!!,
                        description = details.selectFirst("p")!!.text()
                    )
                }.orEmpty()
            )
        }