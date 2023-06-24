package com.rakul.skriningparu.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.text.Editable
import android.text.Html
import android.text.Layout
import android.text.Spannable
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import org.xml.sax.XMLReader
import java.util.Stack

class ListTagHandler : Html.TagHandler {

    private val lists = Stack<ListTag>()

    /**
     * Called when the HTML parser reaches an opening or closing tag.
     * We only handle list tags and ignore other tags.
     *
     * <ul> and <ol> tags are pushed to the [lists] stack and popped when the closing tag is reached.
     *
     * <li> tags are handled by the [ListTag] instance corresponding to the parent tag.
     */
    override fun handleTag(opening: Boolean, tag: String, output: Editable, xmlReader: XMLReader) {
        when (tag) {
            UL_TAG -> if (opening) { // handle <ul>
                lists.push(Ul())
            } else { // handle </ul>
                lists.pop()
            }

            OL_TAG -> if (opening) { // handle <ol>
                lists.push(Ol())
            } else { // handle </ol>
                lists.pop()
            }

            LI_TAG -> if (opening) { // handle <li>
                lists.peek().openItem(output)
            } else { // handle </li>
                lists.peek().closeItem(output, indentation = lists.size - 1)
            }
        }
    }

    /**
     * Handler for <li> tags. Subclasses set the bullet appearance.
     */
    private interface ListTag {

        /**
         * Called when an opening <li> tag is encountered.
         *
         * Inserts an invisible [Mark] span that doesn't do any styling.
         * Instead, [closeItem] will later find the location of this span so it knows where the opening tag was.
         */
        fun openItem(text: Editable)

        /**
         * Called when a closing </li> tag is encountered.
         *
         * Pops out the invisible [Mark] span and uses it to get the opening tag location.
         * Then, sets a [LeadingMarginSpan] from the opening tag position to closing tag position.
         */
        fun closeItem(text: Editable, indentation: Int)
    }

    /**
     * Subclass of [ListTag] for unordered lists.
     */
    private class Ul : ListTag {

        override fun openItem(text: Editable) {
            appendNewLine(text)
            start(text, BulletListItem())
        }

        override fun closeItem(text: Editable, indentation: Int) {
            appendNewLine(text)

            getLast<BulletListItem>(text)?.let { mark ->
                setSpanFromMark(text, mark, TextLeadingMarginSpan(GAP_WIDTH, indentation, "•"))
            }
        }
    }

    /**
     * Subclass of [ListTag] for ordered lists.
     */
    private class Ol : ListTag {

        private var index = 1

        override fun openItem(text: Editable) {
            appendNewLine(text)
            start(text, NumberListItem(index))
            index++
        }

        override fun closeItem(text: Editable, indentation: Int) {
            appendNewLine(text)

            getLast<NumberListItem>(text)?.let { mark ->
                setSpanFromMark(
                    text, mark, TextLeadingMarginSpan(GAP_WIDTH, indentation, "${mark.number}.")
                )
            }
        }
    }

    /**
     * These static methods are based on the Android Html class source code.
     * https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/text/Html.java
     */
    companion object {

        private const val GAP_WIDTH = 50

        /**
         * Appends a new line to [text] if it doesn't already end in a new line
         */
        private fun appendNewLine(text: Editable) {
            if (text.isNotEmpty() && text.last() != '\n') {
                text.append("\n")
            }
        }

        /**
         * Returns the most recently added span of type [T] in [text].
         *
         * Invisible marking spans are inserted to record the location of opening HTML tags in the text.
         * We do this rather than using a stack in case text is inserted and the relative location shifts around.
         *
         * The last span corresponds to the top of the "stack".
         */
        private inline fun <reified T : Mark> getLast(text: Spanned) =
            text.getSpans(0, text.length, T::class.java).lastOrNull()

        /**
         * Pops out the invisible [mark] span and uses it to get the opening tag location.
         * Then, sets a span from the opening tag position to closing tag position.
         */
        private fun setSpanFromMark(text: Spannable, mark: Mark, styleSpan: Any) {
            // Find the location where the mark is inserted in the string.
            val markerLocation = text.getSpanStart(mark)
            // Remove the mark now that the location is saved
            text.removeSpan(mark)

            val end = text.length
            if (markerLocation != end) {
                text.setSpan(styleSpan, markerLocation, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }

        /**
         * Inserts an invisible [mark] span that doesn't do any styling.
         * Instead, [setSpanFromMark] will later find the location of this span so it knows where the opening tag was.
         */
        private fun start(text: Spannable, mark: Mark) {
            val currentPosition = text.length
            text.setSpan(mark, currentPosition, currentPosition, Spanned.SPAN_MARK_MARK)
        }
    }
}

interface Mark

/**
 * Marks the opening tag location of a list item inside an <ul> element.
 */
class BulletListItem : Mark

/**
 * Marks the opening tag location of a list item inside an <ol> element.
 */
class NumberListItem(val number: Int) : Mark

/**
 * A version of [LeadingMarginSpan] that shows text inside the margin.
 *
 * @param marginWidth Size of the margin.
 * @param indentation The zero-based indentation level of this item.
 * @param string String to show inside the margin.
 */
class TextLeadingMarginSpan(
    private val marginWidth: Int,
    private val indentation: Int,
    private val string: String
) : LeadingMarginSpan {

    override fun drawLeadingMargin(
        c: Canvas,
        p: Paint,
        x: Int,
        dir: Int,
        top: Int,
        baseline: Int,
        bottom: Int,
        text: CharSequence,
        start: Int,
        end: Int,
        first: Boolean,
        l: Layout
    ) {
        val startCharOfSpan = (text as Spanned).getSpanStart(this)
        val isFirstCharacter = startCharOfSpan == start

        if (isFirstCharacter) {
            // Depending on the phone, x might always be 0. We need to re-calculate it here.
            val trueX = marginWidth * indentation
            c.drawText(string, trueX.toFloat(), baseline.toFloat(), p)
        }
    }

    override fun getLeadingMargin(first: Boolean): Int = marginWidth
}
