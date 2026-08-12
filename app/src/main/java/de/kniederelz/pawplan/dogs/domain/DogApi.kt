package de.kniederelz.pawplan.dogs.domain

import retrofit2.http.GET

interface DogApi {
    @GET("api/breeds/image/random")
    suspend fun getRandomDogImage(): DogImageResponse
}