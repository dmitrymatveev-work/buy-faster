package com.matveyev.buyfaster.uicomponents

import android.content.Context
import android.database.DataSetObserver
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ListAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import com.matveyev.buyfaster.BR
import java.lang.Exception

class ObserverListAdapter<T>(
    private val observable: ObservableArrayList<T>,
    private val viewType: Int,
    private val parentVM: Any?,
    private val refreshFilter: () -> Unit
)
    : ListAdapter, Filterable where T: com.matveyev.buyfaster.vms.BaseVM {

    private var suggestions = mutableListOf<T>()
    private var callback = object : ObservableList.OnListChangedCallback<ObservableList<T>>() {
        override fun onChanged(sender: ObservableList<T>?) { }
        override fun onItemRangeChanged(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) { }
        override fun onItemRangeInserted(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) { }
        override fun onItemRangeMoved(
            sender: ObservableList<T>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) { }

        override fun onItemRangeRemoved(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
            refreshFilter()
        }
    }

    init {
        observable.addOnListChangedCallback(callback)
    }

    override fun getCount(): Int {
        return suggestions.size
    }

    override fun getItem(position: Int): Any {
        return suggestions[position] as Any
    }

    override fun getItemId(position: Int): Long {
        return suggestions[position].id
    }

    override fun getItemViewType(position: Int): Int {
        return viewType
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE)
        if(inflater is LayoutInflater) {
            val view = inflater
                .inflate(viewType, parent, false)

            val binding: ViewDataBinding? = DataBindingUtil.bind(view)
            binding?.also {
                it.setVariable(BR.viewModel, suggestions[position])
                it.setVariable(BR.parentViewModel, parentVM)
                it.executePendingBindings()
            }
            return view
        }
        throw Exception("Couldn't inflate the item")
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun hasStableIds(): Boolean {
        return false;
    }

    override fun isEmpty(): Boolean {
        return suggestions.isEmpty()
    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {
        observable.removeOnListChangedCallback(callback)
        observable.addOnListChangedCallback(callback)
    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {
        observable.removeOnListChangedCallback(callback)
    }

    override fun areAllItemsEnabled(): Boolean {
        return true
    }

    override fun isEnabled(position: Int): Boolean {
        if(suggestions.size <= position) {
            throw ArrayIndexOutOfBoundsException()
        }
        return true
    }

    override fun getFilter(): Filter {
        return object: Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()

                suggestions = ObservableArrayList()
                if(constraint == null) {
                    suggestions.addAll(observable)
                } else {
                    val filtered = observable.filter { it.toString().startsWith(constraint, true) }
                    suggestions.addAll(filtered)
                }
                results.values = suggestions
                results.count = suggestions.size

                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                results?.also {
                    suggestions = ObservableArrayList()
                    if(constraint == null) {
                        suggestions.addAll(observable)
                    } else {
                        val filtered = observable.filter { it.toString().startsWith(constraint, true) }
                        suggestions.addAll(filtered)
                    }
                    it.values = suggestions
                    it.count = suggestions.size
                }
            }
        }
    }
}