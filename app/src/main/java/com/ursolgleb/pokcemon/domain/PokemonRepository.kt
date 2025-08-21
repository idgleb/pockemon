package com.ursolgleb.pokcemon.domain

import com.ursolgleb.pokcemon.domain.model.Pokemon

interface PokemonRepository {
    suspend fun fetchAndCachePage(limit: Int, offset: Int): List<Pokemon>
    suspend fun getFilteredByTypesOr(types: List<String>, order: String): List<Pokemon>
    suspend fun getAll(): List<Pokemon>
}


