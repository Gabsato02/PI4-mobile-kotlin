package br.senac.mobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import br.senac.mobile.databinding.CatalogItemBinding
import br.senac.mobile.databinding.CatalogTitleBinding
import br.senac.mobile.databinding.FragmentStoreCatalogBinding
import br.senac.mobile.models.Category
import br.senac.mobile.services.API
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

private const val ARG_PARAM1 = "catalogType"
private const val ARG_PARAM2 = "catalogId"

class StoreCatalogFragment : Fragment() {
    private var catalogType: String? = null
    private var catalogId: Int = 0
    private lateinit var binding: FragmentStoreCatalogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            catalogType = it.getString(ARG_PARAM1)
            catalogId = it.getInt(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStoreCatalogBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getItemsListByCategory()
    }

    private fun updateUiWithCategory(category: Category) {
        binding.catalogContentLinearLayout.removeAllViews()

        val catalogTitleBinding = CatalogTitleBinding.inflate(layoutInflater)
        val categoryTitle = if (category.items.isEmpty()) "Não há resultados para: ${category.name}" else category.name
        catalogTitleBinding.catalogTitleText.text = categoryTitle
        binding.catalogContentLinearLayout.addView(catalogTitleBinding.root)

        if (category.items.isNotEmpty()) {
            category.items.forEach {
                if (it.deleted_at.isNullOrEmpty()) {
                    val catalogItemBinding = CatalogItemBinding.inflate(layoutInflater)
                    catalogItemBinding.catalogNameText.text = it.name
                    catalogItemBinding.catalogDescriptionText.text = it.description
                    catalogItemBinding.catalogPriceText.text = "${it.price} PO"
                    val itemId = it.id

                    catalogItemBinding.root.setOnClickListener {
                        val fragment = ItemFragment.newInstance(itemId)
                        parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                            .addToBackStack("home").commit()
                        true
                    }

                    Picasso
                        .get()
                        .load("${API().baseUrl}image/item/${it.id}")
                        .into(catalogItemBinding.catalogItemImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                catalogItemBinding.catalogItemImageProgressBar.visibility = View.GONE
                                catalogItemBinding.catalogItemImage.visibility = View.VISIBLE
                            }

                            override fun onError(e: Exception?) {
                                catalogItemBinding.catalogItemImageProgressBar.visibility = View.GONE
                                catalogItemBinding.catalogItemImage.visibility = View.VISIBLE
                            }
                        })
                    binding.catalogContentLinearLayout.addView(catalogItemBinding.root)
                }
            }
        }
    }

    private fun getItemsListByCategory() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                binding.catalogProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val category = response.body()
                    category?.let { updateUiWithCategory(category) }
                } else {
                    Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                        "Não é possível atualizar os dados.",
                        Snackbar.LENGTH_LONG).show()
                    println(response)
                }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                binding.catalogProgressBar.visibility = View.GONE
                Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                    "Não foi possível conectar ao servidor.",
                    Snackbar.LENGTH_LONG).show()
                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        API().category.getCategoryWithItems(catalogId).enqueue(callback)
        binding.catalogProgressBar.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic fun newInstance(catalogType: String, catalogId: Int) =
            StoreCatalogFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, catalogType)
                    putInt(ARG_PARAM2, catalogId)
                }
            }
    }
}