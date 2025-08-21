package com.ursolgleb.pokcemon.domain.usecase

import com.ursolgleb.pokcemon.domain.PokemonRepository
import com.ursolgleb.pokcemon.domain.model.Pokemon

class GetPokemonListFiltered(private val repository: PokemonRepository) {
	suspend operator fun invoke(types: List<String>, order: String): List<Pokemon> =
		repository.getFilteredByTypesOr(types, order)
}


