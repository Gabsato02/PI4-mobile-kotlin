package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.senac.mobile.R
import br.senac.mobile.databinding.FragmentCartBinding
import br.senac.mobile.databinding.ShoppingCartCardBinding

class CartFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCartBinding.inflate(inflater, container, false)

        val cartCardBinding = ShoppingCartCardBinding.inflate(layoutInflater)
        cartCardBinding.itemImage.setImageResource(R.drawable.sword)
        binding.cartLinearLayout.addView(cartCardBinding.root)

        cartCardBinding.cartCard.setOnClickListener {
            val fragment = ItemFragment()
            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment).commit()
            true
        }

        binding.button.setOnClickListener {
            val fragment = PurchaseFragment()
            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment).commit()
            true
        }

        return binding.root
    }
}