package io.github.tomaszk8266.ilostan.api.extractors

import io.github.tomaszk8266.ilostan.api.client
import io.github.tomaszk8266.ilostan.api.getAndParse
import io.github.tomaszk8266.ilostan.api.trimQuotes
import io.github.tomaszk8266.ilostan.api.types.Category

suspend fun getAndExtractCategories() = client.getAndParse("https://ilostan.forumkolejowe.pl/index.php")
    .select("div.text:has(span.nav:contains(Tabor trakcyjny)) tbody > tr.wiersz > td.cat").map {
        val categoryLink = it.selectFirst("a")
        val relativeUrl = categoryLink?.attr("href")!!
        val regex = Regex("""^index.php\?nav=trakcje&typ=(\d+)""")

        Category(
            id = regex.find(relativeUrl)?.groupValues[1]!!.toInt(),
            name = categoryLink.text(),
            description = it.selectFirst("span.opis")?.text()?.trimQuotes()
        )
    }