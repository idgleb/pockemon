package com.ursolgleb.pokcemon.domain.usecase

import com.ursolgleb.pokcemon.domain.PokemonRepository
import com.ursolgleb.pokcemon.domain.model.Pokemon

class GetAllPokemon(private val repository: PokemonRepository) {
	suspend operator fun invoke(): List<Pokemon> = repository.getAll()
}


