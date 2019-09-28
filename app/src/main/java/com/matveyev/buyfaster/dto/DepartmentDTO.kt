package com.matveyev.buyfaster.dto

import kotlinx.serialization.Serializable

@Serializable
class DepartmentDTO {
    var title = ""
    var subjects: List<SubjectDTO> = emptyList()
}