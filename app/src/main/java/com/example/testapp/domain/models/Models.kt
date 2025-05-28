package com.example.testapp.domain.models

import android.net.Uri
import com.example.testapp.remote.models.ReagentDto
import com.example.testapp.remote.models.ReportDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.Instant

// Field.kt
data class Field(
    val id: Int,
    val name: String
)

// Well.kt
data class Well(
    val id: Int,
    val wellNumber: String,
    val fieldId: Int
)

// Layer.kt
data class Layer(
    val id: Int,
    val layerName: String,
    val wellId: Int
)

// Customer.kt
data class Customer(
    val id: Int,
    val companyName: String
)

// Position.kt
data class Position(
    val id: Int,
    val positionName: String
)

data class Laboratorian(
    val id: Int,
    val fullName: String
)

// Employee.kt
data class Employee(
    val id: Int,
    val fullName: String,
    val phoneNumber: String,
    val password: String,
    val position: String
)



data class BlenderReport(
    override val id: Int? = null,
    override val code: String,
    override val employeeId: Int,
    override val fieldId: Int,
    override val wellId: Int,
    override val layerId: Int,
    override val customerId: Int,
    override val status: String? = null,
    override val fileUrl: String? = null,
    override val createdAt: String? = null,
    override val reportName: String,
    val reagents: List<Reagent>,
    val supervisorSignatureUrl: String? = null,
    val engineerSignatureUrl: String? = null,
    override val supervisor_signature_url: String? = null,
    override val engineer_signature_url: String? = null
): BaseReport {
    override val reportType: ReportTypeEnum = ReportTypeEnum.BLENDER
}




data class Reagent(
    val id: Int,
    val name: String,
    val tests: List<BlenderReportTestDetail>
)

data class BlenderReportReagentLink(
    val reportId: Int? = null,
    val reagentId: Int
)

data class ReportType(
    val id: Int? = null,
    val name: String
)

data class BlenderReportTestDetail(
    val id: Int? = null,
    val reportId: Int,
    val reagentId: Int,
    val flowRate: Double,
    val concentration: Double,
    val testTime: Double,
    val actualAmount: Double
)

data class TestAttempt(
    val id: Int,
    val reagentId: Int,
    val flowRate: Double,
    val concentration: Double,
    val testTime: Double,
    val actualAmount: Double
)


data class Photo(
    val uri: Uri,
    val reagentName: String,
    val attemptNumber: Int
)


data class EmulsionPhoto(
    val uri: Uri,
    val emulsionName: String
)

data class ReportPhoto(
    val id: Int? = null,
    val reportId: Int,
    val photoTypeId: Int,
    val photoUrl: String,
    val createdAt: Instant? = null,
    val attemptNumber: Int? = null,
)

data class PhotoType(
    val id: Int,
    val name: String,
    val description: String?
)


data class ReportFilters(
    val reportType: ReportTypeEnum? = null,
    val status: ReportStatus? = null,
    var fields: List<Field> = emptyList(),
    var wells: List<Well> = emptyList(),
    var layers: List<Layer> = emptyList(),
    var customers: List<Customer> = emptyList(),
    val dateRange: ClosedRange<java.time.LocalDate>? = null
)



data class AcidReport(
    override val id: Int? = null,
    override val employeeId: Int,
    override val fieldId: Int,
    override val wellId: Int,
    override val layerId: Int,
    override val customerId: Int,
    val labTechnicianId: Int,
    override val status: String? = null,
    override val fileUrl: String? = null,
    override val createdAt: String? = null,
    override val reportName: String,
    override val code: String,
    val concentratedAcidPercentage: Double,
    val preparedAcidPercentage: Double,
    override val supervisor_signature_url: String? = null,
    override val engineer_signature_url: String? = null
): BaseReport {
    override val reportType: ReportTypeEnum = ReportTypeEnum.ACID
}

data class AcidReportSignatureLinkDto(
    val report_id: Int,
    val signature_id: Int
)


data class GelReport(
    override val id: Int? = null,
    override val employeeId: Int,
    override val fieldId: Int,
    override val wellId: Int,
    override val layerId: Int,
    override val customerId: Int,
    override val status: String? = null,
    override val fileUrl: String? = null,
    override val createdAt: String? = null,
    override val reportName: String,
    override val code: String,
    override val supervisor_signature_url: String? = null,
    override val engineer_signature_url: String? = null
): BaseReport {
    override val reportType: ReportTypeEnum = ReportTypeEnum.GEL
}

data class GelReportSignatureLink(
    val report_id: Int,
    val signature_id: Int
)


sealed interface BaseReport {
    val id: Int?
    val employeeId: Int
    val fieldId: Int?
    val wellId: Int?
    val layerId: Int?
    val customerId: Int?
    val status: String?
    val fileUrl: String?
    val createdAt: String?
    val reportName: String
    val reportType: ReportTypeEnum
    val code: String
    val supervisor_signature_url: String?
    val engineer_signature_url: String?
}

sealed class ReportData {
    abstract val employee: Employee
    abstract val customer: Customer
    abstract val field: Field
    abstract val well: Well
    abstract val layer: Layer
    abstract val report: BaseReport
    abstract val createdAt: String

    data class Blender(
        override val employee: Employee,
        override val customer: Customer,
        override val field: Field,
        override val well: Well,
        override val layer: Layer,
        override val report: BlenderReport,
        val testDetails: List<BlenderReportTestDetail>,
        val reagents: List<Reagent>,
        override val createdAt: String
    ) : ReportData()

    data class Acid(
        override val employee: Employee,
        override val customer: Customer,
        override val field: Field,
        override val well: Well,
        override val layer: Layer,
        override val report: AcidReport,
        val labTechnician: Employee?,
        override val createdAt: String
    ) : ReportData()

    data class Gel(
        override val employee: Employee,
        override val customer: Customer,
        override val field: Field,
        override val well: Well,
        override val layer: Layer,
        override val report: GelReport,
        override val createdAt: String
    ) : ReportData()
}
data class ReportStatus(
    val id: Int,
    val status_name: String
)

enum class ReportTypeEnum(val id: Int, val displayName: String) {

    BLENDER(1, "Блендер"),
    ACID(2, "Кислота"),
    GEL(3, "Гель");


}

data class ReportWithStatus(
    val report: BaseReport,
    val status: ReportStatus?
)


data class ReportsStatistics(
    val blenderCount: Int,
    val acidCount: Int,
    val gelCount: Int,
    val total: Int
)

data class HourlyReportData(
    val hour: Int,
    val count: Int
)

data class ChartData(
    val sessionsByHour: Map<Int, Int>,
    val yAxisLabels: List<Int>,
    val xAxisLabels: List<Int>
)