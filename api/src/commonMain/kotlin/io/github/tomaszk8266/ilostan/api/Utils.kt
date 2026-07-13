package io.github.tomaszk8266.ilostan.api

import com.fleeksoft.ksoup.Ksoup
import com.fleeksoft.ksoup.nodes.Element
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.utils.io.charsets.Charset
import kotlinx.datetime.LocalDate

val client = HttpClient()

internal suspend fun HttpClient.getAndParse(
    url: String,
    fallbackCharset: Charset = Charsets.UTF_8
) = Ksoup.parse(get(url).bodyAsText(fallbackCharset)).body()

internal fun parseSections(root: Element): Map<String, Element> {
    val result = mutableMapOf<String, Element>()

    var pendingTitle: String? = null

    for (child in root.children()) {
        when {
            child.tagName() == "h4" -> {
                // capture title for the next relevant content block
                pendingTitle = child.text().trim()
            }
            child.tagName() == "div" && child.id() == "myDIV" && pendingTitle != null -> {
                result[pendingTitle] = child
                pendingTitle = null
            }
            else -> pendingTitle = null
        }
    }

    return result
}

internal fun String.parseDate() = try {
    LocalDate.parse(replace("**", "01").replace("xx", "01"))
} catch (_: Exception) { null }

internal fun String.trimQuotes() = Regex("""^"?(.*?)"?$""").find(this)!!.groupValues[1]