package com.example.testapp.remote.models

import com.example.testapp.domain.models.AcidReport
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Laboratorian
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.PhotoType
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.ReportPhoto
import com.example.testapp.domain.models.BlenderReportReagentLink
import com.example.testapp.domain.models.BlenderReportTestDetail
import com.example.testapp.domain.models.GelReport
import com.example.testapp.domain.models.ReportType
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
    val id: Int? = null,
    @SerialName("position_name")
    val positionName: String
)

@Serializable
data class BlenderReportDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("employee_id") val employee_id: Int,
    @SerialName("field_id") val field_id: Int,
    @SerialName("well_id") val well_id: Int,
    @SerialName("layer_id") val layer_id: Int,
    @SerialName("customer_id") val customer_id: Int,
    @SerialName("file_url") val file_url: String? = null,
    @SerialName("created_at") val created_at: String? = null,
    @SerialName("report_name") val report_name: String,
    @SerialName("code") val code: String
) {
    fun toDomain(
        reagents: List<Reagent> = emptyList(),
        supervisorSignatureUrl: String? = null,
        engineerSignatureUrl: String? = null
    ): BlenderReport {
        return BlenderReport(
            id = id,
            code = code,
            employeeId = employee_id,
            fieldId = field_id,
            wellId = well_id,
            layerId = layer_id,
            customerId = customer_id,
            fileUrl = file_url,
            createdAt = created_at,
            reportName = report_name,
            reagents = reagents,
            supervisorSignatureUrl = supervisorSignatureUrl,
            engineerSignatureUrl = engineerSignatureUrl
        )
    }
}

@Serializable
data class BlenderReportReagentLinkDto(
    @SerialName("report_id") val reportId: Int? = null,
    @SerialName("reagent_id") val reagentId: Int
) {
    fun toDomain(): BlenderReportReagentLink {
        return BlenderReportReagentLink(
            reportId = this.reportId,
            reagentId = this.reagentId
        )
    }
}

@Serializable
data class BlenderReportTestDetailDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("report_id") val reportId: Int,
    @SerialName("reagent_id") val reagentId: Int,
    @SerialName("flow_rate") val flowRate: Double,
    @SerialName("concentration") val concentration: Double,
    @SerialName("test_time") val testTime: Double,
    @SerialName("actual_amount") val actualAmount: Double
) {
    fun toDomain(): BlenderReportTestDetail {
        return BlenderReportTestDetail(
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
    fun toDomain(reagents: List<BlenderReportTestDetail> = emptyList()): Reagent {
        return Reagent(
            id = this.id,
            name = this.name,
            tests = reagents
        )
    }
}

@Serializable
data class ReportTypeDto(
    @SerialName("id")
    val id: Int,

    @SerialName("type_name")
    val name: String
) {
    fun toDomain(): ReportType {
        return ReportType(
            id = id,
            name = name
        )
    }
}


@Serializable
data class ReportPhotoDto(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("report_id")
    val reportId: Int,

    @SerialName("photo_type_id")
    val photoTypeId: Int,

    @SerialName("photo_url")
    val photoUrl: String,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("attempt_number")
    val attemptNumber: Int? = null,
) {
    fun toDomain(): ReportPhoto {
        return ReportPhoto(
            id = this.id,
            reportId = this.reportId,
            photoTypeId = this.photoTypeId,
            photoUrl = this.photoUrl,
            attemptNumber = this.attemptNumber,
        )
    }
}

@Serializable
data class PhotoTypeDto(
    @SerialName("id")
    val id: Int,

    @SerialName("name")
    val name: String,

    @SerialName("description")
    val description: String?,

    @SerialName("photos") // Обратная связь с фотографиями
    val photos: List<ReportPhotoDto> = emptyList()
) {
    fun toDomain(): PhotoType {
        return PhotoType(
            id = this.id,
            name = this.name,
            description = this.description
        )
    }
}


@Serializable
data class BlenderReportSignatureLinkDto(
    @SerialName("report_id") val report_id: Int,
    @SerialName("signature_id") val signature_id: Int
)

@Serializable
data class ReportDto(
    @SerialName("id") val id: Int? = null,
    @SerialName("status_id") val status_id: Int? = null,
    @SerialName("type_id") val type_id: Int? = null,
    @SerialName("supervisor_signature_url") val supervisor_signature_url: String? = null,
    @SerialName("engineer_signature_url") val engineer_signature_url: String? = null
)

@Serializable
data class AcidReportDto(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("employee_id")
    val employeeId: Int,

    @SerialName("lab_technician_id")
    val labTechnicianId: Int,

    @SerialName("field_id")
    val fieldId: Int,

    @SerialName("well_id")
    val wellId: Int,

    @SerialName("layer_id")
    val layerId: Int,

    @SerialName("customer_id")
    val customerId: Int,

    @SerialName("file_url")
    val fileUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("report_name")
    val reportName: String,

    @SerialName("code")
    val code: String,

    @SerialName("concentrated_acid_percentage")
    val concentratedAcidPercentage: Double,

    @SerialName("prepared_acid_percentage")
    val preparedAcidPercentage: Double
) {
    fun toDomain(): AcidReport {
        return AcidReport(
            id = this.id,
            employeeId = this.employeeId,
            labTechnicianId = this.labTechnicianId,
            fieldId = this.fieldId,
            wellId = this.wellId,
            layerId = this.layerId,
            customerId = this.customerId,
            fileUrl = this.fileUrl,
            createdAt = this.createdAt,
            reportName = this.reportName,
            code = this.code,
            concentratedAcidPercentage = this.concentratedAcidPercentage,
            preparedAcidPercentage = this.preparedAcidPercentage
        )
    }
}

@Serializable
data class AcidReportSignatureLinkDto(
    @SerialName("report_id")
    val reportId: Int,

    @SerialName("signature_id")
    val signatureId: Int
)


@Serializable
data class GelReportDto(
    @SerialName("id")
    val id: Int? = null,

    @SerialName("employee_id")
    val employeeId: Int,

    @SerialName("field_id")
    val fieldId: Int,

    @SerialName("well_id")
    val wellId: Int,

    @SerialName("layer_id")
    val layerId: Int,

    @SerialName("customer_id")
    val customerId: Int,

    @SerialName("file_url")
    val fileUrl: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("report_name")
    val reportName: String,

    @SerialName("code")
    val code: String
) {
    fun toDomain(): GelReport {
        return GelReport(
            id = this.id,
            employeeId = this.employeeId,
            fieldId = this.fieldId,
            wellId = this.wellId,
            layerId = this.layerId,
            customerId = this.customerId,
            fileUrl = this.fileUrl,
            createdAt = this.createdAt,
            reportName = this.reportName,
            code = this.code
        )
    }
}


@Serializable
data class GelReportSignatureLinkDto(
    @SerialName("report_id")
    val reportId: Int,

    @SerialName("signature_id")
    val signatureId: Int
)


@Serializable
data class ReportSignatureDto(
    val report_id: Int,
    val signature_id: Int
)

@Serializable
data class ReportStatusDto(
    val id: Int,
    val status_id: Int
)
