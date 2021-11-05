package br.senac.mobile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import br.senac.mobile.R
import br.senac.mobile.databinding.CatalogItemBinding
import br.senac.mobile.databinding.CatalogTitleBinding
import br.senac.mobile.databinding.FragmentStoreCatalogBinding

private const val ARG_PARAM1 = "catalogType"
private const val ARG_PARAM2 = "catalogId"

class StoreCatalogFragment : Fragment() {
    private var catalogType: String? = null
    private var catalogId: Int? = null

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
        val binding = FragmentStoreCatalogBinding.inflate(inflater, container, false)

        val mainActivity = activity as AppCompatActivity
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mainActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow)

        val catalogTitleBinding = CatalogTitleBinding.inflate(layoutInflater)
        val catalogTitleBinding2 = CatalogTitleBinding.inflate(layoutInflater)

        catalogTitleBinding.catalogTitleText.text = "Espadas"
        catalogTitleBinding2.catalogTitleText.text = "Armaduras"

        val catalogItemBinding = CatalogItemBinding.inflate(layoutInflater)
        val catalogItemBinding2 = CatalogItemBinding.inflate(layoutInflater)
        val catalogItemBinding3 = CatalogItemBinding.inflate(layoutInflater)

        catalogItemBinding.catalogItemImage.setImageResource(R.drawable.sword)
        catalogItemBinding.catalogNameText.text = "Espada Bastarda"
        catalogItemBinding.catalogPriceText.text = "12 PO"
        catalogItemBinding.catalogDescriptionText.text = "É uma espada medieval, que possui esse nome por não se encaixar exatamente nem no grupo de espadas de duas mãos, nem do de espadas de uma mão"

        catalogItemBinding2.catalogItemImage.setImageResource(R.drawable.sword)
        catalogItemBinding2.catalogNameText.text = "Espada Bastarda"
        catalogItemBinding2.catalogPriceText.text = "12 PO"
        catalogItemBinding2.catalogDescriptionText.text = "É uma espada medieval, que possui esse nome por não se encaixar exatamente nem no grupo de espadas de duas mãos, nem do de espadas de uma mão"
        catalogItemBinding2.catalogItemNamePriceLinearLayout.setBackgroundResource(R.color.transparent)

        catalogItemBinding3.catalogItemImage.setImageResource(R.drawable.sword)
        catalogItemBinding3.catalogNameText.text = "Espada Bastarda"
        catalogItemBinding3.catalogPriceText.text = "12 PO"
        catalogItemBinding3.catalogDescriptionText.text = "É uma espada medieval, que possui esse nome por não se encaixar exatamente nem no grupo de espadas de duas mãos, nem do de espadas de uma mão"
        catalogItemBinding3.catalogItemNamePriceLinearLayout.setBackgroundResource(R.color.transparent)

        binding.catalogContentLinearLayout.addView(catalogTitleBinding.root, 0)
        binding.catalogContentLinearLayout.addView(catalogItemBinding.root, 1)
        binding.catalogContentLinearLayout.addView(catalogItemBinding2.root, 2)

        binding.catalogContentLinearLayout.addView(catalogTitleBinding2.root, 0)
        binding.catalogContentLinearLayout.addView(catalogItemBinding3.root, 1)

        binding.catalogContentLinearLayout.setOnClickListener {
            val fragment = ItemFragment()
            parentFragmentManager.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                .addToBackStack("home").commit()
            true
        }

        return binding.root
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