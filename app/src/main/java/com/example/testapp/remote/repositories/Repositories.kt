package com.example.testapp.remote.repositories

import android.content.Context
import android.net.Uri
import android.util.Log
import com.example.testapp.domain.models.AcidReport
import com.example.testapp.domain.models.BaseReport

import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Laboratorian
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.BlenderReport
import com.example.testapp.domain.models.BlenderReportTestDetail
import com.example.testapp.domain.models.GelReport
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.models.ReportPhoto
import com.example.testapp.domain.models.ReportType
import com.example.testapp.domain.models.ReportTypeEnum
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.repositories.AcidReportRepository
import com.example.testapp.domain.repositories.CustomerRepository
import com.example.testapp.domain.repositories.EmployeeRepository
import com.example.testapp.domain.repositories.FieldRepository
import com.example.testapp.domain.repositories.GelReportRepository
import com.example.testapp.domain.repositories.LayerRepository
import com.example.testapp.domain.repositories.PhotoRepository
import com.example.testapp.domain.repositories.ReportBlenderRepository
import com.example.testapp.domain.repositories.ReportRepository
import com.example.testapp.domain.repositories.ReportTypeRepository
import com.example.testapp.domain.repositories.WellRepository
import com.example.testapp.remote.models.AcidReportDto
import com.example.testapp.remote.models.AcidReportSignatureLinkDto
import com.example.testapp.remote.models.CustomerDto
import com.example.testapp.remote.models.EmployeeDto
import com.example.testapp.remote.models.FieldDto
import com.example.testapp.remote.models.LaboratorianDto
import com.example.testapp.remote.models.LayerDto
import com.example.testapp.remote.models.PhotoTypeDto
import com.example.testapp.remote.models.PositionDto
import com.example.testapp.remote.models.ReagentDto
import com.example.testapp.remote.models.BlenderReportDto
import com.example.testapp.remote.models.ReportPhotoDto
import com.example.testapp.remote.models.BlenderReportReagentLinkDto
import com.example.testapp.remote.models.BlenderReportSignatureLinkDto
import com.example.testapp.remote.models.BlenderReportTestDetailDto
import com.example.testapp.remote.models.GelReportDto
import com.example.testapp.remote.models.GelReportSignatureLinkDto
import com.example.testapp.remote.models.ReportDto
import com.example.testapp.remote.models.ReportSignatureDto
import com.example.testapp.remote.models.ReportStatusDto
import com.example.testapp.remote.models.ReportTypeDto
import com.example.testapp.remote.models.WellDto
import com.example.testapp.ui.viewmodels.toFile
import com.example.testapp.utils.PhotoType
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.filter.PostgrestFilterBuilder
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FieldRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : FieldRepository {
    override suspend fun getFields(): List<Field> {
        return withContext(Dispatchers.IO) {
            val fieldsDto = postgrest.from("fields")
                .select()
                .decodeList<FieldDto>()
            Log.e("FieldRepositoryImpl", fieldsDto.toString())
            fieldsDto.map { it.toDomain() }
        }
    }

    override suspend fun getFieldById(id: Int): Field? {
        return withContext(Dispatchers.IO) {
            val fieldDto = postgrest.from("fields")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<FieldDto>()
            fieldDto?.toDomain()
        }
    }
}

class WellRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : WellRepository {
    override suspend fun getWells(): List<Well> {
        return withContext(Dispatchers.IO) {
            val wellsDto = postgrest.from("wells")
                .select()
                .decodeList<WellDto>()
            Log.e("WellRepositoryImpl", wellsDto.toString())
            wellsDto.map { it.toDomain() }
        }
    }

    override suspend fun getWellById(id: Int): Well? {
        return withContext(Dispatchers.IO) {
            val wellDto = postgrest.from("wells")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<WellDto>()
            wellDto?.toDomain()
        }
    }
}

class LayerRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : LayerRepository {
    override suspend fun getLayers(): List<Layer> {
        return withContext(Dispatchers.IO) {
            val layersDto = postgrest.from("layers")
                .select()
                .decodeList<LayerDto>()
            layersDto.map { it.toDomain() }
        }
    }

    override suspend fun getLayerById(id: Int): Layer? {
        return withContext(Dispatchers.IO) {
            val layerDto = postgrest.from("layers")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<LayerDto>()
            layerDto?.toDomain()
        }
    }

}

class CustomerRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : CustomerRepository {
    override suspend fun getCustomers(): List<Customer> {
        return withContext(Dispatchers.IO) {
            val customersDto = postgrest.from("customers")
                .select()
                .decodeList<CustomerDto>()
            customersDto.map { it.toDomain() }
        }
    }

    override suspend fun getCustomerById(id: Int): Customer? {
        return withContext(Dispatchers.IO) {
            val customerDto = postgrest.from("customers")
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<CustomerDto>()
            customerDto?.toDomain()
        }
    }
}

class ReportTypeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : ReportTypeRepository {
    override suspend fun getReportTypes(): List<ReportType> {
        return withContext(Dispatchers.IO) {
            val reportTypeDto = postgrest.from("report_types")
                .select()
                .decodeList<ReportTypeDto>()
            reportTypeDto.map { it.toDomain() }
        }
    }

}




class EmployeeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : EmployeeRepository {

    override suspend fun login(phoneNumber: String, password: String): Employee? {
        return withContext(Dispatchers.IO) {

            val query = postgrest.from("employees")
                .select(
                    Columns.list("id", "full_name", "phone_number", "password", "positions(position_name)")
                ) {
                    filter {
                        eq("phone_number", phoneNumber)
                        eq("password", password)
                    }
                }


            val employeeDtoList = query.decodeList<EmployeeDto>()


            employeeDtoList.firstOrNull()?.toDomain()
        }
    }



    override suspend fun getEmployeeIdByPhone(phoneNumber: String): Int? {
        return withContext(Dispatchers.IO) {
            val employee = postgrest.from("employees")
                .select {
                    filter {
                        eq("phone_number", phoneNumber)
                    }
                }.decodeSingle<EmployeeDto>()
            val employeeId = employee.id

            employeeId
        }
    }

    override suspend fun getLaboratorians(): List<Laboratorian> {

        return withContext(Dispatchers.IO) {
            val laboratorianPosition = postgrest.from("positions")
                .select {
                    filter {
                        eq("position_name", "Лаборант ООО \"ЛРС\"")
                    }
                }.decodeSingle<PositionDto>()

            val laboratorians = postgrest.from("employees")
                .select {
                    filter {
                        laboratorianPosition.id?.let { eq("position_id", it) }
                    }
                }.decodeList<LaboratorianDto>()

            laboratorians.map { it.toDomain() }
        }

    }
}

class ReportBlenderRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : ReportBlenderRepository {

    override suspend fun insertReportBlender(
        report: BlenderReport,
        blenderReportCode: String
    ): Int? {
        return withContext(Dispatchers.IO) {

            try {
                val reportDto = BlenderReportDto(
                    employee_id = report.employeeId,
                    field_id = report.fieldId,
                    well_id = report.wellId,
                    layer_id = report.layerId,
                    customer_id = report.customerId,
                    report_name = report.reportName,
                    code = report.code
                )

                postgrest.from("blender_reports").insert(reportDto)


                val insertedReport = postgrest.from("blender_reports")
                    .select {
                        filter {
                            eq("code", blenderReportCode)
                        }
                    }.decodeSingle<BlenderReportDto>()
                val reportId = insertedReport.id

                reportId
            } catch (e: Exception) {
                Log.e("ReportBlenderRepo", "Error inserting report", e)
                null
            }
        }
    }

    override suspend fun updateReportReagents(reportId: Int, reagents: List<Reagent>): Boolean {
        return withContext(Dispatchers.IO) {
            Log.e("gfhgfghf", reagents.toString())
            reagents.forEach { reagent ->
                val reagentLinkDto = BlenderReportReagentLinkDto(
                    reportId = reportId,
                    reagentId = reagent.id
                )
                postgrest.from("blender_report_reagent_links")
                    .insert(reagentLinkDto)

                reagent.tests.forEach { test ->
                    val testDetailDto = BlenderReportTestDetailDto(
                        reportId = reportId,
                        reagentId = reagent.id,
                        flowRate = test.flowRate,
                        concentration = test.concentration,
                        testTime = test.testTime,
                        actualAmount = test.actualAmount
                    )
                    postgrest.from("blender_report_test_details")
                        .insert(testDetailDto)
                }
            }
            true
        }
    }

    override suspend fun getReagentIdByName(reagentName: String): Int? {
        return withContext(Dispatchers.IO) {
            val reagent = postgrest.from("reagents")
                .select {
                    filter {
                        eq("name", reagentName)
                    }
                }.decodeSingleOrNull<ReagentDto>()
            reagent?.id
        }
    }

    override suspend fun getSupervisorReports(): List<BaseReport> {
        return withContext(Dispatchers.IO) {

            val reportsWithStatus2 = mutableListOf<BaseReport>()


            ReportTypeEnum.entries.forEach { type ->
                val tableName = when (type) {
                    ReportTypeEnum.BLENDER -> "blender_report_signatures"
                    ReportTypeEnum.ACID -> "acid_report_signatures"
                    ReportTypeEnum.GEL -> "gel_report_signatures"
                }

                // Находим signature_id с status_id = 2
                val signatureIds = postgrest.from("reports")
                    .select(Columns.raw("id")) {
                        filter { eq("status_id", 2) }
                    }
                    .decodeList<Map<String, Int>>()
                    .map { it["id"]!! }

                if (signatureIds.isNotEmpty()) {
                    // Получаем report_id для этих signature_id
                    val reportIds = postgrest.from(tableName)
                        .select(Columns.raw("report_id")) {
                            filter { isIn("signature_id", signatureIds) }
                        }
                        .decodeList<Map<String, Int>>()
                        .map { it["report_id"]!! }

                    // Получаем сами отчеты
                    val reports = when (type) {
                        ReportTypeEnum.BLENDER -> {
                            postgrest.from("blender_reports")
                                .select(Columns.ALL) {
                                    filter { isIn("id", reportIds) }
                                }
                                .decodeList<BlenderReportDto>()
                                .map { it.toDomain() }
                        }
                        ReportTypeEnum.ACID -> {
                            postgrest.from("acid_reports")
                                .select(Columns.ALL) {
                                    filter { isIn("id", reportIds) }
                                }
                                .decodeList<AcidReportDto>()
                                .map { it.toDomain() }
                        }
                        ReportTypeEnum.GEL -> {
                            postgrest.from("gel_reports")
                                .select(Columns.ALL) {
                                    filter { isIn("id", reportIds) }
                                }
                                .decodeList<GelReportDto>()
                                .map { it.toDomain() }
                        }
                    }

                    reportsWithStatus2.addAll(reports)
                }
            }

            reportsWithStatus2.sortedByDescending { it.createdAt }
        }
    }
    override suspend fun getBlenderReportsSupervisor(): List<BlenderReport> {
        return withContext(Dispatchers.IO) {

            val blenderReports = postgrest.from("blender_reports")
                .select()
                .decodeList<BlenderReportDto>()


            val signatureLinks = postgrest.from("blender_report_signatures")
                .select()
                .decodeList<BlenderReportSignatureLinkDto>()


            val allReportSignatures = if (signatureLinks.isNotEmpty()) {
                val signatureIds = signatureLinks.map { it.signature_id }
                postgrest.from("reports")
                    .select {
                        filter { signatureIds.map { id -> eq("id", id) } }
                    }.decodeList<ReportDto>()
            } else {
                emptyList()
            }


            blenderReports.mapNotNull { blenderReport ->

                val signatureLink = signatureLinks.find { it.report_id == blenderReport.id }


                val reportSignature = signatureLink?.let { link ->
                    allReportSignatures.find { it.id == link.signature_id }
                }


                val reagentsWithTests = getReagentsWithTests(blenderReport.id!!)


                val domainReport = blenderReport.toDomain(
                    reagents = reagentsWithTests,
                    supervisorSignatureUrl = reportSignature?.supervisor_signature_url,
                    engineerSignatureUrl = reportSignature?.engineer_signature_url,
                )


                if (domainReport.supervisorSignatureUrl == null) {
                    domainReport
                } else {
                    null
                }
            }
        }
    }
    override suspend fun getReportPhotos(reportId: Int, reportType: ReportTypeEnum): List<ReportPhoto> {
        return withContext(Dispatchers.IO) {
            try {

                val photosTable = when (reportType) {
                    ReportTypeEnum.BLENDER -> "report_photos"
                    ReportTypeEnum.ACID -> "acid_report_photos"
                    ReportTypeEnum.GEL -> "gel_report_photos"
                }

                postgrest.from(photosTable)
                    .select {
                        filter {
                            eq("report_id", reportId)
                        }
                    }
                    .decodeList<ReportPhotoDto>()
                    .map { it.toDomain() }
            } catch (e: Exception) {
                Log.e("ReportRepository", "Error fetching photos for report $reportId", e)
                emptyList()
            }
        }
    }

    override suspend fun uploadSupervisorSignature(
        reportId: Int,
        reportType: ReportTypeEnum,
        signatureFile: File
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                // Определяем таблицу связей в зависимости от типа отчета
                val signatureTable = when (reportType) {
                    ReportTypeEnum.BLENDER -> "blender_report_signatures"
                    ReportTypeEnum.ACID -> "acid_report_signatures"
                    ReportTypeEnum.GEL -> "gel_report_signatures"
                }

                // Получаем ID подписи
                val signatureLink = postgrest.from(signatureTable)
                    .select {
                        filter { eq("report_id", reportId) }
                    }.decodeSingle<Map<String, Int>>()

                val signatureId = signatureLink["signature_id"] ?: return@withContext null

                // Загружаем файл подписи
                val path = "supervisor_${signatureId}_${reportType.name.lowercase()}.png"
                storage.from("signatures").upload(path, signatureFile.readBytes(), upsert = false)
                val url = storage.from("signatures").publicUrl(path)

                // Обновляем запись в таблице reports
                postgrest.from("reports").update({
                    set("supervisor_signature_url", url)
                }) {
                    filter { eq("id", signatureId) }
                }

                url
            } catch (e: Exception) {
                Log.e("ReportRepository", "Error uploading supervisor signature", e)
                null
            }
        }
    }


    private suspend fun getReagentsWithTests(reportId: Int): List<Reagent> {
        return withContext(Dispatchers.IO) {
            // 1. Получаем связи отчет-реагент
            val reagentLinks = postgrest.from("blender_report_reagent_links")
                .select {
                    filter { eq("report_id", reportId) }
                }.decodeList<BlenderReportReagentLinkDto>()

            Log.e("reagentLinks", reagentLinks.toString())

            // 2. Получаем все реагенты (одним запросом)
            val reagentIds = reagentLinks.map { it.reagentId }
            val reagents = reagentIds.mapNotNull { id ->
                postgrest.from("reagents")
                    .select {
                        filter { eq("id", id) }
                    }.decodeSingleOrNull<ReagentDto>()
            }

            Log.e("reagents", reagents.toString())

            // 3. Получаем все тесты для отчета
            val tests = postgrest.from("blender_report_test_details")
                .select {
                    filter { eq("report_id", reportId) }
                }.decodeList<BlenderReportTestDetailDto>()

            // 4. Собираем результат
            reagents.map { reagent ->
                Reagent(
                    id = reagent.id,
                    name = reagent.name,
                    tests = tests.filter { it.reagentId == reagent.id }
                        .map { test ->
                            BlenderReportTestDetail(
                                id = test.id,
                                reportId = test.reportId,
                                reagentId = test.reagentId,
                                flowRate = test.flowRate,
                                concentration = test.concentration,
                                testTime = test.testTime,
                                actualAmount = test.actualAmount
                            )
                        }
                )
            }
        }
    }

}
class ReportRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : ReportRepository {

    override suspend fun getReports(filters: ReportFilters): List<BaseReport> {
        return withContext(Dispatchers.IO) {
            // 1. Получаем базовые отчеты по фильтрам
            val reports = when (filters.reportType) {
                null -> getBlenderReports(filters) + getAcidReports(filters) + getGelReports(filters)
                ReportTypeEnum.BLENDER -> getBlenderReports(filters)
                ReportTypeEnum.ACID -> getAcidReports(filters)
                ReportTypeEnum.GEL -> getGelReports(filters)
            }


            // 3. Группируем отчеты по типам для пакетной обработки
            val reportsByType = reports.groupBy { it.reportType }

            // 4. Собираем все ID отчетов для каждого типа
            val reportIdsByType = reportsByType.mapValues { (_, reports) ->
                reports.mapNotNull { it.id }
            }

            // 5. Получаем все signature_id для отчетов каждого типа
            val signatureIds = reportIdsByType.mapValues { (type, ids) ->
                if (ids.isEmpty()) return@mapValues emptyMap<Int, Int>()

                val tableName = when (type) {
                    ReportTypeEnum.BLENDER -> "blender_report_signatures"
                    ReportTypeEnum.ACID -> "acid_report_signatures"
                    ReportTypeEnum.GEL -> "gel_report_signatures"
                }

                postgrest.from(tableName)
                    .select(Columns.list("report_id, signature_id")) {
                        filter { isIn("report_id", ids) }
                    }
                    .decodeList<ReportSignatureDto>()
                    .associate { it.report_id to it.signature_id }
            }

            // 6. Получаем статусы для всех signature_id
            val allSignatureIds = signatureIds.values.flatMap { it.values }
            val statuses = if (allSignatureIds.isNotEmpty()) {
                postgrest.from("reports")
                    .select(Columns.list("id, status_id")) {
                        filter { isIn("id", allSignatureIds) }
                    }
                    .decodeList<ReportStatusDto>()
                    .associate { it.id to it.status_id }
            } else {
                emptyMap()
            }

            // 7. Фильтруем отчеты по статусу 3
            reports.filter { report ->
                val signatureId = signatureIds[report.reportType]?.get(report.id ?: -1)
                signatureId?.let { statuses[it] == 3 } ?: false
            }.sortedByDescending { it.createdAt }
        }
    }

    private suspend fun getBlenderReports(filters: ReportFilters): List<BlenderReport> {
        return postgrest.from("blender_reports")
            .select(Columns.ALL) {
                if (filters.fields.isNotEmpty()) {
                    filter {
                        isIn("field_id", filters.fields.map { it.id!! })
                    }
                }

                if (filters.wells.isNotEmpty()) {
                    filter {
                        isIn("well_id", filters.wells.map { it.id!! })
                    }
                }

                if (filters.layers.isNotEmpty()) {
                    filter {
                        isIn("layer_id", filters.layers.map { it.id!! })
                    }
                }

                if (filters.customers.isNotEmpty()) {
                    filter {
                        isIn("customer_id", filters.customers.map { it.id!! })
                    }
                }

                if (filters.dateRange != null) {
                    filter {
                        and {
                            gte("created_at", filters.dateRange.start.toString())
                            lte("created_at", filters.dateRange.endInclusive.toString())
                        }
                    }
                }
            }
            .decodeList<BlenderReportDto>()
            .map { it.toDomain() }
    }

    private suspend fun getAcidReports(filters: ReportFilters): List<AcidReport> {
        return postgrest.from("acid_reports")
            .select(Columns.ALL) {
                if (filters.fields.isNotEmpty()) {
                    filter {
                        isIn("field_id", filters.fields.map { it.id!! })
                    }
                }

                if (filters.wells.isNotEmpty()) {
                    filter {
                        isIn("well_id", filters.wells.map { it.id!! })
                    }
                }

                if (filters.layers.isNotEmpty()) {
                    filter {
                        isIn("layer_id", filters.layers.map { it.id!! })
                    }
                }

                if (filters.customers.isNotEmpty()) {
                    filter {
                        isIn("customer_id", filters.customers.map { it.id!! })
                    }
                }

                if (filters.dateRange != null) {
                    filter {
                        and {
                            gte("created_at", filters.dateRange.start.toString())
                            lte("created_at", filters.dateRange.endInclusive.toString())
                        }
                    }
                }
            }
            .decodeList<AcidReportDto>()
            .map { it.toDomain() }
    }

    private suspend fun getGelReports(filters: ReportFilters): List<GelReport> {
        return postgrest.from("gel_reports")
            .select(Columns.ALL) {
                if (filters.fields.isNotEmpty()) {
                    filter {
                        isIn("field_id", filters.fields.map { it.id!! })
                    }
                }

                if (filters.wells.isNotEmpty()) {
                    filter {
                        isIn("well_id", filters.wells.map { it.id!! })
                    }
                }

                if (filters.layers.isNotEmpty()) {
                    filter {
                        isIn("layer_id", filters.layers.map { it.id!! })
                    }
                }

                if (filters.customers.isNotEmpty()) {
                    filter {
                        isIn("customer_id", filters.customers.map { it.id!! })
                    }
                }

                if (filters.dateRange != null) {
                    filter {
                        and {
                            gte("created_at", filters.dateRange.start.toString())
                            lte("created_at", filters.dateRange.endInclusive.toString())
                        }
                    }
                }
            }
            .decodeList<GelReportDto>()
            .map { it.toDomain() }
    }

}

class PhotoRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage
) : PhotoRepository {

    override suspend fun uploadPhotoBlenderReport(
        reportId: Int,
        photoTypeName: String,
        photoFile: File,
        attemptNumber: Int,
        report_photo_table_name: String
    ): String? = withContext(Dispatchers.IO) {
        try {
            // 1. Получаем или создаем тип фотографии
            val photoType = getOrCreatePhotoType(photoTypeName)

            // 2. Генерируем уникальное имя файла с учетом попытки
            val fileName = "${photoTypeName.sanitizeFileName()}_attempt${attemptNumber}_${System.currentTimeMillis()}.jpg"

            // 3. Загружаем файл в хранилище
            storage.from("photos")
                .upload(fileName, photoFile.readBytes(), upsert = false)

            // 4. Получаем публичный URL
            val photoUrl = storage.from("photos").publicUrl(fileName)

            // 5. Создаем запись в БД
            val photoRecord = ReportPhotoDto(
                reportId = reportId,
                photoTypeId = photoType.id,
                photoUrl = photoUrl,
                attemptNumber = attemptNumber,
            )

            postgrest.from(report_photo_table_name).insert(photoRecord)

            return@withContext photoUrl
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    private suspend fun getOrCreatePhotoType(name: String): PhotoTypeDto {
        val existingType = postgrest.from("photo_types")
            .select { filter { eq("name", name) } }
            .decodeSingle<PhotoTypeDto>()
        return existingType
    }
}

fun String.sanitizeFileName(): String {
    return this.replace("[^A-Za-z0-9_.-]".toRegex(), "_")
        .replace("__+".toRegex(), "_")
        .trim('_')
}


class AcidReportRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
    @ApplicationContext private val context: Context
) : AcidReportRepository {

    override suspend fun saveReportAndGetId(
        report: AcidReport,
        acidReportCode: String,
        photos: Map<PhotoType, Uri>
    ): Int? {
        return withContext(Dispatchers.IO) {
            try {
                val reportDto = AcidReportDto(
                    employeeId = report.employeeId,
                    labTechnicianId = report.labTechnicianId,
                    fieldId = report.fieldId,
                    wellId = report.wellId,
                    layerId = report.layerId,
                    customerId = report.customerId,
                    reportName = report.reportName,
                    code = report.code,
                    concentratedAcidPercentage = report.concentratedAcidPercentage,
                    preparedAcidPercentage = report.preparedAcidPercentage
                )

                postgrest.from("acid_reports").insert(reportDto)

                val insertedReport = postgrest.from("acid_reports")
                    .select {
                        filter {
                            eq("code", acidReportCode)
                        }
                    }.decodeSingle<AcidReportDto>()
                val reportId = insertedReport.id ?: return@withContext null


                val signatureLinkDto = AcidReportSignatureLinkDto(
                    reportId = reportId,
                    signatureId = reportId
                )
                postgrest.from("acid_report_signatures").insert(signatureLinkDto)


                photos.forEach { (photoType, uri) ->
                    uploadAcidPhoto(
                        reportId = reportId,
                        photoType = photoType,
                        photoUri = uri
                    )
                }

                return@withContext reportId
            } catch (e: Exception) {
                Log.e("AcidReportRepo", "Error saving acid report", e)
                return@withContext null
            }
        }
    }

    private suspend fun uploadAcidPhoto(
        reportId: Int,
        photoType: PhotoType,
        photoUri: Uri
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Получаем или создаем тип фотографии
                val photoTypeName = getPhotoTypeName(photoType)
                val photoTypeRecord = getOrCreatePhotoType(photoTypeName)

                val file = photoUri.toFile(context = context)

                // 3. Генерируем имя файла
                val fileName = "acid_${reportId}_${photoType.name.lowercase()}_${System.currentTimeMillis()}.jpg"

                // 4. Загружаем в хранилище
                storage.from("photos").upload(fileName, file.readBytes(), upsert = false)
                val photoUrl = storage.from("photos").publicUrl(fileName)

                val photoRecord = ReportPhotoDto(
                    reportId = reportId,
                    photoTypeId = photoTypeRecord.id,
                    photoUrl = photoUrl
                )
                postgrest.from("acid_report_photos").insert(photoRecord)

                return@withContext photoUrl
            } catch (e: Exception) {
                Log.e("AcidReportRepo", "Error uploading photo", e)
                return@withContext null
            }
        }
    }

    override suspend fun getAcidReportsForEmployee(employeeId: Int): List<AcidReport> {
        return withContext(Dispatchers.IO) {
            try {
                val reports = postgrest.from("acid_reports")
                    .select {
                        filter { eq("employee_id", employeeId) }
                    }.decodeList<AcidReportDto>()

                reports.map { it.toDomain() }
            } catch (e: Exception) {
                Log.e("AcidReportRepo", "Error fetching reports", e)
                emptyList()
            }
        }
    }
    private fun getPhotoTypeName(photoType: PhotoType): String {
        return when (photoType) {
            PhotoType.PHOTO_5000_GENERAL -> "Фото 5000 общий"
            PhotoType.PHOTO_5000_AFTER_POUR_25_75 -> "Фото 5000 после заливки 25 75"
            PhotoType.PHOTO_5000_AFTER_POUR_50_50 -> "Фото 5000 после заливки 50 50"
            PhotoType.PHOTO_5000_AFTER_POUR_75_25 -> "Фото 5000 после заливки 75 25"
            PhotoType.PHOTO_5000_AFTER_POUR_SPENT -> "Фото 5000 после заливки отработанный"
            PhotoType.PHOTO_2000_GENERAL -> "Фото 2000 общий"
            PhotoType.PHOTO_2000_AFTER_POUR_25_75 -> "Фото 2000 после заливки 25 75"
            PhotoType.PHOTO_2000_AFTER_POUR_50_50 -> "Фото 2000 после заливки 50 50"
            PhotoType.PHOTO_2000_AFTER_POUR_75_25 -> "Фото 2000 после заливки 75 25"
            PhotoType.PHOTO_2000_AFTER_POUR_SPENT -> "Фото 2000 после заливки отработанный"
            PhotoType.PHOTO_DENSIMETER_CONCENTRATED_ACID -> "Фото плотномер концентрированная кислота"
            PhotoType.PHOTO_DENSIMETER_PREPARED_ACID -> "Фото плотномер приготовленная кислота"
            PhotoType.PHOTO_SAMPLING -> ""
            PhotoType.PHOTO_REAGENT -> ""
        }
    }

    private suspend fun getOrCreatePhotoType(name: String): PhotoTypeDto {
        val existingType = postgrest.from("photo_types")
            .select { filter { eq("name", name) } }
            .decodeSingle<PhotoTypeDto>()
        return existingType
    }

    override suspend fun getReportPhotos(reportId: Int): List<ReportPhoto> {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from("report_photos")
                    .select {
                        filter { eq("report_id", reportId) }
                    }.decodeList<ReportPhotoDto>()
                    .map { it.toDomain() }
            } catch (e: Exception) {
                Log.e("AcidReportRepo", "Error fetching photos", e)
                emptyList()
            }
        }
    }
}


class GelReportRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
    @ApplicationContext private val context: Context
) : GelReportRepository {

    override suspend fun saveReportAndGetId(
        report: GelReport,
        gelReportCode: String,
        photoSamplingUri: Uri
    ): Int? {
        return withContext(Dispatchers.IO) {
            try {
                val reportDto = GelReportDto(
                    employeeId = report.employeeId,
                    fieldId = report.fieldId,
                    wellId = report.wellId,
                    layerId = report.layerId,
                    customerId = report.customerId,
                    reportName = report.reportName,
                    code = report.code
                )

                // Вставка основного отчета
                postgrest.from("gel_reports").insert(reportDto)

                val insertedReport = postgrest.from("gel_reports")
                    .select {
                        filter {
                            eq("code", gelReportCode)
                        }
                    }.decodeSingle<GelReportDto>()
                val reportId = insertedReport.id ?: return@withContext null


                val signatureLinkDto = GelReportSignatureLinkDto(
                    reportId = reportId,
                    signatureId = reportId
                )
                postgrest.from("gel_report_signatures").insert(signatureLinkDto)


                uploadGelPhoto(
                    reportId = reportId,
                    photoType = PhotoType.PHOTO_SAMPLING,
                    photoUri = photoSamplingUri
                )



                return@withContext reportId
            } catch (e: Exception) {
                Log.e("GelReportRepo", "Error saving gel report", e)
                return@withContext null
            }
        }
    }

    private suspend fun uploadGelPhoto(
        reportId: Int,
        photoType: PhotoType,
        photoUri: Uri
    ): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Получаем или создаем тип фотографии
                val photoTypeName = getPhotoTypeName(photoType)
                val photoTypeRecord = getOrCreatePhotoType(photoTypeName)

                val file = photoUri.toFile(context = context)

                // 3. Генерируем имя файла
                val fileName = "gel_${reportId}_${photoType.name.lowercase()}_${System.currentTimeMillis()}.jpg"

                // 4. Загружаем в хранилище
                storage.from("photos").upload(fileName, file.readBytes(), upsert = false)
                val photoUrl = storage.from("photos").publicUrl(fileName)

                // 5. Создаем запись в таблице gel_report_photos
                val photoRecord = ReportPhotoDto(
                    reportId = reportId,
                    photoTypeId = photoTypeRecord.id,
                    photoUrl = photoUrl
                )
                postgrest.from("gel_report_photos").insert(photoRecord)

                return@withContext photoUrl
            } catch (e: Exception) {
                Log.e("GelReportRepo", "Error uploading photo", e)
                return@withContext null
            }
        }
    }

    override suspend fun getGelReportsForEmployee(employeeId: Int): List<GelReport> {
        return withContext(Dispatchers.IO) {
            try {
                val reports = postgrest.from("gel_reports")
                    .select {
                        filter { eq("employee_id", employeeId) }
                    }.decodeList<GelReportDto>()

                reports.map { it.toDomain() }
            } catch (e: Exception) {
                Log.e("GelReportRepo", "Error fetching reports", e)
                emptyList()
            }
        }
    }

    override suspend fun getReportPhotos(reportId: Int): List<ReportPhoto> {
        return withContext(Dispatchers.IO) {
            try {
                postgrest.from("gel_report_photos")
                    .select {
                        filter { eq("report_id", reportId) }
                    }.decodeList<ReportPhotoDto>()
                    .map { it.toDomain() }
            } catch (e: Exception) {
                Log.e("GelReportRepo", "Error fetching photos", e)
                emptyList()
            }
        }
    }

    private fun getPhotoTypeName(photoType: PhotoType): String {
        return when (photoType) {
            PhotoType.PHOTO_SAMPLING -> "Фотография отбора пробы (фиксация отбора на скважине)"
            else -> "Фото геля"
        }
    }

    private suspend fun getOrCreatePhotoType(name: String): PhotoTypeDto {
        val existingType = postgrest.from("photo_types")
            .select { filter { eq("name", name) } }
            .decodeSingle<PhotoTypeDto>()
        return existingType
    }
}


