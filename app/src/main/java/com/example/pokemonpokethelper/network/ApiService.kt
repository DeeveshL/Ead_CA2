
package com.example.pokemonpokethelper.network

import retrofit2.http.*
import com.example.pokemonpokethelper.network.CollectionDto
import com.example.pokemonpokethelper.network.AddCollectionRequest



interface ApiService {
    // GET  /api/users/{UserId}/collections
    @GET("users/{userId}/collections")
    suspend fun getCollections(
        @Header("Authorization") bearer: String,
        @Path("userId") userId: String
    ): List<CollectionDto>

    @GET("users/{userId}/collections/{collectionName}/cards")
    suspend fun getCards(
        @Header("Authorization") bearer: String,
        @Path("userId") userId: String,
        @Path("collectionName") collectionName: String
    ): List<CardDto>

    // POST /api/users/{UserId}/collections
    @POST("users/{userId}/collections")
    suspend fun createCollection(
        @Header("Authorization") bearer: String,
        @Path("userId") userId: String,
        @Body payload: CreateCollectionRequest
    ): CollectionDto

    // GET  /api/users/{UserId}/collections/{collectionName}
    @GET("users/{userId}/collections/{collectionName}")
    suspend fun getCollection(
        @Header("Authorization") bearer: String,
        @Path("userId") userId: String,
        @Path("collectionName") collectionName: String
    ): CollectionDto

    // POST /api/users/{UserId}/collections/{collectionName}/cards
    // Body is a raw GUID string unless you wrapped it
    @POST("users/{userId}/collections/{collectionName}/cards")
    suspend fun addCardToCollection(
        @Header("Authorization") bearer: String,
        @Path("userId") userId: String,
        @Path("collectionName") collectionName: String,
        @Body cardId: String
    )
}

