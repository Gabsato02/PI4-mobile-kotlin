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
import br.senac.mobile.models.Item
import br.senac.mobile.services.API
import br.senac.mobile.utils.getResponseMessage
import br.senac.mobile.utils.setSnackbar
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

private const val CATALOG_TYPE_PARAM = "catalogType"
private const val CATALOG_ID_PARAM = "catalogId"
private const val CATALOG_QUERY_PARAM = "catalogQuery"

class StoreCatalogFragment : Fragment() {
    private var catalogType: String? = null
    private var catalogId: Int = 0
    private var catalogQuery: String? = null
    private lateinit var binding: FragmentStoreCatalogBinding
    private var callCategories: Call<Category>? = null
    private var callCategoriesItems: Call<List<Category>>? = null
    private var callItemsBySearch: Call<List<Item>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            catalogType = it.getString(CATALOG_TYPE_PARAM)
            catalogId = it.getInt(CATALOG_ID_PARAM)
            catalogQuery = it.getString(CATALOG_QUERY_PARAM)
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
        when(catalogType) {
            "category" -> getItemsListByCategory()
            "general" -> getAllItems()
            else -> getItemsListBySearch()
        }

    }

    private fun updateUiWithCategories(categoriesList: List<Category>) {
        if (layoutInflater != null) {
            binding.catalogContentLinearLayout.removeAllViews()

            categoriesList.forEach { category ->
                if (category.items.isEmpty()) return@forEach

                val catalogTitleBinding = CatalogTitleBinding.inflate(layoutInflater)
                catalogTitleBinding.catalogTitleText.text = category.name
                binding.catalogContentLinearLayout.addView(catalogTitleBinding.root)

                category.items.forEach { item ->
                    if (item.deleted_at.isNullOrEmpty()) {
                        updateItemsList(item)
                    }
                }
            }
        }
    }

    private fun updateUiWithSearch(itemsList: List<Item>) {
        if (layoutInflater != null) {
            binding.catalogContentLinearLayout.removeAllViews()
        }

        val catalogTitleBinding = CatalogTitleBinding.inflate(layoutInflater)
        catalogTitleBinding.catalogTitleText.text = "${itemsList.size} resultados para: $catalogQuery"
        binding.catalogContentLinearLayout.addView(catalogTitleBinding.root)

        itemsList.forEach { item -> updateItemsList(item) }
    }

    private fun updateItemsList(item: Item) {
        val catalogItemBinding = CatalogItemBinding.inflate(layoutInflater)
        catalogItemBinding.catalogNameText.text = item.name
        catalogItemBinding.catalogDescriptionText.text = item.description
        catalogItemBinding.catalogPriceText.text = "${item.price} PO"

        catalogItemBinding.root.setOnClickListener {
            val fragment = ItemFragment.newInstance(item.id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack("home").commit()
            true
        }

        Picasso
            .get()
            .load("${API().baseUrl}image/item/${item.id}")
            .into(
                catalogItemBinding.catalogItemImage,
                object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        catalogItemBinding.catalogItemImageProgressBar.visibility =
                            View.GONE
                        catalogItemBinding.catalogItemImage.visibility = View.VISIBLE
                    }

                    override fun onError(e: Exception?) {
                        catalogItemBinding.catalogItemImageProgressBar.visibility =
                            View.GONE
                        catalogItemBinding.catalogItemImage.visibility = View.VISIBLE
                    }
                })
        binding.catalogContentLinearLayout.addView(catalogItemBinding.root)
    }

    private fun getItemsListByCategory() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<Category> {
            override fun onResponse(call: Call<Category>, response: Response<Category>) {
                binding.catalogProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val categoryList = mutableListOf<Category>()
                    response.body()?.let { categoryList.add(it) }
                    categoryList?.let { updateUiWithCategories(categoryList) }
                } else {
                    setSnackbar(mainActivity, getResponseMessage(response.code()))
                }
            }

            override fun onFailure(call: Call<Category>, t: Throwable) {
                if (!call.isCanceled) {
                    binding.catalogProgressBar.visibility = View.GONE
                    setSnackbar(mainActivity, "Não foi possível conectar o servidor.")
                    Log.e("ERROR", "Falha ao executar serviço", t)
                }
            }
        }

        callCategories = API().category.getCategoryWithItems(catalogId)
        callCategories?.enqueue(callback)
        binding.catalogProgressBar.visibility = View.VISIBLE
    }


    private fun getAllItems() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                binding.catalogProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val categoriesList = response.body()
                    categoriesList?.let { updateUiWithCategories(categoriesList) }
                } else {
                    setSnackbar(mainActivity, getResponseMessage(response.code()))
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                if (!call.isCanceled) {
                    binding.catalogProgressBar.visibility = View.GONE
                    setSnackbar(mainActivity, "Não foi possível conectar ao servidor.")
                    Log.e("ERROR", "Falha ao executar serviço", t)
                }
            }
        }

        callCategoriesItems = API().category.getCategories(true)
        callCategoriesItems?.enqueue(callback)
        binding.catalogProgressBar.visibility = View.VISIBLE
    }


    private fun getItemsListBySearch() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<List<Item>> {
            override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                binding.catalogProgressBar.visibility = View.GONE
                if (response.isSuccessful) {
                    val itemsList = response.body()
                    itemsList?.let { updateUiWithSearch(itemsList) }
                } else {
                    val catalogTitleBinding = CatalogTitleBinding.inflate(layoutInflater)
                    catalogTitleBinding.catalogTitleText.text = "Não foram encontrados resultados para: $catalogQuery"
                    binding.catalogContentLinearLayout.addView(catalogTitleBinding.root)
                }
            }

            override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                if (!call.isCanceled) {
                    binding.catalogProgressBar.visibility = View.GONE
                    setSnackbar(mainActivity, "Não foi possível conectar ao servidor.")
                    Log.e("ERROR", "Falha ao executar serviço", t)
                }
            }
        }

        callItemsBySearch = catalogQuery?.let { API().item.getItems(it) }
        callItemsBySearch?.enqueue(callback)
        binding.catalogProgressBar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        callCategoriesItems?.cancel()
        callCategories?.cancel()
        callItemsBySearch?.cancel()
    }

    companion object {
        @JvmStatic fun newInstance(catalogType: String, catalogId: Int, catalogQuery: String) =
            StoreCatalogFragment().apply {
                arguments = Bundle().apply {
                    putString(CATALOG_TYPE_PARAM, catalogType)
                    putInt(CATALOG_ID_PARAM, catalogId)
                    putString(CATALOG_QUERY_PARAM, catalogQuery)
                }
            }
    }
}