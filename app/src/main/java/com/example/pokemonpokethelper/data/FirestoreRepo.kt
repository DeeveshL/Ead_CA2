package com.example.pokemonpokethelper.data


import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object FirestoreRepo {
    private val db = Firebase.firestore

    fun getCollections() =
        db.collection("collections")
            .orderBy("createdAt")
            .snapshots()   // returns Flow<QuerySnapshot>

    suspend fun addCollection(name: String) {
        val data = mapOf(
            "name"      to name,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("collections")
            .add(data)
            .await()
    }

    fun getCards(collectionId: String) =
        db.collection("collections")
            .document(collectionId)
            .collection("cards")
            .orderBy("createdAt")
            .snapshots()

    suspend fun addCard(
        collectionId: String,
        name: String,
        type: String,
        rarity: String
    ) {
        val data = mapOf(
            "name"      to name,
            "type"      to type,
            "rarity"    to rarity,
            "createdAt" to FieldValue.serverTimestamp()
        )
        db.collection("collections")
            .document(collectionId)
            .collection("cards")
            .add(data)
            .await()
    }
    suspend fun deleteCard(collectionId: String, cardId: String) {
        db.collection("collections")
            .document(collectionId)
            .collection("cards")
            .document(cardId)
            .delete()
            .await()
    }

}

