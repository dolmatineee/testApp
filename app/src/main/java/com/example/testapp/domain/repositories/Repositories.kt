package com.example.testapp.domain.repositories

import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Laboratorian
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Reagent
import com.example.testapp.domain.models.Report
import com.example.testapp.domain.models.TestAttempt
import com.example.testapp.domain.models.Well
import java.io.File

interface FieldRepository {
    suspend fun getFields(): List<Field>
}

interface WellRepository {
    suspend fun getWells(): List<Well>
}

interface LayerRepository {
    suspend fun getLayers(): List<Layer>
}

interface CustomerRepository {
    suspend fun getCustomers(): List<Customer>
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
       report: Report,
       file: File
    ): Int?

    suspend fun updateReportReagents(
        reportId: Int,
        reagents: List<Reagent>
    ): Boolean

    suspend fun getReagentIdByName(
        reagentName: String
    ): Int?
}

interface ReportRepository {
    suspend fun getReports(employeeId: Int): List<Report>?
}