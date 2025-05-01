
package com.example.pokemonpokethelper.network

import retrofit2.http.*
import com.example.pokemonpokethelper.network.CollectionDto
import com.example.pokemonpokethelper.network.AddCollectionRequest



interface ApiService {
    @GET("collections")
    suspend fun getCollections(
        @Header("Authorization") bearer: String
    ): List<CollectionDto>

    @POST("collections")
    suspend fun addCollection(
        @Header("Authorization") bearer: String,
        @Body payload: AddCollectionRequest
    ): CollectionDto

    // ——————————————————————————
    // NOTE the uppercase “Cards” here to match your controller name
    @GET("collections/{cid}/Cards")
    suspend fun getCards(
        @Header("Authorization") bearer: String,
        @Path("cid") collectionId: String
    ): List<CardDto>

    @POST("collections/{cid}/Cards")
    suspend fun addCard(
        @Header("Authorization") bearer: String,
        @Path("cid") collectionId: String,
        @Body payload: AddCardRequest
    )

    @DELETE("collections/{cid}/Cards/{cardId}")
    suspend fun deleteCard(
        @Header("Authorization") bearer: String,
        @Path("cid") collectionId: String,
        @Path("cardId") cardId: String
    )
}

