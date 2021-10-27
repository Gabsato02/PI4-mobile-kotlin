package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.senac.mobile.adapters.CategoryAdapter
import br.senac.mobile.databinding.*
import br.senac.mobile.models.Category

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Adição manual
        val spotlightCardBinding = SpotlightCardBinding.inflate(layoutInflater)
        spotlightCardBinding.spotlightCardTitle.text = "Loja Principal"
        binding.homeLinearLayout.addView(spotlightCardBinding.root)

        val cardListBinding = CardListBinding.inflate(layoutInflater)
        cardListBinding.cardListTitle.text = "Categorias"

        // Adapter
        var arrayList: ArrayList<Category> = ArrayList()
        arrayList = setCategoryList()
        val categoryAdapter: CategoryAdapter? = activity?.let { CategoryAdapter(it, arrayList!!) }


        cardListBinding.cardListGrid.adapter = categoryAdapter
        binding.homeLinearLayout.addView(cardListBinding.root)

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