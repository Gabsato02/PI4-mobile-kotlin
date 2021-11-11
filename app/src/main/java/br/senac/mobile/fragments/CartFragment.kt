package br.senac.mobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import br.senac.mobile.R
import br.senac.mobile.database.Database
import br.senac.mobile.databinding.FragmentCartBinding
import br.senac.mobile.databinding.ShoppingCartCardBinding
import br.senac.mobile.services.API
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.concurrent.thread

class CartFragment : Fragment() {
    lateinit var binding: FragmentCartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        binding.swipeRefresh.setOnRefreshListener {
            getCartItems()
        }

//        val cartCardBinding = ShoppingCartCardBinding.inflate(layoutInflater)
//        val cartCardBinding2 = ShoppingCartCardBinding.inflate(layoutInflater)
//        val cartCardBinding3 = ShoppingCartCardBinding.inflate(layoutInflater)
//
//        val spinnerItems = arrayOf("1", "2", "3", "4", "5")
//        val adapter = context?.let {
//            ArrayAdapter(
//                it,
//                android.R.layout.simple_spinner_item,
//                spinnerItems
//            )
//        }
//        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//
//
//        cartCardBinding.itemImage.setImageResource(R.drawable.sword)
//        cartCardBinding.itemPriceText.text = "12 PO"
//        cartCardBinding.itemNameText.text = "Espada Bastarda"
//        cartCardBinding.quantitySpinner.adapter = adapter
//
//        cartCardBinding2.itemImage.setImageResource(R.drawable.sword)
//        cartCardBinding2.itemPriceText.text = "10 PO"
//        cartCardBinding2.itemNameText.text = "Espada Longa"
//        cartCardBinding2.quantitySpinner.adapter = adapter
//
//        cartCardBinding3.itemImage.setImageResource(R.drawable.sword)
//        cartCardBinding3.itemPriceText.text = "4 PP"
//        cartCardBinding3.itemNameText.text = "Espada Curta"
//        cartCardBinding3.quantitySpinner.adapter = adapter
//
//        binding.cartLinearLayout.addView(cartCardBinding.root, 0)
//        binding.cartLinearLayout.addView(cartCardBinding2.root, 1)
//        binding.cartLinearLayout.addView(cartCardBinding3.root, 2)
//
//
//        cartCardBinding.cartCard.setOnClickListener {
//            val fragment = ItemFragment.newInstance(1)
//            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
//                .addToBackStack("cart").commit()
//            true
//        }
//
//        binding.button.setOnClickListener {
//            val fragment = PurchaseFragment()
//            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
//                .addToBackStack("cart").commit()
//            true
//        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getCartItems()
    }

    private fun getCartItems() {
        binding.cartProgressBar.visibility = View.VISIBLE
        binding.cartLinearLayout.removeAllViews()
        val mainActivity = activity as AppCompatActivity
        val db = Database().databaseBuilder(mainActivity.applicationContext)

        thread {
            val cartItems = db.cartDao().list()
            var totalPrice = 0

            mainActivity.runOnUiThread {
                cartItems.forEach {
                    val cartCardBinding = ShoppingCartCardBinding.inflate(layoutInflater)
                    totalPrice += it.price.toInt() * it.quantity.toInt()

                    cartCardBinding.itemImage.setImageResource(R.drawable.sword)
                    cartCardBinding.itemNameText.text = it.name
                    cartCardBinding.itemPriceText.text = it.price
                    cartCardBinding.itemQuantityInput.setText(it.quantity)
                    Picasso
                        .get()
                        .load("${API().baseUrl}image/item/${it.itemId}")
                        .error(R.drawable.no_image)
                        .into(cartCardBinding.itemImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                cartCardBinding.cartImageProgressBar.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                cartCardBinding.cartImageProgressBar.visibility = View.GONE
                            }
                        })

                    cartCardBinding.itemDeleteText.setOnClickListener { _ ->
                        thread { it.id?.let { itemId -> db.cartDao().delete(itemId) } }
                        getCartItems()
                    }

                    cartCardBinding.itemQuantityInput.doOnTextChanged { text, start, before, count ->
                        // TODO Atualizar de forma reativa
                        if (!text.isNullOrEmpty()) {
                            val quantity = text.toString()
                            thread { it.id?.let { itemId ->
                                db.cartDao().update(quantity.toInt(), itemId)
                            } }
                            getCartItems()
                        }
                    }

                    binding.cartLinearLayout.addView(cartCardBinding.root, 0)
                    binding.cartProgressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
                binding.cartProgressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                binding.priceText.text = "Total: ${totalPrice.toString()} PO"
            }
        }
    }
}
