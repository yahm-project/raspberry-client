package it.unibo.yahm.client.entities

import kotlin.random.Random

enum class Quality(val value: Int) {
    VERY_BAD(0),
    BAD(1),
    OK(2),
    GOOD(3),
    PERFECT(4);

    companion object {
        private val map = values().associateBy(Quality::value)
        fun fromValue(value: Int) = map[value]

        fun random() = map[Random.nextInt(0, map.size)] ?: error("out of range")
    }
}
