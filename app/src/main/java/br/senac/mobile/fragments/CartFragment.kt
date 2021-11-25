package br.senac.mobile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.size
import br.senac.mobile.R
import br.senac.mobile.database.Database
import br.senac.mobile.databinding.FragmentCartBinding
import br.senac.mobile.databinding.ShoppingCartCardBinding
import br.senac.mobile.services.API
import com.squareup.picasso.Picasso
import java.lang.Exception
import kotlin.concurrent.thread
import br.senac.mobile.models.CustomResponse
import br.senac.mobile.models.OrderAddItem
import br.senac.mobile.models.OrderCreate
import br.senac.mobile.services.LOGIN_FILE
import br.senac.mobile.utils.setSnackbar
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
            if (binding.cartLinearLayout.size != 0) {
                createOrder()
            } else {
                setSnackbar(mainActivity, "Sacola vazia, adicione algum item")
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getCartItems()
    }

    private fun addItemsToOrder(order_id: String) {
        val mainActivity = activity as AppCompatActivity
        val db = Database().databaseBuilder(mainActivity.applicationContext)

        try {
            thread {
                val cartItems = db.cartDao().list()

                cartItems.forEach {
                    val item = OrderAddItem (
                        order_id = order_id.toInt(),
                        item_id = it.itemId,
                        price = it.price.toInt(),
                        quantity = it.quantity.toInt()
                    )

                    val callback = object: Callback<CustomResponse> {
                        override fun onResponse(call: Call<CustomResponse>, response: Response<CustomResponse>) {
                            if (!response.isSuccessful) {
                                setSnackbar(mainActivity, "Não é possível finalizar a compra")
                            }
                        }

                        override fun onFailure(call: Call<CustomResponse>, t: Throwable) {
                            setSnackbar(mainActivity, "Não foi possível conectar ao servidor.")
                            Log.e("ERROR", "Falha ao executar serviço", t)
                        }
                    }

                    API().order.postAddItemToOrder(item).enqueue(callback)
                }

                db.cartDao().deleteAll()
                val fragment = PurchaseFragment.newInstance(order_id.toInt())
                parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                    .addToBackStack("cart").commit()
                true
            }
        } catch (error: Error) {
            setSnackbar(mainActivity, "Não é possível finalizar a compra")
        }
    }

    private fun createOrder() {
        val mainActivity = activity as AppCompatActivity
        val preferences = mainActivity.getSharedPreferences(LOGIN_FILE, Context.MODE_PRIVATE)
        val userId = preferences.getString("userId", "") as String

        val orderCreateBody = OrderCreate(1, userId.toInt())

        val callback = object: Callback<Int> {
            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                binding.cartProgressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    addItemsToOrder(response.body().toString())
                } else {
                    setSnackbar(mainActivity, "Não é possível finalizar a compra")
                    Log.e("ERROR", response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<Int>, t: Throwable) {
                binding.cartProgressBar.visibility = View.GONE

                setSnackbar(mainActivity, "Não foi possível conectar ao servidor.")
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
                            cartCardBinding.itemPriceText.text = "${it.price} PO"
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

                            cartCardBinding.itemLess.setOnClickListener { _ ->
                                val quantity = (it.quantity.toInt() - 1)
                                if (quantity > 0) {
                                    thread {
                                        it.id?.let { itemId ->
                                            db.cartDao().update(quantity, itemId)
                                        }
                                    }
                                    getCartItems()
                                }
                            }

                            cartCardBinding.itemMore.setOnClickListener { _ ->
                                val quantity = (it.quantity.toInt() + 1)
                                thread {
                                    it.id?.let { itemId ->
                                        db.cartDao().update(quantity, itemId)
                                    }
                                }
                                getCartItems()
                            }

                            binding.cartLinearLayout.addView(cartCardBinding.root, 0)
                        }
                        binding.cartProgressBar.visibility = View.GONE
                        binding.swipeRefresh.isRefreshing = false
                        binding.priceText.text = "Total: ${totalPrice.toString()} PO"
                        binding.emptyCartText.visibility = View.GONE
                    }
                } else {
                    binding.cartProgressBar.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                    binding.priceText.text = "Total: 0 PO"
                    binding.emptyCartText.visibility = View.VISIBLE
                }
            }
        }
    }
}
