package br.senac.mobile.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import br.senac.mobile.R
import br.senac.mobile.databinding.CategoryCardBinding
import br.senac.mobile.fragments.StoreCatalogFragment
import br.senac.mobile.models.Category
import br.senac.mobile.services.API
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class CategoryRecyclerViewAdapter(private val categoryList: List<Category>, private val fragmentManagger: FragmentManager)
    : RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryCardViewHolder>() {
    class CategoryCardViewHolder(private val binding: CategoryCardBinding,private val fragmentManagger: FragmentManager)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(category: Category) {
            binding.categoryCardTextView.text = category.name
            Picasso
                .get()
                .load("${API().baseUrl}image/category/${category.id}")
                .into(binding.categoryCardImageView, object : Callback {
                    override fun onSuccess() {
                        binding.categoryCardProgressBar.visibility = View.GONE
                    }

                    override fun onError(e: Exception?) {
                        binding.categoryCardProgressBar.visibility = View.GONE
                    }
                })

            binding.root.setOnClickListener {
                val fragment = StoreCatalogFragment.newInstance("category", category.id, "")
                fragmentManagger.beginTransaction().replace(R.id.mainFragmentContainer, fragment)
                    .addToBackStack("home").commit()
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryCardViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val cardBinding = CategoryCardBinding.inflate(layoutInflater, parent, false)
        return CategoryCardViewHolder(cardBinding, fragmentManagger)
    }

    override fun onBindViewHolder(holder: CategoryCardViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }

    override fun getItemCount(): Int {
        return categoryList.size
    }
}