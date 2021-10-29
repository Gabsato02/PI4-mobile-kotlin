package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import br.senac.mobile.databinding.FragmentHistoryBinding
import br.senac.mobile.databinding.HistoryCardBinding

class HistoryFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val historyCardBinding = HistoryCardBinding.inflate(layoutInflater)
        val historyCardBinding2 = HistoryCardBinding.inflate(layoutInflater)
        val historyCardBinding3 = HistoryCardBinding.inflate(layoutInflater)

        historyCardBinding.historyIdText.text = "#ckdt8"
        historyCardBinding.historyDateText.text = "21/05/2021"
        historyCardBinding.historyNameText.text = "Espada Bastarda"
        historyCardBinding.historyPriceText.text = "12 PO"
        historyCardBinding.historyLinearLayout.setOnClickListener {
            val isVisible = historyCardBinding.historyItemLinearLayout.isVisible
            if (isVisible) {
                historyCardBinding.historyItemLinearLayout.visibility = View.GONE
            } else {
                historyCardBinding.historyItemLinearLayout.visibility = View.VISIBLE
            }
        }

        binding.historyLinearLayout.addView(historyCardBinding.root, 0)

        return binding.root
    }
}