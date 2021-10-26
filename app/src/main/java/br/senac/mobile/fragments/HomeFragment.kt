package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.senac.mobile.databinding.*

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        val spotlightCardBinding = SpotlightCardBinding.inflate(layoutInflater)
        spotlightCardBinding.spotlightCardTitle.text = "Loja Principal"
        binding.homeLinearLayout.addView(spotlightCardBinding.root)

        val cardBinding = CardBinding.inflate(layoutInflater)
        cardBinding.cardTextView.text = "Espadas"

        val cardListBinding = CardListBinding.inflate(layoutInflater)
        cardListBinding.cardListTitle.text = "Categorias"
        cardListBinding.cardListRows.addView(cardBinding.root)

        binding.homeLinearLayout.addView(cardListBinding.root)

        return binding.root
    }
}