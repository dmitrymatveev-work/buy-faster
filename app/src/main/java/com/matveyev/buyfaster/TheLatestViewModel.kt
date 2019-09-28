package com.matveyev.buyfaster

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel

class TheLatestViewModel : ViewModel() {
    var theLatests = ObservableArrayList<String>()

    private var count = 0

    fun test(){
        theLatests.add("Test " + count++)
    }
}