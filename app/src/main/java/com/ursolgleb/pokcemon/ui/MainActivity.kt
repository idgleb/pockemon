package com.ursolgleb.pokcemon.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.GridLayoutManager
import com.ursolgleb.pockemon.databinding.ActivityMainBinding
import com.ursolgleb.pockemon.R
import com.ursolgleb.pokcemon.presentation.PokemonViewModel
import com.ursolgleb.pokcemon.presentation.state.UiState
import com.ursolgleb.pokcemon.presentation.util.colorForType
import com.ursolgleb.pokcemon.presentation.util.applyHintAndColors
import dagger.hilt.android.AndroidEntryPoint
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import androidx.core.content.edit

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

	private lateinit var binding: ActivityMainBinding
	private val viewModel: PokemonViewModel by viewModels()
	private var scrollRestored: Boolean = false
	private var isLoadingUi: Boolean = false
	private var connectivityMonitor: com.ursolgleb.pokcemon.presentation.util.ConnectivityMonitor? = null
	private lateinit var fabController: com.ursolgleb.pokcemon.presentation.util.FabController

	override fun onCreate(savedInstanceState: Bundle?) {
		val splash = installSplashScreen()
		splash.setOnExitAnimationListener { splashView ->
			com.ursolgleb.pokcemon.presentation.util.animateSplashExit(
				activity = this,
				splashView = splashView
			)
		}
		super.onCreate(savedInstanceState)
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		// Настройка подсказки и цветов текста для SearchView
		binding.searchView.applyHintAndColors(
			getString(R.string.search_hint),
			com.ursolgleb.pokcemon.presentation.util.UiConstants.SEARCH_HINT_COLOR,
			Color.WHITE
		)
		// Мониторинг подключения к сети
		connectivityMonitor = com.ursolgleb.pokcemon.presentation.util.ConnectivityMonitor(this) { online ->
			runOnUiThread { updateConnectionIcon(online) }
		}.also {
			it.register()
			updateConnectionIcon(it.isNetworkAvailable())
		}

		fabController = com.ursolgleb.pokcemon.presentation.util.FabController(binding.fabScrollTop)

		setupRecyclerView()
		setupSearchView()
		setupSwipeRefreshLayout()
		observeViewModel()
		renderActiveFilters()

		// Восстановление предыдущего запроса поиска
		val initialQuery = viewModel.getCurrentQuery()
		if (initialQuery.isNotBlank()) {
			binding.searchView.setQuery(initialQuery, false)
			binding.searchView.isIconified = false
			binding.searchView.requestFocus()
			val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
			imm?.showSoftInput(binding.searchView, InputMethodManager.SHOW_IMPLICIT)
		}

		// Обеспечить фокус при любом нажатии на SearchView
		binding.searchView.setOnQueryTextFocusChangeListener { v, hasFocus ->
			// без действий
		}
		binding.searchView.setOnClickListener {
			binding.searchView.isIconified = false
			binding.searchView.requestFocus()
		}

		// Очистка текста по нажатию на крестик и показ полного списка
		(binding.searchView.findViewById(androidx.appcompat.R.id.search_close_btn) as? ImageView)?.setOnClickListener {
			if (!binding.searchView.query.isNullOrEmpty()) {
				binding.searchView.setQuery("", false)
				viewModel.setSearchQuery("")
			}
			// Скрыть клавиатуру
			val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
			imm?.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
			binding.searchView.clearFocus()
		}

		// Простой бесконечный скролл
		binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
			override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
				super.onScrolled(recyclerView, dx, dy)
				val lm = recyclerView.layoutManager as GridLayoutManager
				val total = lm.itemCount
				val last = lm.findLastVisibleItemPosition()
				if (!isLoadingUi && last >= total - com.ursolgleb.pokcemon.presentation.util.UiConstants.ENDLESS_SCROLL_PREFETCH) {
					viewModel.fetchPokemon()
				}
				updateFabVisibilityByScroll()
			}
		})

		binding.btnFilters.setOnClickListener {
			// Скрыть клавиатуру при открытии фильтров
			val imm = getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager
			imm?.hideSoftInputFromWindow(binding.searchView.windowToken, 0)
			binding.searchView.clearFocus()
			hideFab()
			showFiltersSheet()
		}

		// FAB прокрутка к началу
		binding.fabScrollTop.setOnClickListener {
			it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
			scrollToTopAndResetSaved()
			// синхронизация эффекта, если есть
		}

	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		return false
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		return super.onOptionsItemSelected(item)
	}

	private fun setupRecyclerView() {
		binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
		binding.recyclerView.adapter = PokemonAdapter()
	}

	private fun setupSearchView() {
		binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
			override fun onQueryTextSubmit(query: String?): Boolean {
				return false
			}

			override fun onQueryTextChange(newText: String?): Boolean {
				viewModel.setSearchQuery(newText ?: "")
				scrollToTopAndResetSaved()
				return true
			}
		})
	}

	private fun setupSwipeRefreshLayout() {
		binding.swipeRefreshLayout.setOnRefreshListener {
			viewModel.fetchPokemon()
			binding.swipeRefreshLayout.isRefreshing = false
		}
	}

	private fun observeViewModel() {
		viewModel.state.observe(this) { state ->
			when (state) {
				is UiState.Loading -> {
					isLoadingUi = true
					// Если в адаптере уже есть элементы: показываем нижний лоадер, иначе центральный
					val hasItems = (binding.recyclerView.adapter as PokemonAdapter).itemCount > 0
					binding.progressBar.visibility = if (hasItems) View.GONE else View.VISIBLE
					binding.progressBarBottom.visibility = if (hasItems) View.VISIBLE else View.GONE
					binding.noDataTextView.visibility = View.GONE
					updateFiltersButtonTint()
				}
				is UiState.Data -> {
					isLoadingUi = false
					binding.progressBar.visibility = View.GONE
					binding.progressBarBottom.visibility = View.GONE
					(binding.recyclerView.adapter as PokemonAdapter).submitList(state.items)
					binding.noDataTextView.visibility = if (state.items.isEmpty()) View.VISIBLE else View.GONE
					binding.noDataTextView.text = if (state.items.isEmpty()) getString(R.string.no_results) else ""
					updateFiltersButtonTint()
					restoreScrollPositionIfNeeded()
				}
				is UiState.Error -> {
					isLoadingUi = false
					binding.progressBar.visibility = View.GONE
					binding.progressBarBottom.visibility = View.GONE
					(binding.recyclerView.adapter as PokemonAdapter).submitList(state.cached)
					binding.noDataTextView.visibility = if (state.cached.isEmpty()) View.VISIBLE else View.GONE
					binding.noDataTextView.text = state.message
					updateFiltersButtonTint()
				}
			}
		}
	}

	private fun showFiltersSheet() {
		com.ursolgleb.pokcemon.presentation.util.FiltersBottomSheet(
			context = this,
			currentOrder = viewModel.getOrderBy(),
			preselectedTypes = viewModel.getSelectedTypes(),
			onApply = { selectedTypes, order ->
				viewModel.applyFilters(selectedTypes, order)
				renderActiveFilters()
				scrollToTopAndResetSaved()
			},
			onReset = {
				viewModel.resetFilters()
				renderActiveFilters()
				scrollToTopAndResetSaved()
			}
		).show()
	}

	override fun onSupportNavigateUp(): Boolean {
		onBackPressedDispatcher.onBackPressed()
		return true
	}

	private fun renderActiveFilters() {
		com.ursolgleb.pokcemon.presentation.util.renderActiveFilters(
			context = this,
			chipGroup = binding.chipGroupActiveFilters,
			selectedType = viewModel.getSelectedType(),
			selectedTypes = viewModel.getSelectedTypes(),
			orderBy = viewModel.getOrderBy(),
			onApplyFilters = { types, order ->
				viewModel.applyFilters(types, order)
				renderActiveFilters()
				scrollToTopAndResetSaved()
			},
			onResetFilters = {
				viewModel.resetFilters()
				renderActiveFilters()
				scrollToTopAndResetSaved()
			}
		)

		binding.chipGroupActiveFilters.visibility =
			if (binding.chipGroupActiveFilters.childCount == 0) View.GONE else View.VISIBLE
		updateFiltersButtonTint()
	}

	private fun updateFiltersButtonTint() {
		val active = !viewModel.getSelectedType().isNullOrBlank() || viewModel.getOrderBy() != "number"
		val color = if (active) ContextCompat.getColor(this, R.color.cardAccent) else ContextCompat.getColor(this, android.R.color.white)
		binding.btnFilters.setColorFilter(color, PorterDuff.Mode.SRC_IN)
	}

	private fun updateFabVisibilityByScroll() {
		val lm = binding.recyclerView.layoutManager as? GridLayoutManager ?: return
		fabController.updateVisibilityByScroll(lm)
	}

	private fun hideFab() {
		fabController.hide()
	}

	private fun updateConnectionIcon(online: Boolean) {
		binding.ivConnectionStatus.setImageResource(if (online) R.drawable.ic_online else R.drawable.ic_offline)
		binding.tvConnectionStatus.text = if (online) getString(R.string.online_mode) else getString(R.string.offline_mode)
	}

	override fun onDestroy() {
		super.onDestroy()
		connectivityMonitor?.unregister()
	}

	private fun restoreScrollPositionIfNeeded() {
		if (scrollRestored) return
		val prefs = getSharedPreferences(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.PREFS_FILTERS, MODE_PRIVATE)
		val pos = prefs.getInt(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_RECYCLER_FIRST_POS, 0)
		val offset = prefs.getInt(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_RECYCLER_OFFSET, 0)
		(binding.recyclerView.layoutManager as? GridLayoutManager)?.scrollToPositionWithOffset(pos, offset)
		scrollRestored = true
	}

	private fun scrollToTopAndResetSaved() {
		(binding.recyclerView.layoutManager as? GridLayoutManager)?.scrollToPositionWithOffset(0, 0)
		getSharedPreferences(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.PREFS_FILTERS, MODE_PRIVATE)
			.edit() {
                putInt(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_RECYCLER_FIRST_POS, 0)
                    .putInt(
                        com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_RECYCLER_OFFSET,
                        0
                    )
            }
		scrollRestored = true
	}

	override fun onPause() {
		super.onPause()
		val lm = binding.recyclerView.layoutManager as? GridLayoutManager ?: return
		val first = lm.findFirstVisibleItemPosition()
		if (first != RecyclerView.NO_POSITION) {
			val firstView = binding.recyclerView.findViewHolderForAdapterPosition(first)?.itemView
			val offset = firstView?.top ?: 0
			getSharedPreferences(com.ursolgleb.pokcemon.presentation.util.PrefsKeys.PREFS_FILTERS, MODE_PRIVATE)
				.edit() {
                    putInt(
                        com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_RECYCLER_FIRST_POS,
                        first
                    )
                        .putInt(
                            com.ursolgleb.pokcemon.presentation.util.PrefsKeys.KEY_RECYCLER_OFFSET,
                            offset
                        )
                }
		}
	}
}


