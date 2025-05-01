package com.example.pokemonpokethelper.data

import com.example.pokemonpokethelper.model.*
import com.example.pokemonpokethelper.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object RemoteRepo {
    private val apiService = RetrofitClient.apiService

    // Collections
    suspend fun getCollections(userId: String): List<ModelCollection> =
        withContext(Dispatchers.IO) { apiService.getCollections(userId) }

    suspend fun createCollection(userId: String, name: String): ModelCollection =
        withContext(Dispatchers.IO) {
            apiService.createCollection(
                userId,
                ModelCollection(
                    id = UUID.randomUUID().toString(),
                    name = name,
                    userId = userId
                )
            )
        }

    // Cards
    suspend fun getAllCards(): List<Card> =
        withContext(Dispatchers.IO) { apiService.getAllCards() }

    suspend fun searchCards(
        name: String? = null,
        expansion: String? = null,
        expansionId: Int? = null
    ): List<Card> = withContext(Dispatchers.IO) {
        apiService.searchCards(name, expansion, expansionId)
    }

    suspend fun addCardToCollection(
        userId: String,
        collectionName: String,
        cardId: String
    ) = withContext(Dispatchers.IO) {
        apiService.addCardToCollection(
            userId,
            collectionName,
            cardId
        )
    }

    suspend fun createCard(name: String, expansion: String, expansionId: Int): Card =
        withContext(Dispatchers.IO) {
            val newCard = Card(
                id = UUID.randomUUID().toString(),
                name = name,
                expansion = expansion,
                expansionId = expansionId
            )
            RetrofitClient.apiService.createCard(newCard)
        }

}