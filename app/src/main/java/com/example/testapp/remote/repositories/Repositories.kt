package com.example.testapp.remote.repositories

import android.util.Log
import com.example.testapp.BuildConfig
import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
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
import com.example.testapp.domain.repositories.WellRepository
import com.example.testapp.remote.models.CustomerDto
import com.example.testapp.remote.models.EmployeeDto
import com.example.testapp.remote.models.FieldDto
import com.example.testapp.remote.models.LayerDto
import com.example.testapp.remote.models.ReportDto
import com.example.testapp.remote.models.WellDto
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
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
}

class ReportBlenderRepositoryImpl @Inject constructor(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : ReportBlenderRepository {
    override suspend fun insertReportBlender(
        report: Report
    ): Boolean {
        return try {
            true
        } catch (e: Exception) {
            throw e
        }
    }

}

private fun buildImageUrl(fileName: String) =
    "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/${fileName}"
