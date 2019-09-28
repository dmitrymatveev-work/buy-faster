package com.matveyev.buyfaster.data

import android.content.Context
import androidx.room.Room
import com.matveyev.buyfaster.dto.DepartmentDTO
import com.matveyev.buyfaster.vms.DepartmentVM
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.list

class DepRepository(private val ctx: Context) {
    private val db = Room.databaseBuilder(
        ctx,
        BuyFasterDB::class.java,
        "family_budget.db"
    ).fallbackToDestructiveMigration().build()

    private val channel = Channel<List<DepartmentVM>>(50)

    init {
        GlobalScope.launch {
            while (true) {
                storeDepsInternal(channel.receive())
            }
        }
    }

    private fun storeDepsInternal(depVMs: List<DepartmentVM>) {
        db.depDAO().deleteAll()
        val json = Json.stringify(DepartmentDTO.serializer().list, mapToDTO(depVMs))
        val depData = DepDBO(0, json)
        db.depDAO().insert(depData)
    }

    fun getDepsAsync() = GlobalScope.async<List<DepartmentVM>> {
        val deps = db.depDAO().getAll()
        val depDBO = deps.firstOrNull()
        depDBO?.content?.run {
            val depDTOs = Json.parse(DepartmentDTO.serializer().list, this)
            mapToVM(depDTOs)
        } ?: emptyList()
    }

    fun storeDepsAsync(depVMs: List<DepartmentVM>) = GlobalScope.async {
        channel.send(depVMs)
    }

    private fun mapToDTO(depVMs: List<DepartmentVM>) : List<DepartmentDTO> {
        return depVMs.map {
            val dto = DepartmentDTO()
            it.title.get()?.apply {
                dto.title = this
            }
            dto
        }.toList()
    }

    private fun mapToVM(depDTOs: List<DepartmentDTO>) : List<DepartmentVM> {
        return depDTOs.map {
            val vm = DepartmentVM()
            vm.title.set(it.title)
            vm
        }.toList()
    }
}