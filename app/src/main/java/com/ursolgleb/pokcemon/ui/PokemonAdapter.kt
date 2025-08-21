package com.ursolgleb.pokcemon.ui

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import android.util.TypedValue
import com.ursolgleb.pokcemon.presentation.model.PokemonUi
import com.ursolgleb.pockemon.databinding.ItemPokemonBinding
import androidx.core.graphics.toColorInt
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.chip.Chip
import java.util.Locale
import com.ursolgleb.pokcemon.presentation.util.colorForType

class PokemonAdapter :
    ListAdapter<PokemonUi, PokemonAdapter.PokemonViewHolder>(PokemonDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val binding = ItemPokemonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PokemonViewHolder(private val binding: ItemPokemonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pokemon: PokemonUi) {
            binding.pokemonName.text = pokemon.title
            Glide.with(binding.pokemonImage.context)
                .load(pokemon.imageUrl)
                .into(binding.pokemonImage)
            applyBackgroundForTypes(pokemon)
        }

        private fun applyBackgroundForTypes(p: PokemonUi) {
            val types = p.types
            val card = binding.cardContainer
            val inner = binding.cardInner
            // Чипы типов
            binding.chipTypes.removeAllViews()
            types.take(3).forEach { t ->
                val label = t.toString().uppercase(Locale.ROOT)
                val chip = Chip(binding.root.context).apply {
                    text = label
                    isClickable = false
                    isCheckable = false
                    setTextColor(Color.WHITE)
                    chipBackgroundColor =
                        ColorStateList.valueOf(colorForType(t))
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    try {
                        this.javaClass.getMethod("setEnsureMinTouchTargetSize", Boolean::class.java)
                            .invoke(this, false)
                    } catch (_: Throwable) {
                    }
                    try {
                        this.javaClass.getMethod("setChipMinHeight", Float::class.java)
                            .invoke(this, 0f)
                    } catch (_: Throwable) {
                    }
                    minHeight = dp(14)
                    includeFontPadding = false
                    setLineSpacing(0f, 1f)
                    chipStartPadding = 15f
                    chipEndPadding = 15f
                    textStartPadding = 0f
                    textEndPadding = 0f
                    setPadding(0, 10, 0, 10)

                }
                binding.chipTypes.addView(chip)
            }
            if (types.size > 3) {
                val moreChip = Chip(binding.root.context).apply {
                    text = "…"
                    isClickable = false
                    isCheckable = false
                    setTextColor(Color.WHITE)
                    chipBackgroundColor =
                        ColorStateList.valueOf("#808080".toColorInt())
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 10f)
                    try {
                        this.javaClass.getMethod("setEnsureMinTouchTargetSize", Boolean::class.java)
                            .invoke(this, false)
                    } catch (_: Throwable) {
                    }
                    try {
                        this.javaClass.getMethod("setChipMinHeight", Float::class.java)
                            .invoke(this, 0f)
                    } catch (_: Throwable) {
                    }
                    minHeight = dp(14)
                    includeFontPadding = false
                    setLineSpacing(0f, 1f)
                    chipStartPadding = 0f
                    chipEndPadding = 0f
                    textStartPadding = 0f
                    textEndPadding = 0f
                    iconStartPadding = 0f
                    iconEndPadding = 0f
                    setPadding(0, 0, 0, 0)
                }
                binding.chipTypes.addView(moreChip)
            }
            when {
                types.isEmpty() -> {
                    card.setCardBackgroundColor("#607D8B".toColorInt())
                    inner.background = null
                    card.cardElevation = 6f
                    inner.foreground = null
                }

                types.size == 1 -> {
                    card.setCardBackgroundColor(colorForType(types[0]))
                    inner.background = null
                    card.cardElevation = 6f
                    inner.foreground = null
                }

                else -> {
                    val c1 = colorForType(types[0])
                    val c2 = colorForType(types[1])
                    val colors = if (types.size >= 3) intArrayOf(
                        c1,
                        c2,
                        colorForType(types[2])
                    ) else intArrayOf(c1, c2)
                    val gd = GradientDrawable(
                        GradientDrawable.Orientation.TL_BR,
                        colors
                    )
                    gd.cornerRadius = 16f * inner.resources.displayMetrics.density
                    card.setCardBackgroundColor(Color.TRANSPARENT)
                    inner.background = gd
                    card.cardElevation = 8f
                    inner.foreground = "#14000000".toColorInt().toDrawable()
                }
            }
        }

        private fun dp(value: Int): Int {
            val density = binding.root.resources.displayMetrics.density
            return (value * density).toInt()
        }
    }

    

    class PokemonDiffCallback : DiffUtil.ItemCallback<PokemonUi>() {
        override fun areItemsTheSame(oldItem: PokemonUi, newItem: PokemonUi): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PokemonUi, newItem: PokemonUi): Boolean {
            return oldItem == newItem
        }
    }
}


