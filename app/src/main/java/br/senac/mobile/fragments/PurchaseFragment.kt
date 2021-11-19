package br.senac.mobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import br.senac.mobile.databinding.FragmentPurchaseBinding
import br.senac.mobile.databinding.PurchaseCardBinding
import br.senac.mobile.models.Order
import br.senac.mobile.services.API
import br.senac.mobile.utils.getResponseMessage
import br.senac.mobile.utils.setSnackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

private const val ARG_PARAM1 = "orderId"

class PurchaseFragment : Fragment() {
    private var orderId: Int = 0
    private lateinit var binding: FragmentPurchaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPurchaseBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getPurchaseRecord()
    }

    private fun updateUI(orderList: List<Order>) {
        binding.purchaseFragLinearLayout.removeAllViews()
        val totalPrice = orderList[0].order_items.sumOf {
            it.item.price * it.quantity
        }

        binding.purchaseCompletion.text = "Muito obrigado aventureiro, compra finalizada!"
        binding.purchaseTotalText.text = "Total: ${totalPrice.toString()} PO"
        binding.purchaseFragLinearLayout.addView(binding.purchaseCompletion, 0)

        orderList?.forEach { it ->

            it.order_items?.forEach { product ->
                val purchaseCardBinding = PurchaseCardBinding.inflate(layoutInflater)

                purchaseCardBinding.purchaseCardNameText.text = product.item.name
                purchaseCardBinding.purchaseCardValueText.text = (product.item.price * product.quantity).toString()

                Picasso
                    .get()
                    .load("${API().baseUrl}image/item/${product.item.id}")
                    .error(R.drawable.logo)
                    .into(purchaseCardBinding.purchaseCardImageView, object : com.squareup.picasso.Callback {
                        override fun onSuccess() {
                            purchaseCardBinding.purchaseImageProgressBar.visibility = View.GONE
                        }

                        override fun onError(e: Exception?) {
                            purchaseCardBinding.purchaseImageProgressBar.visibility = View.GONE
                        }
                    })

                binding.purchaseFragLinearLayout.addView(purchaseCardBinding.root)
            }
            binding.purchaseFragLinearLayout.addView(binding.purchaseTotalText)
        }
    }

    private fun getPurchaseRecord() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<List<Order>> {
            override fun onResponse(call: Call<List<Order>>, response: Response<List<Order>>) {
                binding.purchaseProgressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val orderList = response.body()
                    orderList?.let { updateUI(orderList.filter {
                        it.id == orderId
                    }) }
                } else {
                    setSnackbar(mainActivity, getResponseMessage(response.code()))
                    Log.e("ERROR", "Falha ao receber a resposta do serviço")
                }
            }

            override fun onFailure(call: Call<List<Order>>, t: Throwable) {
                binding.purchaseProgressBar.visibility = View.GONE
                setSnackbar(mainActivity, "Não foi possível conectar ao servidor.")
                Log.e("ERROR", "Falha ao executar serviço", t)
            }

        }

        API().order.getOrder().enqueue(callback)
        binding.purchaseProgressBar.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic fun newInstance(orderId: Int) =
            PurchaseFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, orderId)
                }
            }
    }
}