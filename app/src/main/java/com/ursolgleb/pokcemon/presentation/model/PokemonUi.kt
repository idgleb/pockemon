package com.ursolgleb.pokcemon.presentation.model

import com.ursolgleb.pokcemon.domain.model.Pokemon

data class PokemonUi(
	val id: Int,
	val title: String,
	val imageUrl: String,
	val typesLabel: String,
	val types: List<String>
)

fun Pokemon.toUi(): PokemonUi = PokemonUi(
	id = id,
	title = name.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() },
	imageUrl = imageUrl,
	typesLabel = if (types.isEmpty()) "" else types.joinToString(", "),
	types = types
)


