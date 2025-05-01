package com.example.pokemonpokethelper.network

data class AddCardRequest(
    val name: String,
    val expansion: String,
    val expansionId: Int
)