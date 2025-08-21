package com.ursolgleb.pokcemon.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.Json
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

interface PokemonApiService {

	@GET("pokemon")
	suspend fun getPokemonList(
		@Query("limit") limit: Int,
		@Query("offset") offset: Int
	): PokemonResponse

	@GET("pokemon/{id}")
	suspend fun getPokemonDetail(
		@Path("id") id: Int
	): PokemonDetailResponse

	companion object {
		private const val BASE_URL = "https://pokeapi.co/api/v2/"

		fun create(): PokemonApiService {
			val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }
			val client = OkHttpClient.Builder()
				.addInterceptor(logger)
				.build()
			val moshi = Moshi.Builder()
				.add(KotlinJsonAdapterFactory())
				.build()
			return Retrofit.Builder()
				.baseUrl(BASE_URL)
				.client(client)
				.addConverterFactory(MoshiConverterFactory.create(moshi))
				.build()
				.create(PokemonApiService::class.java)
		}
	}
}

data class PokemonResponse(
	val count: Int,
	val next: String?,
	val previous: String?,
	val results: List<Pokemon>
)

data class Pokemon(
	val name: String,
	val url: String
)

// Детальный ответ (минимальный)

data class PokemonDetailResponse(
	val types: List<TypeSlot>,
	val stats: List<PokemonStat>
)

data class TypeSlot(
	val slot: Int,
	val type: NamedApiResource
)

data class NamedApiResource(
	val name: String,
	val url: String
)

data class PokemonStat(
	val base_stat: Int,
	val stat: NamedStat
)

data class NamedStat(
	val name: String,
	@Json(name = "url") val url: String?
)


