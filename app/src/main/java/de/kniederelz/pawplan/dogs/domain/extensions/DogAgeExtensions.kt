package de.kniederelz.pawplan.dogs.domain.extensions

import de.kniederelz.pawplan.dogs.domain.Dog
import de.kniederelz.pawplan.dogs.domain.DogAge
import java.time.LocalDate
import java.time.Period

fun Dog.getAge(): DogAge {
    val period = Period.between(birthday, LocalDate.now())
    return DogAge(period.years, period.months)
}
