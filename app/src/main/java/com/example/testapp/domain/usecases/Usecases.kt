package com.example.testapp.domain.usecases

import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Well
import com.example.testapp.domain.repositories.EmployeeRepository
import com.example.testapp.remote.repositories.CustomerRepositoryImpl
import com.example.testapp.remote.repositories.EmployeeRepositoryImpl
import com.example.testapp.remote.repositories.FieldRepositoryImpl
import com.example.testapp.remote.repositories.LayerRepositoryImpl
import com.example.testapp.remote.repositories.WellRepositoryImpl
import javax.inject.Inject

// GetFields.kt
class GetFields @Inject constructor(
    private val fieldRepositoryImpl: FieldRepositoryImpl
) {
    suspend operator fun invoke(): List<Field> {
        return fieldRepositoryImpl.getFields()
    }
}

// GetWells.kt
class GetWells @Inject constructor(
    private val wellRepositoryImpl: WellRepositoryImpl
) {
    suspend operator fun invoke(): List<Well> {
        return wellRepositoryImpl.getWells()
    }
}

class GetLayers @Inject constructor(
    private val layerRepositoryImpl: LayerRepositoryImpl
) {
    suspend operator fun invoke(): List<Layer> {
        return layerRepositoryImpl.getLayers()
    }
}

class GetCustomers @Inject constructor(
    private val customerRepositoryImpl: CustomerRepositoryImpl
) {
    suspend operator fun invoke(): List<Customer> {
        return customerRepositoryImpl.getCustomers()
    }
}

class LoginEmployee @Inject constructor(
    private val employeeRepositoryImpl: EmployeeRepositoryImpl
) {
    suspend operator fun invoke(phoneNumber: String, password: String): Employee? {
        return employeeRepositoryImpl.login(phoneNumber, password)
    }
}