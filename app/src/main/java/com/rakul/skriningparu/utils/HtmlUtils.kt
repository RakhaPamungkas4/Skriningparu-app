package com.rakul.skriningparu.utils

import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import androidx.core.text.HtmlCompat

const val OL_TAG = "ordered"
const val UL_TAG = "unordered"
const val LI_TAG = "listitem"


object HtmlUtils {

    fun String?.setHtmlHandlersText(isNestedList: Boolean = false): Spanned {
        this ?: return SpannableString("")

        val finalText = if (isNestedList) {
            val nestedText =
                this.replace("(?i)</li>(?i)<ul[^>]*>(?i)<li[^>]*>".toRegex(), "<ul><li>")
            nestedText.replace("(?i)</li>(?i)</ul>(?i)<li[^>]*>".toRegex(), "</li></ul></li><li>")
        } else {
            this
        }

        // Replace tags with unknown ones so ListTagHandler will be used
        val formattedHtml = finalText
            .replace("(?i)<ul[^>]*>".toRegex(), "<$UL_TAG>")
            .replace("(?i)</ul>".toRegex(), "</$UL_TAG>")
            .replace("(?i)<ol[^>]*>".toRegex(), "<$OL_TAG>")
            .replace("(?i)</ol>".toRegex(), "</$OL_TAG>")
            .replace("(?i)<li[^>]*>".toRegex(), "<$LI_TAG>")
            .replace("(?i)</li>".toRegex(), "</$LI_TAG>")
            .replace("(?i)</p>".toRegex(), "</p>")

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(formattedHtml, Html.FROM_HTML_MODE_COMPACT, null, ListTagHandler())
        } else {
            Html.fromHtml(formattedHtml)
        }
    }

    fun String.fromHtml(): Spanned {
        return HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    fun getQueryFromUrl(url: String, query: String): String? {
        val uri = Uri.parse(url)
        return uri.getQueryParameter(query)
    }

}