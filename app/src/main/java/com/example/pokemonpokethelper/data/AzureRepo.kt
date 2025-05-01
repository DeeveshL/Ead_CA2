package com.example.pokemonpokethelper.data

import com.example.pokemonpokethelper.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AzureRepo {
    private val api = RetrofitClient.api

    // ——— Collections ——————————————————————————————

    /** GET  /api/collections */
    suspend fun getCollections(token: String): List<CollectionDto> =
        withContext(Dispatchers.IO) {
            api.getCollections("Bearer $token")
        }

    /** POST /api/collections */
    suspend fun addCollection(token: String, name: String): CollectionDto =
        withContext(Dispatchers.IO) {
            api.addCollection("Bearer $token", AddCollectionRequest(name))
        }

    // ——— Cards ——————————————————————————————————————

    /** GET  /api/collections/{cid}/cards */
    suspend fun getCards(token: String, collectionId: String): List<CardDto> =
        withContext(Dispatchers.IO) {
            api.getCards("Bearer $token", collectionId)
        }

    /** POST /api/collections/{cid}/cards */
    suspend fun addCard(
        token: String,
        collectionId: String,
        name: String,
        expansion: String,
        expansionId: Int
    ): Unit = withContext(Dispatchers.IO) {
        api.addCard(
            bearer     = "Bearer $token",
            collectionId = collectionId,
            payload    = AddCardRequest(name, expansion, expansionId)
        )
    }

    /** DELETE /api/collections/{cid}/cards/{cardId} */
    suspend fun deleteCard(
        token: String,
        collectionId: String,
        cardId: String
    ): Unit = withContext(Dispatchers.IO) {
        api.deleteCard("Bearer $token", collectionId, cardId)
    }
}
