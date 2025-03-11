package com.example.testapp.domain.repositories

import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
import com.example.testapp.domain.models.Well

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
}