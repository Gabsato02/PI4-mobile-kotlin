package br.senac.mobile.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import br.senac.mobile.R
import br.senac.mobile.fragments.HomeFragment
import br.senac.mobile.models.Category
import kotlin.collections.ArrayList

public class CategoryAdapter(val context: Context, private val categoryList: ArrayList<Category>): BaseAdapter() {

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
        var view: View = View.inflate(context, R.layout.card, null)
        var text: TextView = view.findViewById(R.id.cardTextView)
        var category: Category = categoryList.get(position)
        text.text = category.name

        return view
    }
}