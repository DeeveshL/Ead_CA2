package com.example.pokemonpokethelper.network

import com.example.pokemonpokethelper.model.*
import retrofit2.http.*

interface ApiService {



    @GET("api/users/{userId}/collections")
    suspend fun getCollections(
        @Path("userId") userId: String
    ): List<ModelCollection>

    @POST("api/users/{userId}/collections")
    suspend fun createCollection(
        @Path("userId") userId: String,
        @Body collection: ModelCollection
    ): ModelCollection

    @GET("api/users/{userId}/collections/{collectionName}")
    suspend fun getCollection(
        @Path("userId") userId: String,
        @Path("collectionName") collectionName: String
    ): ModelCollection

    @POST("api/users/{userId}/collections/{collectionName}/cards")
    suspend fun addCardToCollection(
        @Path("userId") userId: String,
        @Path("collectionName") collectionName: String,
        @Body cardId: String
    )

    @GET("api/cards")
    suspend fun getAllCards(): List<Card>

    @GET("api/cards/search")
    suspend fun searchCards(
        @Query("name") name: String? = null,
        @Query("expansion") expansion: String? = null,
        @Query("expansionId") expansionId: Int? = null
    ): List<Card>

    @POST("api/cards")
    suspend fun createCard(
        @Body card: Card
    ): Card
}
