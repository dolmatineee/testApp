package com.example.testapp.remote.models

import com.example.testapp.domain.models.Customer
import com.example.testapp.domain.models.Employee
import com.example.testapp.domain.models.Field
import com.example.testapp.domain.models.Layer
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
    @SerialName("position_name")
    val positionName: String
)