package br.senac.mobile.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.senac.mobile.R
import br.senac.mobile.adapters.CategoryCardAdapter
import br.senac.mobile.databinding.*
import br.senac.mobile.models.Category
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.services.API
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
            val fragment = StoreCatalogFragment.newInstance("general", 0)
            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack("home").commit()
            true
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getCategoryList()
    }

    private fun updateUI(categoryList: List<Any>?) {
        // Adapter
        val categoryAdapter: CategoryCardAdapter? = activity?.let { CategoryCardAdapter(it,
            categoryList!! as ArrayList<Category>, ::setOnClickedCategory
        ) }

        binding.homeCardListGrid.adapter = categoryAdapter
    }

    private fun setOnClickedCategory(id: Int) {
        val catalogType = "category"
        val fragment = StoreCatalogFragment.newInstance(catalogType, id)
        parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
            .addToBackStack("home").commit()
        true
    }

    private fun getCategoryList() {
        val mainActivity = activity as AppCompatActivity

        val callback = object: Callback<List<Category>> {
            override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                binding.homeProgressBar.visibility = View.GONE

                if (response.isSuccessful) {
                    val categoryList = response.body()
                    categoryList?.let { updateUI(categoryList) }
                } else {
                    Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                        "Não é possível atualizar os dados.",
                        Snackbar.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                binding.homeProgressBar.visibility = View.GONE

                Snackbar.make(mainActivity.findViewById(R.id.mainConstraintLayout),
                    "Não foi possível conectar ao servidor.",
                    Snackbar.LENGTH_LONG).show()
                Log.e("ERROR", "Falha ao executar serviço", t)
            }
        }

        API().category.getCategory().enqueue(callback)
        binding.homeProgressBar.visibility = View.VISIBLE
    }
}