package com.arran4.txtar

class TxtarEntry(headerText: String, contentText: String) {
    var filename: String = ""
    var contentText: String = ""

    init {
        this.filename = extractFilename(headerText)
        this.contentText = contentText
    }

    val headerText: String
        get() = "-- $filename --\n"

    private fun extractFilename(headerText: String): String {
        var text = headerText.trim()
        if (text.startsWith("-- ") && text.endsWith(" --")) {
            return text.substring(3, text.length - 3).trim()
        } else {
            return text
        }
    }

    fun clone(): TxtarEntry {
        return TxtarEntry(headerText, contentText)
    }
}
