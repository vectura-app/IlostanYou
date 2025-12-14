package io.github.tomaszk8266.ilostan.api.extractors

import com.fleeksoft.ksoup.Ksoup
import io.github.tomaszk8266.ilostan.api.client
import io.github.tomaszk8266.ilostan.api.trimQuotes
import io.github.tomaszk8266.ilostan.api.types.Category
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText

suspend fun getAndExtractCategories() =
    Ksoup.parse(client.get("https://ilostan.forumkolejowe.pl/index.php")
        .bodyAsText(Charsets.UTF_8))
        .body().select("div.main > div.text:nth-child(11) tbody > tr.wiersz > td.cat").map {
            val categoryLink = it.selectFirst("a")
            val relativeUrl = categoryLink?.attr("href")!!
            val regex = Regex("""^index.php\?nav=trakcje&typ=(\d+)""")

            Category(
                id = regex.find(relativeUrl)?.groupValues[1]!!.toInt(),
                name = categoryLink.text(),
                description = it.selectFirst("span.opis")?.text()?.trimQuotes()
            )
        }