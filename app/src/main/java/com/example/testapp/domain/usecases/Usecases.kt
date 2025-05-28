package com.example.testapp.domain.usecases

import android.content.Context
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
import com.example.testapp.domain.models.Position
import com.example.testapp.domain.models.ReportFilters
import com.example.testapp.domain.models.ReportPhoto
import com.example.testapp.domain.models.ReportStatus
import com.example.testapp.domain.models.ReportType
import com.example.testapp.domain.models.ReportTypeEnum
import com.example.testapp.domain.models.Well
import com.example.testapp.remote.models.ReportStatusDto
import com.example.testapp.remote.repositories.AcidReportRepositoryImpl
import com.example.testapp.remote.repositories.CustomerRepositoryImpl
import com.example.testapp.remote.repositories.EmployeeRepositoryImpl
import com.example.testapp.remote.repositories.FieldRepositoryImpl
import com.example.testapp.remote.repositories.GelReportRepositoryImpl
import com.example.testapp.remote.repositories.LayerRepositoryImpl
import com.example.testapp.remote.repositories.PhotoRepositoryImpl
import com.example.testapp.remote.repositories.ReportBlenderRepositoryImpl
import com.example.testapp.remote.repositories.ReportRepositoryImpl
import com.example.testapp.remote.repositories.ReportTypeRepositoryImpl
import com.example.testapp.remote.repositories.StatusRepositoryImpl
import com.example.testapp.remote.repositories.WellRepositoryImpl
import com.example.testapp.utils.PhotoType
import java.io.File
import javax.inject.Inject

// GetFields.kt
class GetFields @Inject constructor(
    private val fieldRepositoryImpl: FieldRepositoryImpl
) {
    suspend operator fun invoke(): List<Field> {
        return fieldRepositoryImpl.getFields()
    }

    suspend operator fun invoke(id: Int): Field? {
        return fieldRepositoryImpl.getFieldById(id)
    }
}

class GetStatuses @Inject constructor(
    private val statusRepositoryImpl: StatusRepositoryImpl
) {
    suspend operator fun invoke(): List<ReportStatus> {
        return statusRepositoryImpl.getStatuses()
    }

    suspend operator fun invoke(id: Int): ReportStatus? {
        return statusRepositoryImpl.getStatusById(id)
    }
}

// GetWells.kt
class GetWells @Inject constructor(
    private val wellRepositoryImpl: WellRepositoryImpl
) {
    suspend operator fun invoke(): List<Well> {
        return wellRepositoryImpl.getWells()
    }

    suspend operator fun invoke(id: Int): Well? {
        return wellRepositoryImpl.getWellById(id)
    }
}

class GetLayers @Inject constructor(
    private val layerRepositoryImpl: LayerRepositoryImpl
) {
    suspend operator fun invoke(): List<Layer> {
        return layerRepositoryImpl.getLayers()
    }

    suspend operator fun invoke(id: Int): Layer? {
        return layerRepositoryImpl.getLayerById(id)
    }
}

class GetCustomers @Inject constructor(
    private val customerRepositoryImpl: CustomerRepositoryImpl
) {
    suspend operator fun invoke(): List<Customer> {
        return customerRepositoryImpl.getCustomers()
    }

    suspend operator fun invoke(id: Int): Customer? {
        return customerRepositoryImpl.getCustomerById(id)
    }
}

class GetReportTypes @Inject constructor(
    private val reportTypeRepositoryImpl: ReportTypeRepositoryImpl
) {
    suspend operator fun invoke(): List<ReportType> {
        return reportTypeRepositoryImpl.getReportTypes()
    }

}

class LoginEmployee @Inject constructor(
    private val employeeRepositoryImpl: EmployeeRepositoryImpl
) {
    suspend operator fun invoke(phoneNumber: String, password: String): Employee? {
        return employeeRepositoryImpl.login(phoneNumber, password)
    }

    suspend operator fun invoke(phoneNumber: String): Int? {
        return employeeRepositoryImpl.getEmployeeIdByPhone(phoneNumber)
    }
}

class GetLaboratorians @Inject constructor(
    private val employeeRepositoryImpl: EmployeeRepositoryImpl
) {
    suspend operator fun invoke(): List<Laboratorian> {
        return employeeRepositoryImpl.getLaboratorians()
    }
}

class GetAllEmployees @Inject constructor(
    private val employeeRepositoryImpl: EmployeeRepositoryImpl
) {
    suspend operator fun invoke(): List<Employee> {
        return employeeRepositoryImpl.getAllEmployees()
    }
}

// Получение сотрудника по ID
class GetEmployeeById @Inject constructor(
    private val employeeRepositoryImpl: EmployeeRepositoryImpl
) {
    suspend operator fun invoke(id: Int): Employee? {
        return employeeRepositoryImpl.getEmployeeById(id)
    }
}

// Обновление данных сотрудника
class UpdateEmployee @Inject constructor(
    private val employeeRepositoryImpl: EmployeeRepositoryImpl
) {
    suspend operator fun invoke(employee: Employee): Boolean {
        return employeeRepositoryImpl.updateEmployee(employee)
    }
}

// Получение всех должностей
class GetAllPositions @Inject constructor(
    private val employeeRepositoryImpl: EmployeeRepositoryImpl
) {
    suspend operator fun invoke(): List<Position> {
        return employeeRepositoryImpl.getAllPositions()
    }
}



// Поиск сотрудников по имени
class SearchEmployees @Inject constructor(
    private val employeeRepositoryImpl: EmployeeRepositoryImpl
) {
    suspend operator fun invoke(query: String): List<Employee> {
        return employeeRepositoryImpl.getAllEmployees()
            .filter { it.fullName.contains(query, ignoreCase = true) }
    }
}

class InsertBlenderReport @Inject constructor(
    private val blenderRepositoryImpl: ReportBlenderRepositoryImpl
) {
    suspend operator fun invoke(
        report: BlenderReport,
        blenderReportCode: String,
        reportFile: File,

        context: Context
    ): Int? {
        return blenderRepositoryImpl.insertReportBlender(
            report,
            blenderReportCode,
            reportFile,
            context
        )
    }

    suspend operator fun invoke(reportsId: Int, reagents: List<Reagent>): Boolean {
        return blenderRepositoryImpl.updateReportReagents(reportsId, reagents)
    }

    suspend operator fun invoke(reagentName: String): Int? {
        return blenderRepositoryImpl.getReagentIdByName(reagentName)
    }
}


class InsertAcidReport @Inject constructor(
    private val acidReportRepositoryImpl: AcidReportRepositoryImpl
) {
    suspend operator fun invoke(
        report: AcidReport,
        acidReportCode: String,
        photos: Map<PhotoType, Uri>,
        reportFile: File,
        context: Context
    ): Int? {
        return acidReportRepositoryImpl.saveReportAndGetId(
            report = report,
            acidReportCode = acidReportCode,
            photos = photos,
            reportFile = reportFile,
            context = context
        )
    }

}

class InsertGelReport @Inject constructor(
    private val gelReportRepositoryImpl: GelReportRepositoryImpl
) {
    suspend operator fun invoke(
        report: GelReport,
        gelReportCode: String,
        photoSamplingUri: Uri
    ): Int? {
        return gelReportRepositoryImpl.saveReportAndGetId(
            report = report,
            gelReportCode = gelReportCode,
            photoSamplingUri = photoSamplingUri
        )
    }

}

class GetReports @Inject constructor(
    private val reportRepositoryImpl: ReportRepositoryImpl
) {
    suspend operator fun invoke(filters: ReportFilters): List<BaseReport> {
        return reportRepositoryImpl.getReports(
            filters = filters
        )
    }
}

class InsertPhotoReport @Inject constructor(
    private val photoRepositoryImpl: PhotoRepositoryImpl
) {
    suspend operator fun invoke(
        reportId: Int,
        photoTypeName: String,
        photoFile: File,
        attemptNumber: Int,
        report_photo_table_name: String
    ): String? {
        return photoRepositoryImpl.uploadPhotoBlenderReport(
            reportId = reportId,
            photoTypeName = photoTypeName,
            photoFile = photoFile,
            attemptNumber = attemptNumber,
            report_photo_table_name = report_photo_table_name,
        )
    }
}

class GetSupervisorReports @Inject constructor(
    private val blenderRepositoryImpl: ReportBlenderRepositoryImpl
) {
    suspend operator fun invoke(): List<BaseReport> {
        return blenderRepositoryImpl.getSupervisorReports()
    }
}


class GetEngineerReports @Inject constructor(
    private val blenderRepositoryImpl: ReportBlenderRepositoryImpl
) {
    suspend operator fun invoke(): List<BaseReport> {
        return blenderRepositoryImpl.getEngineerReports()
    }
}


class GetBlenderReports @Inject constructor(
    private val blenderRepositoryImpl: ReportBlenderRepositoryImpl
) {
    suspend operator fun invoke(): List<BlenderReport> {
        return blenderRepositoryImpl.getBlenderReportsSupervisor()
    }


}


class GetBlenderPhotos @Inject constructor(
    private val blenderRepositoryImpl: ReportBlenderRepositoryImpl
) {
    suspend operator fun invoke(reportId: Int, reportType: ReportTypeEnum): List<ReportPhoto> {
        return blenderRepositoryImpl.getReportPhotos(reportId, reportType)
    }

}


class UploadSupervisorSignatureBlenderReport @Inject constructor(
    private val blenderRepositoryImpl: ReportBlenderRepositoryImpl
) {
    suspend operator fun invoke(
        reportId: Int,
        reportType: ReportTypeEnum,
        signatureFile: File
    ): String? {
        return blenderRepositoryImpl.uploadSupervisorSignature(reportId, reportType, signatureFile)
    }

}

class UploadEngineerSignatureBlenderReport @Inject constructor(
    private val blenderRepositoryImpl: ReportBlenderRepositoryImpl
) {
    suspend operator fun invoke(
        reportId: Int,
        reportType: ReportTypeEnum,
        signatureFile: File
    ): String? {
        return blenderRepositoryImpl.uploadEngineerSignature(reportId, reportType, signatureFile)
    }

}
