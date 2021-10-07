package com.ledwon.jakub.chessclock

import org.junit.Assert.assertEquals


class Assertion<T>(val value: T)

val <T> T.should get() = Assertion(this)

fun <T> Assertion<T>.beEqualTo(expected: T) {
    assertEquals(expected, this.value)
}