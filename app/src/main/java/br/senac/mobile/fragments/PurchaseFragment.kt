package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import br.senac.mobile.databinding.FragmentPurchaseBinding
import br.senac.mobile.databinding.PurchaseCardBinding

class PurchaseFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPurchaseBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        val purchaseCardBinding = PurchaseCardBinding.inflate(layoutInflater)
        val purchaseCard2Binding = PurchaseCardBinding.inflate(layoutInflater)
        val purchaseCard3Binding = PurchaseCardBinding.inflate(layoutInflater)

        purchaseCardBinding.purchaseCardNameText.text = "Espada Justiceira dos Thundercats"
        purchaseCardBinding.purchaseCardValueText.text = "10PO"
        purchaseCard2Binding.purchaseCardNameText.text = "Espada Velha"
        purchaseCard2Binding.purchaseCardValueText.text = "1PO"
        purchaseCard3Binding.purchaseCardNameText.text = "Espada Nova"
        purchaseCard3Binding.purchaseCardValueText.text = "11PO"

        binding.purchaseFragLinearLayout.addView(purchaseCardBinding.root, 0)
        binding.purchaseFragLinearLayout.addView(purchaseCard2Binding.root, 0)
        binding.purchaseFragLinearLayout.addView(purchaseCard3Binding.root, 0)

        return binding.root
    }
}