package com.ursolgleb.pokcemon.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class PokemonEntity(
	@PrimaryKey val id: Int,
	val name: String,
	val imageUrl: String,
	val typesCsv: String = "",
	val hp: Int = 0,
	val attack: Int = 0,
	val defense: Int = 0
)


