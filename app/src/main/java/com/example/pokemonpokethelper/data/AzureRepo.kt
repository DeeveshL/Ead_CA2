package com.example.pokemonpokethelper.data

import com.example.pokemonpokethelper.network.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AzureRepo {
    private val api = RetrofitClient.api

    suspend fun getCollections(token: String, userId: String) =
        api.getCollections("Bearer $token", userId)

    suspend fun createCollection(token: String, userId: String, name: String) =
        api.createCollection("Bearer $token", userId, CreateCollectionRequest(name))

    suspend fun getCollection(token: String, userId: String, name: String) =
        api.getCollection("Bearer $token", userId, name)

    suspend fun addCardToCollection(
        token: String,
        userId: String,
        collectionName: String,
        cardId: String
    ) = api.addCardToCollection("Bearer $token", userId, collectionName, cardId)

}

