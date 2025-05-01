package com.example.pokemonpokethelper.model

data class Card(
    val id: String,
    val name: String? = null,
    val expansion: String? = null,
    val expansionId: Int
)