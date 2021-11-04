package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import br.senac.mobile.activities.main.MainActivity
import br.senac.mobile.databinding.FragmentItemBinding
import br.senac.mobile.databinding.TraitsCharacCardBinding

class ItemFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentItemBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        val itemFragTraitLinearLayout = binding.itemFragTraitLinearLayout
        val itemFragCharacteristicLinearLayout = binding.itemFragCharacteristicLinearLayout

        val traitsCard = TraitsCharacCardBinding.inflate(layoutInflater)
        val characteristicsCard = TraitsCharacCardBinding.inflate(layoutInflater)
        val characteristicsCard2 = TraitsCharacCardBinding.inflate(layoutInflater)
        val characteristicsCard3 = TraitsCharacCardBinding.inflate(layoutInflater)

        traitsCard.traitsCharacCardNameText.text = "Traço teste"
        traitsCard.traitsCharacCardDescriptionText.text = "Esse é o traço teste"

        itemFragTraitLinearLayout.addView(traitsCard.root)

        characteristicsCard.traitsCharacCardNameText.text = "Característica teste"
        characteristicsCard.traitsCharacCardDescriptionText.text = "Essa é a característica teste"
        characteristicsCard2.traitsCharacCardNameText.text = "Característica teste"
        characteristicsCard2.traitsCharacCardDescriptionText.text = "Essa é a característica teste"
        characteristicsCard3.traitsCharacCardNameText.text = "Característica teste"
        characteristicsCard3.traitsCharacCardDescriptionText.text = "Essa é a característica teste"

        itemFragCharacteristicLinearLayout.addView(characteristicsCard.root)
        itemFragCharacteristicLinearLayout.addView(characteristicsCard2.root)
        itemFragCharacteristicLinearLayout.addView(characteristicsCard3.root)

        return binding.root
    }
}