package com.example.testapp.remote.models

import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Laboratorian
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.Report
import com.example.testapp.domain.models.ReportReagentLink
import com.example.testapp.domain.models.ReportTestDetail
import com.example.testapp.domain.models.Well
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FieldDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String
) {
    fun toDomain(): Field {
        return Field(
            id = this.id,
            name = this.name
        )
    }
}

@Serializable
data class WellDto(
    @SerialName("id")
    val id: Int,

    @SerialName("well_number")
    val wellNumber: String,

    @SerialName("field_id")
    val fieldId: Int
) {
    fun toDomain(): Well {
        return Well(
            id = this.id,
            wellNumber = this.wellNumber,
            fieldId = this.fieldId
        )
    }
}

@Serializable
data class LayerDto(
    @SerialName("id")
    val id: Int,

    @SerialName("layer_name")
    val layerName: String,

    @SerialName("well_id")
    val wellId: Int
) {
    fun toDomain(): Layer {
        return Layer(
            id = this.id,
            layerName = this.layerName,
            wellId = this.wellId
        )
    }
}

@Serializable
data class CustomerDto(
    @SerialName("id")
    val id: Int,

    @SerialName("company_name")
    val companyName: String
) {
        fun toDomain(): Customer {
        return Customer(
            id = this.id,
            companyName = this.companyName
        )
    }
}


@Serializable
data class LaboratorianDto(
    @SerialName("id")
    val id: Int,

    @SerialName("full_name")
    val fullName: String
) {
    fun toDomain(): Laboratorian {
        return Laboratorian(
            id = this.id,
            fullName = this.fullName
        )
    }
}

@Serializable
data class EmployeeDto(
    @SerialName("id")
    val id: Int,

    @SerialName("full_name")
    val fullName: String,

    @SerialName("phone_number")
    val phoneNumber: String,

    @SerialName("password")
    val password: String,

    @SerialName("positions") // Данные из связанной таблицы positions
    val position: PositionDto
) {
    fun toDomain(): Employee {
        return Employee(
            id = this.id,
            fullName = this.fullName,
            phoneNumber = this.phoneNumber,
            password = this.password,
            position = this.position.positionName // Название должности
        )
    }
}

@Serializable
data class PositionDto(
    @SerialName("id")
    val id: Int,
    @SerialName("position_name")
    val positionName: String
)

@Serializable
data class ReportDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("employee_id") val employeeId: Int,
    @SerialName("field_id") val fieldId: Int,
    @SerialName("well_id") val wellId: Int,
    @SerialName("layer_id") val layerId: Int,
    @SerialName("customer_id") val customerId: Int,
    @SerialName("file_url") val fileUrl: String,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("report_name") val reportName: String
) {
    fun toDomain(reagents: List<Reagent> = emptyList()): Report {
        return Report(
            id = this.id,
            employeeId = this.employeeId,
            fieldId = this.fieldId,
            wellId = this.wellId,
            layerId = this.layerId,
            customerId = this.customerId,
            fileUrl = this.fileUrl,
            createdAt = this.createdAt,
            reportName = this.reportName,
            reagents = reagents
        )
    }
}

@Serializable
data class ReportReagentLinkDto(
    @SerialName("report_id") val reportId: Int? = null,
    @SerialName("reagent_id") val reagentId: Int
) {
    fun toDomain(): ReportReagentLink {
        return ReportReagentLink(
            reportId = this.reportId,
            reagentId = this.reagentId
        )
    }
}

@Serializable
data class ReportTestDetailDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("report_id") val reportId: Int,
    @SerialName("reagent_id") val reagentId: Int,
    @SerialName("flow_rate") val flowRate: Double,
    @SerialName("concentration") val concentration: Double,
    @SerialName("test_time") val testTime: Double,
    @SerialName("actual_amount") val actualAmount: Double
) {
    fun toDomain(): ReportTestDetail {
        return ReportTestDetail(
            id = this.id,
            reportId = this.reportId,
            reagentId = this.reagentId,
            flowRate = this.flowRate,
            concentration = this.concentration,
            testTime = this.testTime,
            actualAmount = this.actualAmount
        )
    }
}

@Serializable
data class ReagentDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String
) {
    fun toDomain(reagents: List<ReportTestDetail> = emptyList()): Reagent {
        return Reagent(
            id = this.id,
            name = this.name,
            tests = reagents
        )
    }
}