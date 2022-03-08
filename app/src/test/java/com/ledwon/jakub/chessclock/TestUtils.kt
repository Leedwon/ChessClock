package com.ledwon.jakub.chessclock

inline fun <reified T : Throwable> assertThrows(body: () -> Unit): T {
    try {
        body()
    } catch (t: Throwable) {
        if (t is T) {
            return t
        }
        throw t
    }
    throw AssertionError(
        "Expected body to throw ${T::class.simpleName} but it completed successfully"
    )
}
