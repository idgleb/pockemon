package com.ursolgleb.pokcemon.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ursolgleb.pokcemon.domain.usecase.GetAllPokemon
import com.ursolgleb.pokcemon.domain.usecase.GetPokemonListFiltered
import com.ursolgleb.pokcemon.domain.usecase.GetPokemonPage
import com.ursolgleb.pokcemon.presentation.model.PokemonUi
import com.ursolgleb.pokcemon.presentation.model.toUi
import com.ursolgleb.pokcemon.presentation.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class PokemonViewModel @Inject constructor(
	@ApplicationContext private val appContext: Context,
	private val getPokemonPage: GetPokemonPage,
	private val getPokemonListFiltered: GetPokemonListFiltered,
	private val getAllPokemon: GetAllPokemon
) : ViewModel() {

	private val _state = MutableLiveData<UiState>(UiState.Loading)
	val state: LiveData<UiState> get() = _state

	private var fullList: MutableList<PokemonUi> = mutableListOf()
	private var currentOffset: Int = 0
	private val pageSize: Int = com.ursolgleb.pokcemon.presentation.util.UiConstants.PAGE_SIZE
	private var endReached: Boolean = false

	private var currentQuery: String = ""
	private var selectedType: String? = null
	private var selectedTypes: MutableList<String> = mutableListOf()
	private var orderBy: String = "number"
	private val prefs = appContext.getSharedPreferences(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.PREFS_FILTERS, Context.MODE_PRIVATE)

	init {
		loadSavedFilters()
		refresh()
	}

	fun isFiltering(): Boolean = selectedTypes.isNotEmpty() || !selectedType.isNullOrBlank() || orderBy != "number"

	fun refresh() {
		currentOffset = 0
		endReached = false
		fullList.clear()
		_state.value = UiState.Loading
		fetchPokemon(reset = true)
	}

	fun fetchPokemon(reset: Boolean = false) {
		if ((_state.value is UiState.Loading && !reset) || endReached) return
		_state.value = UiState.Loading
		viewModelScope.launch {
			try {
				val page = getPokemonPage(pageSize, currentOffset)
				if (page.isEmpty()) {
					endReached = true
				} else {
					currentOffset += pageSize

					val base = if (isFiltering()) getPokemonListFiltered(selectedTypes.toList(), orderBy).map { it.toUi() } else run {
						if (reset) fullList.clear()
						fullList.addAll(page.map { it.toUi() })
						fullList.toList()
					}
					val list = if (currentQuery.isBlank()) base else base.filter { it.title.contains(currentQuery, true) }
					_state.value = UiState.Data(list)
				}
			} catch (e: Exception) {
				val cached = if (isFiltering()) getPokemonListFiltered(selectedTypes.toList(), orderBy) else getAllPokemon()
				fullList.clear(); fullList.addAll(cached.map { it.toUi() })
				val list = if (currentQuery.isBlank()) fullList.toList() else fullList.filter { it.title.contains(currentQuery, true) }
				if (list.isNotEmpty()) {
					_state.value = UiState.Data(list)
				} else {
					_state.value = UiState.Error(appContext.getString(com.ursolgleb.pockemon.R.string.cache_empty_error), cached = emptyList())
				}
			}
		}
	}

	fun getSelectedType(): String? = selectedType

	fun getSelectedTypes(): List<String> = selectedTypes.toList()

	fun getOrderBy(): String = orderBy

	fun setSearchQuery(query: String) {
		currentQuery = query
		viewModelScope.launch {
			val base: List<PokemonUi> = if (isFiltering()) getPokemonListFiltered(selectedTypes.toList(), orderBy).map { it.toUi() } else fullList.toList()
			val result = if (currentQuery.isBlank()) base else base.filter { it.title.contains(currentQuery, ignoreCase = true) }
			_state.value = UiState.Data(result)
			saveSearchQuery()
		}
	}

	fun applyFilters(selectedTypesParam: List<String>?, order: String) {
		selectedTypes.clear()
		if (selectedTypesParam != null) selectedTypes.addAll(selectedTypesParam)
		selectedType = selectedTypes.firstOrNull()
		orderBy = order
		saveFilters()
		viewModelScope.launch {
			val base = getPokemonListFiltered(selectedTypes.toList(), orderBy).map { it.toUi() }
			val list = if (currentQuery.isBlank()) base else base.filter { it.title.contains(currentQuery, true) }
			_state.value = UiState.Data(list)
		}
	}

	fun resetFilters() {
		selectedType = null
		selectedTypes.clear()
		orderBy = "number"
		saveFilters()
		viewModelScope.launch {
			val base = getPokemonListFiltered(emptyList(), orderBy).map { it.toUi() }
			val list = if (currentQuery.isBlank()) base else base.filter { it.title.contains(currentQuery, true) }
			_state.value = UiState.Data(list)
		}
	}

	private fun saveFilters() {
		prefs.edit()
			.putString(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_SELECTED_TYPE, selectedType)
			.putString(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_SELECTED_TYPES_CSV, if (selectedTypes.isEmpty()) "" else selectedTypes.joinToString(","))
			.putString(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_ORDER_BY, orderBy)
			.apply()
	}

	private fun loadSavedFilters() {
		selectedType = prefs.getString(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_SELECTED_TYPE, null)
		selectedTypes.clear()
		prefs.getString(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_SELECTED_TYPES_CSV, "")?.takeIf { it.isNotBlank() }?.split(',')?.map { it.trim() }?.let { selectedTypes.addAll(it) }
		orderBy = prefs.getString(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_ORDER_BY, "number") ?: "number"
		currentQuery = prefs.getString(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_SEARCH_QUERY, "") ?: ""
	}

	private fun saveSearchQuery() {
		prefs.edit().putString(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_SEARCH_QUERY, currentQuery).apply()
	}

	fun getCurrentQuery(): String = currentQuery
}


