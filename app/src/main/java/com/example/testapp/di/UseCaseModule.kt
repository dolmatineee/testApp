package com.example.testapp.di

import com.example.testapp.domain.usecases.GetAllEmployees
import com.example.testapp.domain.usecases.GetAllPositions
import com.example.testapp.domain.usecases.GetBlenderPhotos
import com.example.testapp.domain.usecases.GetBlenderReports
import com.example.testapp.domain.usecases.GetCustomers
import com.example.testapp.domain.usecases.GetEmployeeById
import com.example.testapp.domain.usecases.GetFields
import com.example.testapp.domain.usecases.GetLaboratorians
import com.example.testapp.domain.usecases.GetLayers
import com.example.testapp.domain.usecases.GetReportTypes
import com.example.testapp.domain.usecases.GetReports
import com.example.testapp.domain.usecases.GetStatuses
import com.example.testapp.domain.usecases.GetWells
import com.example.testapp.domain.usecases.InsertAcidReport
import com.example.testapp.domain.usecases.InsertBlenderReport
import com.example.testapp.domain.usecases.InsertGelReport
import com.example.testapp.domain.usecases.InsertPhotoReport
import com.example.testapp.domain.usecases.LoginEmployee
import com.example.testapp.domain.usecases.SearchEmployees
import com.example.testapp.domain.usecases.UpdateEmployee
import com.example.testapp.domain.usecases.UploadEngineerSignatureBlenderReport
import com.example.testapp.domain.usecases.UploadSupervisorSignatureBlenderReport
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideGetFields(
        fieldRepositoryImpl: FieldRepositoryImpl
    ): GetFields {
        return GetFields(fieldRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideGetStatuses(
        statusRepositoryImpl: StatusRepositoryImpl
    ): GetStatuses {
        return GetStatuses(statusRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideGetWells(
        wellRepositoryImpl: WellRepositoryImpl
    ): GetWells {
        return GetWells(wellRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideGetLayers(
        layerRepositoryImpl: LayerRepositoryImpl
    ): GetLayers {
        return GetLayers(layerRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideGetCustomers(
        customerRepositoryImpl: CustomerRepositoryImpl
    ): GetCustomers {
        return GetCustomers(customerRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideGetReportTypes(
        reportTypeRepositoryImpl: ReportTypeRepositoryImpl
    ): GetReportTypes {
        return GetReportTypes(reportTypeRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideLoginEmployee(
        employeeRepositoryImpl: EmployeeRepositoryImpl
    ): LoginEmployee {
        return LoginEmployee(employeeRepositoryImpl)
    }
    @Module
    @InstallIn(SingletonComponent::class)
    object EmployeesUseCaseModule {

        @Provides
        @Singleton
        fun provideGetAllEmployees(
            employeeRepositoryImpl: EmployeeRepositoryImpl
        ): GetAllEmployees {
            return GetAllEmployees(employeeRepositoryImpl)
        }

        @Provides
        @Singleton
        fun provideGetEmployeeById(
            employeeRepositoryImpl: EmployeeRepositoryImpl
        ): GetEmployeeById {
            return GetEmployeeById(employeeRepositoryImpl)
        }

        @Provides
        @Singleton
        fun provideUpdateEmployee(
            employeeRepositoryImpl: EmployeeRepositoryImpl
        ): UpdateEmployee {
            return UpdateEmployee(employeeRepositoryImpl)
        }

        @Provides
        @Singleton
        fun provideGetAllPositions(
            employeeRepositoryImpl: EmployeeRepositoryImpl
        ): GetAllPositions {
            return GetAllPositions(employeeRepositoryImpl)
        }


        @Provides
        @Singleton
        fun provideSearchEmployees(
            employeeRepositoryImpl: EmployeeRepositoryImpl
        ): SearchEmployees {
            return SearchEmployees(employeeRepositoryImpl)
        }
    }
    @Provides
    @Singleton
    fun provideGetLaboratorians(
        employeeRepositoryImpl: EmployeeRepositoryImpl
    ): GetLaboratorians {
        return GetLaboratorians(employeeRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideInsertBlenderReport(
        reportBlenderRepositoryImpl: ReportBlenderRepositoryImpl
    ): InsertBlenderReport {
        return InsertBlenderReport(reportBlenderRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideGetReport(
        reportRepositoryImpl: ReportRepositoryImpl
    ): GetReports {
        return GetReports(reportRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideInsertPhotoBlenderReport(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): InsertPhotoReport {
        return InsertPhotoReport(photoRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideGetBlenderReport(
        reportBlenderRepositoryImpl: ReportBlenderRepositoryImpl
    ): GetBlenderReports {
        return GetBlenderReports(reportBlenderRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideGetBlenderPhotos(
        reportBlenderRepositoryImpl: ReportBlenderRepositoryImpl
    ): GetBlenderPhotos {
        return GetBlenderPhotos(reportBlenderRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideUploadSupervisorSignatureBlenderReport(
        reportBlenderRepositoryImpl: ReportBlenderRepositoryImpl
    ): UploadSupervisorSignatureBlenderReport {
        return UploadSupervisorSignatureBlenderReport(reportBlenderRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideUploadEngineerSignatureBlenderReport(
        reportBlenderRepositoryImpl: ReportBlenderRepositoryImpl
    ): UploadEngineerSignatureBlenderReport {
        return UploadEngineerSignatureBlenderReport(reportBlenderRepositoryImpl)
    }


    @Provides
    @Singleton
    fun provideInsertAcidReport(
        acidReportRepositoryImpl: AcidReportRepositoryImpl
    ): InsertAcidReport {
        return InsertAcidReport(acidReportRepositoryImpl)
    }


    @Provides
    @Singleton
    fun provideInsertGelReport(
        gelReportRepositoryImpl: GelReportRepositoryImpl
    ): InsertGelReport {
        return InsertGelReport(gelReportRepositoryImpl)
    }
}