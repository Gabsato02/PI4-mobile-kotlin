package br.senac.mobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import br.senac.mobile.databinding.FragmentItemBinding
import br.senac.mobile.databinding.TraitsCharacCardBinding
import br.senac.mobile.models.Item
import br.senac.mobile.services.API
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

private const val ARG_PARAM1 = "itemId"

class ItemFragment: Fragment() {
    private var itemId: Int = 0
    private lateinit var binding: FragmentItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            itemId = it.getInt(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentItemBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getSingleItem()
    }

    private fun updateUI(item: Item) {
        binding.itemFragNameText.text = item.name
        binding.itemFragPriceText.text = "${item.price} PO"
        binding.itemFragDescriptionText.text = item.description
        binding.itemFragTraitLabel.text = "Traços"
        binding.itemFragCharacteristicLabel.text = "Características"
        binding.itemFragmentBagButton.visibility = View.VISIBLE

        item.traits.forEach {
            val traitsCard  = TraitsCharacCardBinding.inflate(layoutInflater)
            traitsCard.traitsCharacCardNameText.text = it.name
            traitsCard.traitsCharacCardDescriptionText.text = it.description
            binding.itemFragTraitLinearLayout.addView(traitsCard.root)
        }

        item.characteristics.forEach {
            val characteristicCard = TraitsCharacCardBinding.inflate(layoutInflater)
            characteristicCard.traitsCharacCardNameText.text = "${it.name} - ${it.characteristics_value}"
            characteristicCard.traitsCharacCardDescriptionText.text = it.description
            binding.itemFragCharacteristicLinearLayout.addView(characteristicCard.root)
        }

        Picasso
            .get()
            .load("${API().baseUrl}image/item/${item.id}")
            .into(binding.itemFragCardImageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    binding.itemFragCardImageProgressBar.visibility = View.GONE
                }

                override fun onError(e: Exception?) {
                    binding.itemFragCardImageProgressBar.visibility = View.GONE
                }
            })

        binding.itemFragmentBagButton.setOnClickListener {
            val fragment = CartFragment()
            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack("home").commit()
            true
        }
    }

    private fun getSingleItem() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                binding.itemFragCardProgressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val item = response.body()
                    item?.let { updateUI(item) }
                } else {
                    Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                        "Não é possível atualizar os dados.",
                        Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                binding.itemFragCardProgressBar.visibility = View.GONE

                Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                    "Não foi possível conectar ao servidor.",
                    Snackbar.LENGTH_LONG).show()
                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        API().item.getItem(itemId).enqueue(callback)
        binding.itemFragCardProgressBar.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic fun newInstance(itemId: Int) =
            ItemFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, itemId)
                }
            }
    }
}