package com.tobaxiom.placebo

import java.time.LocalDate
import java.util.UUID

class Streak(var name: String){
    var id: UUID = UUID.randomUUID()
    var markedDays = mutableListOf<LocalDate>()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Streak

        return id != other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    fun markForToday() {
        markedDays.add(LocalDate.now())
    }
}
