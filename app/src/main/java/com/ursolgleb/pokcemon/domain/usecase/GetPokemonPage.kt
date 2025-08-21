package com.ursolgleb.pokcemon.domain.usecase

import com.ursolgleb.pokcemon.domain.PokemonRepository
import com.ursolgleb.pokcemon.domain.model.Pokemon

class GetPokemonPage(private val repository: PokemonRepository) {
	suspend operator fun invoke(limit: Int, offset: Int): List<Pokemon> =
		repository.fetchAndCachePage(limit, offset)
}


