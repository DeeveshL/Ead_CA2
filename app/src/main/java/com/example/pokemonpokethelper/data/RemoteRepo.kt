package com.example.pokemonpokethelper.data

import com.example.pokemonpokethelper.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.http.DELETE
import retrofit2.http.Header
import retrofit2.http.Path

object RemoteRepo {
    private val api = RetrofitClient.api

    // ——— Collections ——————————————————————————————

    /** GET  /api/users/{userId}/collections */
    suspend fun fetchCollections(token: String, userId: String): List<CollectionDto> =
        withContext(Dispatchers.IO) {
            api.getCollections("Bearer $token", userId)
        }

    /** POST /api/users/{userId}/collections */
    suspend fun addCollection(token: String, userId: String, name: String): CollectionDto =
        withContext(Dispatchers.IO) {
            api.createCollection("Bearer $token", userId, CreateCollectionRequest(name))
        }

    // ——— Cards ——————————————————————————————————————

    /** GET  /api/users/{userId}/collections/{collectionName}/cards */
    suspend fun fetchCards(token: String, userId: String, collectionName: String) =
        api.getCards("Bearer $token", userId, collectionName)

    /** POST /api/users/{userId}/collections/{collectionName}/cards */
    suspend fun addCard(
        token: String,
        userId: String,
        collectionName: String,
        cardId: String
    ) = withContext(Dispatchers.IO) {
        api.addCardToCollection("Bearer $token", userId, collectionName, cardId)
    }

    // DELETE /api/users/{userId}/collections/{collectionName}/cards/{cardId}
    @DELETE("users/{userId}/collections/{collectionName}/cards/{cardId}")
    suspend fun deleteCard(
        @Header("Authorization") bearer: String,
        @Path("userId")         userId: String,
        @Path("collectionName") collectionName: String,
        @Path("cardId")         cardId: String
    ) {
    }
}
