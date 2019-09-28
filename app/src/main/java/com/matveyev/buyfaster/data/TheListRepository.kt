package com.matveyev.buyfaster.data

import android.content.Context
import androidx.room.Room
import com.matveyev.buyfaster.dto.DepartmentDTO
import com.matveyev.buyfaster.dto.SubjectDTO
import com.matveyev.buyfaster.dto.TheListDTO
import com.matveyev.buyfaster.vms.DepartmentVM
import com.matveyev.buyfaster.vms.SubjectVM
import com.matveyev.buyfaster.vms.TheListVM
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.channels.*

class TheListRepository(private val ctx: Context) {
    private val db = Room.databaseBuilder(
        ctx,
        BuyFasterDB::class.java,
        "family_budget.db"
    ).fallbackToDestructiveMigration().build()

    private val channel = Channel<TheListVM>(50)

    init {
        GlobalScope.launch {
            while (true) {
                storeTheListInternal(channel.receive())
            }
        }
    }

    private fun storeTheListInternal(theListVM: TheListVM) {
        db.theListDAO().deleteAll()
        val json = Json.stringify(TheListDTO.serializer(), mapToDTO(theListVM))
        val theListData = TheListDBO(0, json)
        db.theListDAO().insert(theListData)
    }

    fun getTheListAsync() = GlobalScope.async<TheListVM> {
        val lists = db.theListDAO().getAll()
        val theListDBO = lists.firstOrNull()
        theListDBO?.content?.run {
            val theListDTO = Json.parse(TheListDTO.serializer(), this)
            mapToVM(theListDTO)
        } ?: TheListVM()
    }

    fun storeTheListAsync(theListVM: TheListVM) = GlobalScope.async  {
        channel.send(theListVM)
    }

    private fun mapToDTO(theListVM: TheListVM) : TheListDTO {
        val theListDTO = TheListDTO()
        theListDTO.departments = theListVM.departments.map {
            val departmentDTO = DepartmentDTO()
            it.title.get()?.apply {
                departmentDTO.title = this
            }
            departmentDTO.subjects = it.subjects.map {
                val subjectDTO = SubjectDTO()
                it.department.get()?.apply {
                    subjectDTO.department = this
                }
                it.description.get()?.apply {
                    subjectDTO.description = this
                }
                subjectDTO
            }.toList()
            departmentDTO
        }.toList()
        return theListDTO
    }

    private fun mapToVM(theListDTO: TheListDTO) : TheListVM {
        val theListVM = TheListVM()
        theListDTO.departments.forEach {
            val departmentVM = DepartmentVM()
            departmentVM.title.set(it.title)

            it.subjects.forEach {
                val subjectVM = SubjectVM()
                subjectVM.department.set(it.department)
                subjectVM.description.set(it.description)
                departmentVM.subjects.add(subjectVM)
            }

            theListVM.departments.add(departmentVM)
        }
        return theListVM
    }
}