package com.matveyev.buyfaster.dto

import kotlinx.serialization.Serializable

@Serializable
class TheListDTO {
    var departments: List<DepartmentDTO> = emptyList()
}