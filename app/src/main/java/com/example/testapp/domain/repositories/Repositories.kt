package com.example.testapp.domain.repositories

import android.net.Uri
import com.example.testapp.domain.models.AcidReport
import com.example.testapp.domain.models.BaseReport
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Laboratorian
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.GelReport
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.models.ReportPhoto
import com.example.testapp.domain.models.ReportType
import com.example.testapp.domain.models.ReportTypeEnum
import com.example.testapp.domain.models.Well
import com.example.testapp.utils.PhotoType
import java.io.File

interface FieldRepository {
    suspend fun getFields(): List<Field>
    suspend fun getFieldById(id: Int): Field?
}

interface ReportTypeRepository {
    suspend fun getReportTypes(): List<ReportType>
}

interface WellRepository {
    suspend fun getWells(): List<Well>
    suspend fun getWellById(id: Int): Well?
}

interface LayerRepository {
    suspend fun getLayers(): List<Layer>
    suspend fun getLayerById(id: Int): Layer?
}

interface CustomerRepository {
    suspend fun getCustomers(): List<Customer>
    suspend fun getCustomerById(id: Int): Customer?
}

interface EmployeeRepository {
    suspend fun login(phoneNumber: String, password: String): Employee?
    suspend fun getEmployeeIdByPhone(phoneNumber: String): Int?
    suspend fun getLaboratorians(): List<Laboratorian>
}

interface ReagentRepository {
    suspend fun getLayers(): List<Layer>
}


interface ReportBlenderRepository {
    suspend fun insertReportBlender(
        report: BlenderReport,
        blenderReportCode: String
    ): Int?

    suspend fun updateReportReagents(
        reportId: Int,
        reagents: List<Reagent>
    ): Boolean

    suspend fun getReagentIdByName(
        reagentName: String
    ): Int?
    suspend fun getSupervisorReports(): List<BaseReport>
    suspend fun getBlenderReportsSupervisor(): List<BlenderReport>
    suspend fun getReportPhotos(reportId: Int, reportType: ReportTypeEnum): List<ReportPhoto>
    suspend fun uploadSupervisorSignature(
        reportId: Int,
        reportType: ReportTypeEnum,
        signatureFile: File
    ): String?
}

interface ReportRepository {
    suspend fun getReports(filters: ReportFilters): List<BaseReport>?

}

interface PhotoRepository{
    suspend fun uploadPhotoBlenderReport(
        reportId: Int,
        photoTypeName: String,
        photoFile: File,
        attemptNumber: Int,
        report_photo_table_name: String
    ): String?
}




interface AcidReportRepository {
    suspend fun saveReportAndGetId(
        report: AcidReport,
        acidReportCode: String,
        photos: Map<PhotoType, Uri>,
    ): Int?

    suspend fun getAcidReportsForEmployee(employeeId: Int): List<AcidReport>

    suspend fun getReportPhotos(reportId: Int): List<ReportPhoto>
}


interface GelReportRepository {
    suspend fun saveReportAndGetId(
        report: GelReport,
        gelReportCode: String,
        photoSamplingUri: Uri
    ): Int?

    suspend fun getGelReportsForEmployee(employeeId: Int): List<GelReport>

    suspend fun getReportPhotos(reportId: Int): List<ReportPhoto>
}