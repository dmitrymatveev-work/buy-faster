package com.matveyev.buyfaster

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import com.matveyev.buyfaster.data.DepRepository
import com.matveyev.buyfaster.data.TheListRepository
import com.matveyev.buyfaster.vms.DepartmentVM
import com.matveyev.buyfaster.vms.SubjectVM
import com.matveyev.buyfaster.vms.TheListVM
import kotlinx.coroutines.*

class TheListViewModel : ViewModel() {
    val theList = ObservableField<TheListVM>()
    val departmentsAutoComplete = ObservableArrayList<DepartmentVM>()

    lateinit var theListRepo: TheListRepository
    lateinit var depsRepo: DepRepository

    fun refresh()
    {
        runBlocking {
            val theListVM = theListRepo.getTheListAsync().await()
            theListVM.departments.addOnListChangedCallback(
                DepartmentsChangedCallback { runBlocking { theListRepo.storeTheListAsync(theListVM).await() } }
            )
            theListVM.departments.forEach {
                it.subjects.addOnListChangedCallback(SubjectsChangedCallback(
                    { runBlocking { theListRepo.storeTheListAsync(theListVM).await() } },
                    theListVM.departments,
                    it
                ))
            }
            theList.set(theListVM)

            val depVMs = depsRepo.getDepsAsync().await()
            departmentsAutoComplete.addAll(depVMs)
        }
    }

    fun addSubjectAsync(subj: SubjectVM) = GlobalScope.async(Dispatchers.Main) {
        val description = subj.description.get() ?: return@async
        theList.get()?.run {
            var dep = departments.find { d -> d.title.get().equals(subj.department.get(), ignoreCase = true) }
            if (dep == null) {
                dep = DepartmentVM().apply {
                    title.set(subj.department.get())
                }

                val theList = this
                dep.subjects.addOnListChangedCallback(SubjectsChangedCallback(
                    { runBlocking { theListRepo.storeTheListAsync(theList).await() } },
                    departments,
                    dep))
                departments.add(0, dep)
            }
            description.lines().asReversed().forEach {
                val newSubj = SubjectVM()
                newSubj.department.set(dep.title.get())
                newSubj.description.set(it)
                dep.subjects.add(0, newSubj)
            }

            if(departmentsAutoComplete.filter { it.title.get().equals(dep.title.get(), true) }.firstOrNull() == null) {
                departmentsAutoComplete.add(dep)
            }

            theListRepo.storeTheListAsync(this).await()
            depsRepo.storeDepsAsync(departmentsAutoComplete).await()
        }
    }

    fun deleteSubjectAsync(subj: SubjectVM) = GlobalScope.async(Dispatchers.Main) {
        theList.get()?.run {
            val dep = departments.find { d -> d.title.get().equals(subj.department.get(), ignoreCase = true) }
            dep?.subjects?.remove(subj)
        }
    }

    fun getMessageContent(): String {
        val list = theList.get()
        if(list == null){
            return ""
        }else{
            return list.departments.joinToString("\n") {
                it.title.get() + "\n" + it.subjects.joinToString("\n") {
                    "-" + it.description.get()
                }
            }
        }
    }

    private class SubjectsChangedCallback(
        val store: () -> Unit,
        val departmentVMS: ObservableList<DepartmentVM>,
        val departmentVM: DepartmentVM) :
        ObservableList.OnListChangedCallback<ObservableList<SubjectVM>>() {
        override fun onItemRangeRemoved(sender: ObservableList<SubjectVM>?, positionStart: Int, itemCount: Int) {
            if (sender?.size == 0)
            {
                departmentVMS.remove(departmentVM)
                departmentVM.subjects.removeOnListChangedCallback(this)
            }
            store()
        }

        override fun onChanged(sender: ObservableList<SubjectVM>?) { }
        override fun onItemRangeChanged(sender: ObservableList<SubjectVM>?, positionStart: Int, itemCount: Int) {
            store()
        }
        override fun onItemRangeInserted(sender: ObservableList<SubjectVM>?, positionStart: Int, itemCount: Int) { }
        override fun onItemRangeMoved(
            sender: ObservableList<SubjectVM>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) { }
    }

    private class DepartmentsChangedCallback(val store: () -> Unit) :
        ObservableList.OnListChangedCallback<ObservableList<SubjectVM>>() {
        override fun onItemRangeRemoved(sender: ObservableList<SubjectVM>?, positionStart: Int, itemCount: Int) { }
        override fun onChanged(sender: ObservableList<SubjectVM>?) { }
        override fun onItemRangeChanged(sender: ObservableList<SubjectVM>?, positionStart: Int, itemCount: Int) {
            store()
        }
        override fun onItemRangeInserted(sender: ObservableList<SubjectVM>?, positionStart: Int, itemCount: Int) { }
        override fun onItemRangeMoved(
            sender: ObservableList<SubjectVM>?,
            fromPosition: Int,
            toPosition: Int,
            itemCount: Int
        ) { }
    }
}