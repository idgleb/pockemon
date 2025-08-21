package com.ursolgleb.pokcemon.presentation.state

import com.ursolgleb.pokcemon.presentation.model.PokemonUi

sealed interface UiState {
	object Loading : UiState
	data class Data(val items: List<PokemonUi>) : UiState
	data class Error(val message: String, val cached: List<PokemonUi> = emptyList()) : UiState
}


