package com.example.testapp.remote.repositories

import android.util.Log

import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Laboratorian
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.Report
import com.example.testapp.domain.models.TestAttempt
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.repositories.CustomerRepository
import com.example.testapp.domain.repositories.EmployeeRepository
import com.example.testapp.domain.repositories.FieldRepository
import com.example.testapp.domain.repositories.LayerRepository
import com.example.testapp.domain.repositories.ReportBlenderRepository
import com.example.testapp.domain.repositories.ReportRepository
import com.example.testapp.domain.repositories.WellRepository
import com.example.testapp.remote.models.CustomerDto
import com.example.testapp.remote.models.EmployeeDto
import com.example.testapp.remote.models.FieldDto
import com.example.testapp.remote.models.LaboratorianDto
import com.example.testapp.remote.models.LayerDto
import com.example.testapp.remote.models.PositionDto
import com.example.testapp.remote.models.ReagentDto
import com.example.testapp.remote.models.ReportDto
import com.example.testapp.remote.models.ReportReagentLinkDto
import com.example.testapp.remote.models.ReportTestDetailDto
import com.example.testapp.remote.models.WellDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
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
}




class EmployeeRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest
) : EmployeeRepository {

    override suspend fun login(phoneNumber: String, password: String): Employee? {
        return withContext(Dispatchers.IO) {
            // Выполняем запрос к таблице employees
            val query = postgrest.from("employees")
                .select(
                    Columns.list("id", "full_name", "phone_number", "password", "positions(position_name)")
                ) {
                    filter {
                        eq("phone_number", phoneNumber)
                        eq("password", password)
                    }
                }

            // Получаем результат запроса
            val employeeDtoList = query.decodeList<EmployeeDto>()

            // Если найден хотя бы один сотрудник, преобразуем его в доменный объект и возвращаем
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
                        eq("position_id", laboratorianPosition.id)
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
        report: Report,
        file: File
    ): Int? {
        return withContext(Dispatchers.IO) {
            val fileName = "report_${report.reportName}_${System.currentTimeMillis()}.docx"
            storage.from("reports")
                .upload(fileName, file.readBytes(), upsert = false)

            val fileUrl = storage.from("reports")
                .publicUrl(fileName)

            val reportDto = ReportDto(
                employeeId = report.employeeId,
                fieldId = report.fieldId,
                wellId = report.wellId,
                layerId = report.layerId,
                customerId = report.customerId,
                fileUrl = fileUrl,
                createdAt = null,
                reportName = report.reportName
            )

            postgrest.from("reports").insert(reportDto)

            val insertedReport = postgrest.from("reports")
                .select {
                    filter {
                        eq("file_url", fileUrl)
                    }
                }.decodeSingle<ReportDto>()
            val reportId = insertedReport.id

            reportId
        }
    }

    override suspend fun updateReportReagents(reportId: Int, reagents: List<Reagent>): Boolean {
        return withContext(Dispatchers.IO) {
            Log.e("gfhgfghf", reagents.toString())
            reagents.forEach { reagent ->
                val reagentLinkDto = ReportReagentLinkDto(
                    reportId = reportId,
                    reagentId = reagent.id
                )
                postgrest.from("report_reagent_links")
                    .insert(reagentLinkDto)

                reagent.tests.forEach { test ->
                    val testDetailDto = ReportTestDetailDto(
                        reportId = reportId,
                        reagentId = reagent.id,
                        flowRate = test.flowRate,
                        concentration = test.concentration,
                        testTime = test.testTime,
                        actualAmount = test.actualAmount
                    )
                    postgrest.from("report_test_details")
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

}

class ReportRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : ReportRepository {

    override suspend fun getReports(employeeId: Int): List<Report>? {
        return withContext(Dispatchers.IO) {
            val reportsDto = postgrest.from("reports")
                .select {
                    filter {
                        eq("employee_id", employeeId)
                    }
                }
                .decodeList<ReportDto>()
            Log.e("ReportRepositoryImpl", reportsDto.toString())
            reportsDto.map { it.toDomain() }
        }

    }

}


