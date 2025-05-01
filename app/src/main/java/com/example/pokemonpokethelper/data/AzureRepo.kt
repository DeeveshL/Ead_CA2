package com.example.pokemonpokethelper.data

import com.example.pokemonpokethelper.model.ModelCollection
import com.example.pokemonpokethelper.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

object AzureRepo {
    private val api = RetrofitClient.apiService

    suspend fun getCollections(userId: String) = withContext(Dispatchers.IO) {
        api.getCollections(userId)
    }

    suspend fun createCollection(userId: String, name: String) = withContext(Dispatchers.IO) {
        val newCollection = ModelCollection(
            id = UUID.randomUUID().toString(),
            name = name,
            userId = userId
        )
        api.createCollection(userId, newCollection)
    }

    suspend fun getCollection(userId: String, name: String) = withContext(Dispatchers.IO) {
        api.getCollection(userId, name)
    }

    suspend fun addCardToCollection(userId: String, collectionName: String, cardId: String) =
        withContext(Dispatchers.IO) {
            api.addCardToCollection(userId, collectionName, cardId)
        }
}
