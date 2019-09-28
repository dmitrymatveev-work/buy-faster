package com.matveyev.buyfaster.uicomponents

import android.content.Context
import android.util.AttributeSet
import android.widget.AutoCompleteTextView
import com.matveyev.buyfaster.R

class CustomAutoCompleteTextView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    AutoCompleteTextView(context, attrs, defStyleAttr) {

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, R.attr.autoCompleteTextViewStyle)

    private var filterText: CharSequence? = null
    private var keyCode: Int = 0

    fun refreshAutoCompleteResults() {
        super.performFiltering(filterText, keyCode)
    }

    override fun performFiltering(text: CharSequence?, keyCode: Int) {
        filterText = text
        this.keyCode = keyCode
        super.performFiltering(text, keyCode)
    }
}