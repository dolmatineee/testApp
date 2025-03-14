package com.example.testapp.di

import com.example.testapp.domain.repositories.EmployeeRepository
import com.example.testapp.domain.repositories.FieldRepository
import com.example.testapp.domain.usecases.GetCustomers
import com.example.testapp.domain.usecases.GetFields
import com.example.testapp.domain.usecases.GetLayers
import com.example.testapp.domain.usecases.GetWells
import com.example.testapp.domain.usecases.InsertBlenderReport
import com.example.testapp.domain.usecases.LoginEmployee
import com.example.testapp.remote.repositories.CustomerRepositoryImpl
import com.example.testapp.remote.repositories.EmployeeRepositoryImpl
import com.example.testapp.remote.repositories.FieldRepositoryImpl
import com.example.testapp.remote.repositories.LayerRepositoryImpl
import com.example.testapp.remote.repositories.ReportBlenderRepositoryImpl
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
    fun provideLoginEmployee(
        employeeRepositoryImpl: EmployeeRepositoryImpl
    ): LoginEmployee {
        return LoginEmployee(employeeRepositoryImpl)
    }

    @Provides
    @Singleton
    fun provideInsertBlenderReport(
        reportBlenderRepositoryImpl: ReportBlenderRepositoryImpl
    ): InsertBlenderReport {
        return InsertBlenderReport(reportBlenderRepositoryImpl)
    }
}