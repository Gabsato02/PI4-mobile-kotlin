package br.senac.mobile.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import br.senac.mobile.R
import br.senac.mobile.models.Category
import br.senac.mobile.services.API
import com.squareup.picasso.Picasso
import kotlin.collections.ArrayList
import kotlin.reflect.KFunction1

public class CategoryCardAdapter(
    val context: Context,
    private val categoryList: ArrayList<Category>,
    private val setOnClickedCategory: KFunction1<Int, Unit>): BaseAdapter() {

    override fun getItem(position: Int): Any {
        return categoryList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return categoryList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view: View = View.inflate(context, R.layout.category_card, null)
        var text: TextView = view.findViewById(R.id.cardTextView)
        var image: ImageView = view.findViewById(R.id.cardImageView)
        var category: Category = categoryList.get(position)
        text.text = category.name

        Picasso
            .get()
            .load("${API().baseUrl}image/category/${category.id}")
            .into(image)

        view.setOnClickListener {
            setOnClickedCategory(category.id)
        }

        return view
    }
}