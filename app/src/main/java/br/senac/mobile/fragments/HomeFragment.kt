package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.senac.mobile.R
import br.senac.mobile.adapters.CategoryCardAdapter
import br.senac.mobile.databinding.*
import br.senac.mobile.models.Category
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        // Adapter
        var arrayList: ArrayList<Category> = ArrayList()
        arrayList = setCategoryList()
        val categoryAdapter: CategoryCardAdapter? = activity?.let { CategoryCardAdapter(it, arrayList!!) }

        binding.homeCardListGrid.adapter = categoryAdapter

        binding.homeSpotlightCardImage.setOnClickListener {
            val fragment = StoreCatalogFragment()
            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack("home").commit()
            true
        }

        binding.homeCardListGrid.onItemClickListener =
            OnItemClickListener { p0, p1, p2, p3 ->
                val fragment = StoreCatalogFragment()
                parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                    .addToBackStack("home").commit()
                true
            }

        return binding.root
    }

    private fun setCategoryList(): ArrayList<Category> {
        val arrayList: ArrayList<Category> = ArrayList()

        arrayList.add(Category(1, "Espadas"))
        arrayList.add(Category(2, "Escudos"))
        arrayList.add(Category(3, "Armaduras"))
        arrayList.add(Category(4, "Poções"))
        arrayList.add(Category(4, "Montarias"))
        arrayList.add(Category(4, "Machados"))
        arrayList.add(Category(4, "Utilidades"))
        arrayList.add(Category(4, "Provisões"))
        arrayList.add(Category(4, "Medicamentos"))

        return arrayList
    }
}