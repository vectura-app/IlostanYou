package io.github.tomaszk8266.ilostan.api.extractors

import com.fleeksoft.ksoup.Ksoup
import io.github.tomaszk8266.ilostan.api.client
import io.github.tomaszk8266.ilostan.api.trimQuotes
import io.github.tomaszk8266.ilostan.api.types.VehiclesTypes
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

suspend fun getAndExtractVehiclesTypes(categoryId: Int) =
    Ksoup.parse(client.get("https://ilostan.forumkolejowe.pl/index.php?nav=trakcje&typ=$categoryId")
        .bodyAsText(Charsets.UTF_8))
        .body().select("div.main > div.text:nth-child(7) > div:nth-child(6) tr.wiersz > td.cat").map {
            VehiclesTypes(
                name = it.selectFirst("span.seria")!!.text(),
                description = it.selectFirst("div.well")?.text(),
                series = it.select("a.kat").map { series ->
                    val idRegex = Regex("""^index.php\?nav=serie&typ=$categoryId&seria=(\d+)""")
                    val photoRegex = Regex("""^foto/(\d+).""")
                    val photoUrl = series.selectFirst("img")!!.attr("src")

                    VehiclesTypes.SeriesEntry(
                        id = idRegex.find(series.attr("href"))!!.groupValues[1].toInt(),
                        name = series.selectFirst("button")!!.ownText().trimQuotes().trim(),
                        photoId = photoRegex.find(photoUrl)!!.groupValues[1].toInt()
                    )
                }
            )
        }
