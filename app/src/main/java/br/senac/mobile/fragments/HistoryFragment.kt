package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import br.senac.mobile.R
import br.senac.mobile.databinding.FragmentHistoryBinding
import br.senac.mobile.databinding.HistoryCardBinding
import br.senac.mobile.databinding.HistoryItemCardBinding

class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        val historyCardBinding = HistoryCardBinding.inflate(layoutInflater)
        val historyCardBinding2 = HistoryCardBinding.inflate(layoutInflater)

        historyCardBinding.historyIdText.text = "#ckdt8"
        historyCardBinding.historyDateText.text = "21/05/2021"
        historyCardBinding.historyNameText.text = "A forja do Guerreiro"
        historyCardBinding.historyPriceText.text = "20 PO"

        historyCardBinding2.historyIdText.text = "#dacx7"
        historyCardBinding2.historyDateText.text = "01/07/2021"
        historyCardBinding2.historyNameText.text = "A forja do Guerreiro"
        historyCardBinding2.historyPriceText.text = "4 PO"

        val historyItemCardBinding = HistoryItemCardBinding.inflate(layoutInflater)
        val historyItemCardBinding2 = HistoryItemCardBinding.inflate(layoutInflater)
        val historyItemCardBinding3 = HistoryItemCardBinding.inflate(layoutInflater)

        historyCardBinding.historyLinearLayout.setOnClickListener {
            val isVisible1 = historyItemCardBinding.historyItemLinearLayout.isVisible
            val isVisible2 = historyItemCardBinding2.historyItemLinearLayout.isVisible
            if (isVisible1 && isVisible2) {
                historyItemCardBinding.historyItemLinearLayout.visibility = View.GONE
                historyItemCardBinding2.historyItemLinearLayout.visibility = View.GONE
            } else {
                historyItemCardBinding.historyItemLinearLayout.visibility = View.VISIBLE
                historyItemCardBinding2.historyItemLinearLayout.visibility = View.VISIBLE
            }
        }

        historyCardBinding2.historyLinearLayout.setOnClickListener {
            val isVisible3 = historyItemCardBinding3.historyItemLinearLayout.isVisible
            if (isVisible3) {
                historyItemCardBinding3.historyItemLinearLayout.visibility = View.GONE
            } else {
                historyItemCardBinding3.historyItemLinearLayout.visibility = View.VISIBLE
            }
        }

        historyItemCardBinding.historyItemImage.setImageResource(R.drawable.sword)
        historyItemCardBinding.historyItemNameText.text = "Espada Bastarda"
        historyItemCardBinding.historyItemPriceText.text = "12 PO"

        historyItemCardBinding2.historyItemImage.setImageResource(R.drawable.sword)
        historyItemCardBinding2.historyItemNameText.text = "Espada Longa"
        historyItemCardBinding2.historyItemPriceText.text = "8 PO"

        historyItemCardBinding3.historyItemImage.setImageResource(R.drawable.sword)
        historyItemCardBinding3.historyItemNameText.text = "Espada Curta"
        historyItemCardBinding3.historyItemPriceText.text = "4 PO"

        binding.historyLinearLayout.addView(historyCardBinding.root, 0)
        binding.historyLinearLayout.addView(historyItemCardBinding.root, 1)
        binding.historyLinearLayout.addView(historyItemCardBinding2.root, 2)

        binding.historyLinearLayout.addView(historyCardBinding2.root, 0)
        binding.historyLinearLayout.addView(historyItemCardBinding3.root, 1)
        return binding.root
    }
}