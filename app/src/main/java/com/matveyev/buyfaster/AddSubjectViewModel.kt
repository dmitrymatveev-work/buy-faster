package com.matveyev.buyfaster

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.ViewModel
import com.matveyev.buyfaster.data.DepRepository
import com.matveyev.buyfaster.vms.DepartmentVM
import com.matveyev.buyfaster.vms.SubjectVM
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async

class AddSubjectViewModel() : ViewModel() {
    lateinit var departmentsAutoComplete: ObservableArrayList<DepartmentVM>
    lateinit var depsRepo: DepRepository

    lateinit var subject: SubjectVM

    fun deleteDepartmentAsync(dep: DepartmentVM) = GlobalScope.async(Dispatchers.Main) {
        departmentsAutoComplete.remove(dep)
        depsRepo.storeDepsAsync(departmentsAutoComplete).await()
    }
}