package com.example.pokemonpokethelper.data

import com.example.pokemonpokethelper.network.AddCardRequest
import com.example.pokemonpokethelper.network.AddCollectionRequest
import com.example.pokemonpokethelper.network.CardDto
import com.example.pokemonpokethelper.network.CollectionDto
import com.example.pokemonpokethelper.network.RetrofitClient

object RemoteRepo {
    private val api = RetrofitClient.api

    suspend fun fetchCollections(token: String): List<CollectionDto> =
        api.getCollections("Bearer $token")

    suspend fun addCollection(token: String, name: String): CollectionDto =
        api.addCollection("Bearer $token", AddCollectionRequest(name))

    suspend fun fetchCards(token: String, cid: String): List<CardDto> =
        RetrofitClient.api.getCards("Bearer $token", cid)

    suspend fun addCard(token: String, cid: String, req: AddCardRequest) =
        api.addCard("Bearer $token", cid, req)

    suspend fun deleteCard(token: String, cid: String, cardId: String) {
        RetrofitClient.api.deleteCard("Bearer $token", cid, cardId)
    }
}
