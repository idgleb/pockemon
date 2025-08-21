package com.ursolgleb.pokcemon.data

import com.ursolgleb.pokcemon.data.local.PokemonDao
import com.ursolgleb.pokcemon.data.local.PokemonEntity
import com.ursolgleb.pokcemon.data.remote.PokemonApiService
import com.ursolgleb.pokcemon.domain.PokemonRepository
import com.ursolgleb.pokcemon.domain.model.Pokemon

class PokemonRepositoryImpl(
    private val api: PokemonApiService,
    private val dao: PokemonDao
) : PokemonRepository {

    override suspend fun fetchAndCachePage(limit: Int, offset: Int): List<Pokemon> {
        val response = api.getPokemonList(limit = limit, offset = offset)
        if (response.results.isEmpty()) return emptyList()
        val pageEntities = response.results.map { result ->
            val id = extractIdFromUrl(result.url)
            val detail = try { api.getPokemonDetail(id) } catch (_: Exception) { null }
            val types = detail?.types?.joinToString(",") { it.type.name } ?: ""
            val stats = detail?.stats ?: emptyList()
            val hp = stats.firstOrNull { it.stat.name == "hp" }?.base_stat ?: 0
            val attack = stats.firstOrNull { it.stat.name == "attack" }?.base_stat ?: 0
            val defense = stats.firstOrNull { it.stat.name == "defense" }?.base_stat ?: 0
            PokemonEntity(
                id = id,
                name = result.name,
                imageUrl = spriteUrl(id),
                typesCsv = types,
                hp = hp,
                attack = attack,
                defense = defense
            )
        }
        dao.insertAll(pageEntities)
        return pageEntities.map { it.toDomain() }
    }

    override suspend fun getFilteredByTypesOr(types: List<String>, order: String): List<Pokemon> {
        if (types.isEmpty()) return dao.getAllSorted(order).map { it.toDomain() }
        val base = dao.getAllSorted(order)
        val filtered = base.filter { entity ->
            val set = entity.typesCsv.split(',').map { it.trim().lowercase() }.toSet()
            types.any { t -> set.contains(t.lowercase()) }
        }
        return filtered.map { it.toDomain() }
    }

    override suspend fun getAll(): List<Pokemon> {
        return dao.getAllPokemon().map { it.toDomain() }
    }

    private fun extractIdFromUrl(url: String): Int {
        return url.trimEnd('/').substringAfterLast('/').toIntOrNull() ?: url.hashCode()
    }

    private fun spriteUrl(id: Int): String =
        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
}


