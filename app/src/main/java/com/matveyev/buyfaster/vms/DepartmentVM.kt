package com.matveyev.buyfaster.vms

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField

class DepartmentVM : BaseVM() {
    val title = ObservableField<String>()
    val subjects = ObservableArrayList<SubjectVM>()
    override fun toString(): String {
        return title.get() ?: ""
    }
}