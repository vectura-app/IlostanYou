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
            val description = it.selectFirst("div.well")?.text().takeIf { d -> d != null }
            val (displayName, manufacturerTypeName) = parseVehicleTypeNameAndDescription(
                name = it.selectFirst("span.seria")!!.text(),
                description = description
            )

            val seriesList = it.select("a.kat").map { series ->
                val idRegex = Regex("""^index.php\?nav=serie&typ=$categoryId&seria=(\d+)""")
                val photoRegex = Regex("""^foto/(\d+).""")
                val photoUrl = series.selectFirst("img")!!.attr("src")

                VehiclesTypes.SeriesEntry(
                    id = idRegex.find(series.attr("href"))!!.groupValues[1].toInt(),
                    name = series.selectFirst("button")!!.ownText().trimQuotes().trim(),
                    photoId = photoRegex.find(photoUrl)!!.groupValues[1].toInt()
                )
            }

            VehiclesTypes(
                name = displayName.takeIf { name ->
                    seriesList.none { series -> series.name == name }
                },
                manufacturerTypeName = manufacturerTypeName,
                description = description,
                series = seriesList
            )
        }

private fun parseVehicleTypeNameAndDescription(
    name: String,
    description: String?
): Pair<String, String?> {
    val descriptionDisplayNameRegex = Regex("""&quot;([a-zA-Z0-9\s]+)&quot;|z\srodziny\s([a-zA-Z0-9\s]+?)[,.]|Rodzina:\s([a-zA-Z0-9\s]+?)[,.]""")
    val subDisplayNameRegex = Regex("""\s\[([a-zA-Z0-9\s]+)]""")

    val manufacturerTypeNameRegex = Regex("""(?:[Tt]yp|[Ss]eria|[Oo]znaczeni[ae](?:\sfabryczne)?):?\s([a-zA-Z0-9\s/.-]+?),\s""")

    val displayName = description?.let { descriptionDisplayNameRegex.find(it)?.groupValues[1] }
        ?: subDisplayNameRegex.find(name)?.groupValues[1]
        ?: name

    val manufacturerTypeName = description?.let { manufacturerTypeNameRegex.find(it)?.groupValues[1] }

    return displayName to manufacturerTypeName
}