package com.ursolgleb.pokcemon.data

import com.ursolgleb.pokcemon.data.local.PokemonEntity
import com.ursolgleb.pokcemon.domain.model.Pokemon

fun PokemonEntity.toDomain(): Pokemon = Pokemon(
	id = id,
	name = name,
	imageUrl = imageUrl,
	types = if (typesCsv.isBlank()) emptyList() else typesCsv.split(',').map { it.trim() },
	hp = hp,
	attack = attack,
	defense = defense
)


