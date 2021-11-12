package br.senac.mobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import br.senac.mobile.R
import br.senac.mobile.databinding.*
import br.senac.mobile.models.Category
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.adapters.CategoryRecyclerViewAdapter
import br.senac.mobile.services.API
import br.senac.mobile.utils.GridSpacingItemDecoration
import br.senac.mobile.utils.getResponseMessage
import br.senac.mobile.utils.setSnackbar
import com.google.android.material.snackbar.Snackbar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding.homeSpotlightCardImage.setOnClickListener {
            val fragment = StoreCatalogFragment.newInstance("general", 0, "")
            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack("home").commit()
            true
        }
        binding.homeCardListGrid.adapter = CategoryRecyclerViewAdapter(listOf<Category>(), parentFragmentManager)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getCategoryList()
    }

    private fun updateUI(categoryList: List<Category>) {
        binding.homeCardListGrid.adapter = CategoryRecyclerViewAdapter(categoryList, parentFragmentManager)
        binding.homeCardListGrid.addItemDecoration(GridSpacingItemDecoration(2, 16, false))
    }

    private fun getCategoryList() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                binding.homeProgressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val categoryList = response.body()?.filter {
                        it.deleted_at.isNullOrEmpty() && it.items.isNotEmpty()
                    }
                    categoryList?.let { updateUI(categoryList) }
                } else {
                    setSnackbar(mainActivity, getResponseMessage(response.code()))
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                binding.homeProgressBar.visibility = View.GONE
                setSnackbar(mainActivity, "Não é possível conectar ao servidor.")
                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        API().category.getCategories(true).enqueue(callback)
        binding.homeProgressBar.visibility = View.VISIBLE
    }
}