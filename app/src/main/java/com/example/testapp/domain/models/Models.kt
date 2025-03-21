package com.example.testapp.domain.models

import android.net.Uri

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



data class Report(
    val id: Int? = null,
    val employeeId: Int,
    val fieldId: Int,
    val wellId: Int,
    val layerId: Int,
    val customerId: Int,
    val fileUrl: String? = null,
    val createdAt: String?,
    val reportName: String,
    val reagents: List<Reagent>
)

data class Reagent(
    val id: Int,
    val name: String,
    val tests: List<ReportTestDetail>
)

data class ReportReagentLink(
    val reportId: Int? = null,
    val reagentId: Int
)

data class ReportTestDetail(
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