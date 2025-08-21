package com.ursolgleb.pokcemon.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonDao {

	@Query("SELECT * FROM PokemonEntity")
	suspend fun getAllPokemon(): List<PokemonEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(pokemon: List<PokemonEntity>)

	@Query("DELETE FROM PokemonEntity")
	suspend fun deleteAll()

	@Query("SELECT * FROM PokemonEntity WHERE (:type IS NULL OR typesCsv LIKE '%' || :type || '%') ORDER BY "+
			"CASE WHEN :order = 'number' THEN id END ASC, "+
			"CASE WHEN :order = 'name' THEN name END ASC, "+
			"CASE WHEN :order = 'hp' THEN hp END DESC, "+
			"CASE WHEN :order = 'attack' THEN attack END DESC, "+
			"CASE WHEN :order = 'defense' THEN defense END DESC")
	suspend fun filterAndSort(type: String?, order: String): List<PokemonEntity>

	@Query("SELECT * FROM PokemonEntity ORDER BY "+
			"CASE WHEN :order = 'number' THEN id END ASC, "+
			"CASE WHEN :order = 'name' THEN name END ASC, "+
			"CASE WHEN :order = 'hp' THEN hp END DESC, "+
			"CASE WHEN :order = 'attack' THEN attack END DESC, "+
			"CASE WHEN :order = 'defense' THEN defense END DESC")
	suspend fun getAllSorted(order: String): List<PokemonEntity>
}


