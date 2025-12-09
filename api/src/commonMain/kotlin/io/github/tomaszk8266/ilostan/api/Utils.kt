package io.github.tomaszk8266.ilostan.api

import com.fleeksoft.ksoup.nodes.Element
import kotlinx.datetime.LocalDate

fun parseSections(root: Element): Map<String, Element> {
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

fun String.parseDate() = try {
    LocalDate.parse(replace("**", "01").replace("xx", "01"))
} catch (_: Exception) { null }

fun String.trimQuotes() = Regex("""^"?(.*?)"?$""").find(this)!!.groupValues[1]