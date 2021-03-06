package br.senac.mobile.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import br.senac.mobile.R
import br.senac.mobile.databinding.FragmentHistoryBinding
import br.senac.mobile.databinding.HistoryCardBinding
import br.senac.mobile.databinding.HistoryItemCardBinding
import br.senac.mobile.models.Order
import br.senac.mobile.services.API
import br.senac.mobile.services.LOGIN_FILE
import br.senac.mobile.utils.formatDate
import br.senac.mobile.utils.setSnackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class HistoryFragment : Fragment() {
    lateinit var binding: FragmentHistoryBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        binding.swipeRefresh.setOnRefreshListener {
            getOrderList()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getOrderList()
    }

    private fun updateUI(orderList: List<Order>) {
        binding.historyLinearLayout.removeAllViews()
        if (orderList.isNotEmpty()) {
            orderList?.forEach { it ->
                val historyCardBinding = HistoryCardBinding.inflate(layoutInflater)
                val historyCardBindingId = it.id

                historyCardBinding.historyIdText.text = "#${it.id.toString()}"
                historyCardBinding.historyDateText.text = formatDate(it.created_at)
                historyCardBinding.historyNameText.text = it.store.name
                val totalPrice = it.order_items.sumOf {
                    it.item.price * it.quantity
                }
                historyCardBinding.historyPriceText.text = "$totalPrice PO"

                    it.order_items?.forEachIndexed { index, product ->
                    val historyItemCardBinding = HistoryItemCardBinding.inflate(layoutInflater)
                    historyItemCardBinding.root.tag = it.id

                    historyItemCardBinding.historyItemNameText.text = "${product.item.name} (x${product.quantity})"
                    val itemTotalPrice = (product.item.price * product.quantity)
                        historyItemCardBinding.historyItemPriceText.text = "$itemTotalPrice PO"

                        Picasso
                        .get()
                        .load("${API().baseUrl}image/item/${product.item.id}")
                        .error(R.drawable.logo)
                        .into(historyItemCardBinding.historyItemImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                historyItemCardBinding.historyProgressBar.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                historyItemCardBinding.historyProgressBar.visibility = View.GONE
                            }
                        })

                    binding.historyLinearLayout.addView(historyItemCardBinding.root, index)
                }

                historyCardBinding.historyLinearLayout.setOnClickListener {
                    binding.historyLinearLayout.forEach { itemView ->
                        if (itemView.tag == historyCardBindingId) {
                            if (itemView.visibility == View.VISIBLE) {
                                itemView.visibility = View.GONE
                            } else {
                                itemView.visibility = View.VISIBLE
                            }
                        }
                    }
                }

                binding.historyLinearLayout.addView(historyCardBinding.root, 0)
                binding.emptyHistoryCartText.visibility = View.GONE
            }
        } else {
            binding.emptyHistoryCartText.visibility = View.VISIBLE
        }
    }

    private fun getOrderList() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                binding.orderProgressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false

                if (response.isSuccessful) {
                    val orderList = response.body()
                    orderList?.let { updateUI(orderList) }
                } else {
                    binding.emptyHistoryCartText.visibility = View.VISIBLE
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                binding.orderProgressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                setSnackbar(mainActivity, "N??o foi poss??vel conectar ao servidor.")
                Log.e("ERROR", "Falha ao executar servi??o", t)
            }

        }

        val preferences = mainActivity.getSharedPreferences(LOGIN_FILE, Context.MODE_PRIVATE)
        val userId = preferences.getString("userId", "") as String
        API().order.getOrder(userId.toInt()).enqueue(callback)
        binding.orderProgressBar.visibility = View.VISIBLE
    }
}