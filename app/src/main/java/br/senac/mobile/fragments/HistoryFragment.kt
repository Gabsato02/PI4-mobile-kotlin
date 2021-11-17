package br.senac.mobile.fragments

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
import br.senac.mobile.utils.formatDate
import br.senac.mobile.utils.getResponseMessage
import br.senac.mobile.utils.setSnackbar
import com.google.android.material.snackbar.Snackbar
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
        orderList?.forEach { it ->
            val historyCardBinding = HistoryCardBinding.inflate(layoutInflater)
            val historyCardBindingId = it.id

            historyCardBinding.historyIdText.text = "#${it.id.toString()}"
            historyCardBinding.historyDateText.text = formatDate(it.created_at)
            historyCardBinding.historyNameText.text = it.store.name
            historyCardBinding.historyPriceText.text = it.order_items.sumOf {
                it.item.price * it.quantity
            }.toString()

            it.order_items?.forEachIndexed { index, product ->
                val historyItemCardBinding = HistoryItemCardBinding.inflate(layoutInflater)
                historyItemCardBinding.root.tag = it.id

                historyItemCardBinding.historyItemNameText.text = product.item.name
                historyItemCardBinding.historyItemPriceText.text = (product.item.price * product.quantity).toString()
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
                    setSnackbar(mainActivity, getResponseMessage(response.code()))
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                binding.orderProgressBar.visibility = View.GONE
                binding.swipeRefresh.isRefreshing = false
                setSnackbar(mainActivity, "Não foi possível conectar ao servidor.")
                Log.e("ERROR", "Falha ao executar serviço", t)
            }

        }

        API().order.getOrder().enqueue(callback)
        binding.orderProgressBar.visibility = View.VISIBLE
    }
}