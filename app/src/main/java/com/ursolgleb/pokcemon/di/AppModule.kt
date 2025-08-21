package com.ursolgleb.pokcemon.di

import android.content.Context
import com.ursolgleb.pokcemon.data.PokemonRepositoryImpl
import com.ursolgleb.pokcemon.data.local.PokemonDao
import com.ursolgleb.pokcemon.data.local.PokemonDatabase
import com.ursolgleb.pokcemon.data.remote.PokemonApiService
import com.ursolgleb.pokcemon.domain.PokemonRepository
import com.ursolgleb.pokcemon.domain.usecase.GetPokemonListFiltered
import com.ursolgleb.pokcemon.domain.usecase.GetAllPokemon
import com.ursolgleb.pokcemon.domain.usecase.GetPokemonPage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

	@Provides
	@Singleton
	fun provideApi(): PokemonApiService = PokemonApiService.create()

	@Provides
	@Singleton
	fun provideDatabase(@ApplicationContext context: Context): PokemonDatabase =
		PokemonDatabase.getDatabase(context)

	@Provides
	fun provideDao(db: PokemonDatabase): PokemonDao = db.pokemonDao()

	@Provides
	@Singleton
	fun provideRepository(api: PokemonApiService, dao: PokemonDao): PokemonRepository =
		PokemonRepositoryImpl(api, dao)

	@Provides
	fun provideGetPokemonPage(repo: PokemonRepository): GetPokemonPage = GetPokemonPage(repo)

	@Provides
	fun provideGetPokemonListFiltered(repo: PokemonRepository): GetPokemonListFiltered = GetPokemonListFiltered(repo)

	@Provides
	fun provideGetAllPokemon(repo: PokemonRepository): GetAllPokemon = GetAllPokemon(repo)
}


