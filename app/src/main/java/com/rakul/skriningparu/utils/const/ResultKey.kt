package com.rakul.skriningparu.utils.const

object ResultKey {
    const val DEFINITELY_NOT_POSITIVE = "Pasti Tidak" // value : -1.0 < x < -0.8
    const val ALMOST_CERTAINLY_NOT_POSITIVE = "Hampir Pasti Tidak" // value : -0.8 < x < -0.6
    const val MOST_LIKELY_NOT_POSITIVE = "Kemungkinan Besar Tidak" // value : -0.6 < x < -0.4
    const val PROBABLY_NOT_POSITIVE = "Mungkin Tidak" // value : -0.4 < x < -0.2
    const val DONT_KNOW_NOT_POSITIVE = "Tidak Tahu" // value : -0.2 < x < 0.2

    const val MAYBE_POSITIVE = "Mungkin" // value : 0.2 < x < 0.6
    const val MOST_LIKELY_POSITIVE = "Kemungkinan Besar" // value : 0.6 < x < 0.8
    const val ALMOST_CERTAIN_POSITIVE = "Hampir Pasti" // value : 0.8 < x < 1.0
    const val CERTAIN_POSITIVE = "Pasti"  // value : x > 1.0
}