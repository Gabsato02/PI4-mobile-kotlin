package br.senac.mobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import br.senac.mobile.R
import br.senac.mobile.database.Database
import br.senac.mobile.databinding.FragmentCartBinding
import br.senac.mobile.databinding.ShoppingCartCardBinding
import br.senac.mobile.services.API
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.concurrent.thread
import android.view.inputmethod.EditorInfo
import br.senac.mobile.models.OrderCreate
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        binding.finishButton.setOnClickListener {
            createOrder()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getCartItems()
    }

    private fun addItemsToOrder(orderCreateBody: OrderCreate) {
        // ADICIONAR ITENS AO PEDIDO
        val fragment = PurchaseFragment()
        parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
            .addToBackStack("cart").commit()
        true
    }

    private fun createOrder() {
        val mainActivity = activity as AppCompatActivity
        // TODO O ID DEVE SER PASSADO DINAMICAMENTE
        val orderCreateBody = OrderCreate(1, 1)

        val callback = object: Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {

                if (response.isSuccessful) {
                    addItemsToOrder(orderCreateBody)
                } else {
                    Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                        "Não é possível finalizar a compra",
                        Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                binding.cartProgressBar.visibility = View.GONE

                Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                    "Não foi possível conectar ao servidor.",
                    Snackbar.LENGTH_LONG).show()
                Log.e("ERROR", "Falha ao executar serviço", t)
            }

        }

        API().order.postOrder(orderCreateBody).enqueue(callback)
        binding.cartProgressBar.visibility = View.VISIBLE
    }

    private fun getCartItems() {
        binding.cartProgressBar.visibility = View.VISIBLE
        binding.cartLinearLayout.removeAllViews()
        val mainActivity = activity as AppCompatActivity
        val db = Database().databaseBuilder(mainActivity.applicationContext)

        thread {
            val cartItems = db.cartDao().list()
            var currentCartList = emptyList<String>()
            var totalPrice = 0

            mainActivity.runOnUiThread {
                if (cartItems.isNotEmpty()) {
                    cartItems.forEach {
                        if (!currentCartList.contains(it.itemId.toString())) {
                            currentCartList += it.itemId.toString()
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
                                .into(
                                    cartCardBinding.itemImage,
                                    object : com.squareup.picasso.Callback {
                                        override fun onSuccess() {
                                            cartCardBinding.cartImageProgressBar.visibility = View.GONE
                                        }

                                        override fun onError(e: Exception?) {
                                            cartCardBinding.cartImageProgressBar.visibility = View.GONE
                                        }
                                    })

                            cartCardBinding.itemDeleteText.setOnClickListener { _ ->
                                thread { it.itemId?.let { itemId -> db.cartDao().delete(itemId) } }
                                getCartItems()
                            }

                            cartCardBinding.itemText.setOnClickListener { _ ->
                                val fragment = ItemFragment.newInstance(it.itemId)
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.mainFragmentContainer, fragment)
                                    .addToBackStack("cart").commit()
                                true
                            }

                            cartCardBinding.itemQuantityInput.doOnTextChanged { text, _, _, _ ->
                                if (!text.isNullOrEmpty()) {
                                    val quantity = text.toString()
                                    thread {
                                        it.id?.let { itemId ->
                                            db.cartDao().update(quantity.toInt(), itemId)
                                        }
                                    }
                                }
                            }

                            cartCardBinding.itemQuantityInput.setOnEditorActionListener { _, actionId, _ ->
                                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO) {
                                    getCartItems()
                                    true
                                }
                                false
                            }

                            binding.cartLinearLayout.addView(cartCardBinding.root, 0)
                        }
                        binding.cartProgressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        binding.priceText.text = "Total: ${totalPrice.toString()} PO"
                    }
                } else {
                    binding.cartProgressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.priceText.text = "Total: 0 PO"
                }
            }
        }
    }
}
