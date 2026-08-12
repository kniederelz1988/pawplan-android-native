package de.kniederelz.pawplan.dogs.domain

data class DogImageResponse(
    val message: String,
    val status: String
)

fun DogImageResponse.getUrl(): String {
    if (status != "success")
        return ""

    return message
}