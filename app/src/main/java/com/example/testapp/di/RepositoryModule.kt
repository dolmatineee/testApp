package com.example.testapp.di

import com.example.testapp.domain.repositories.CustomerRepository
import com.example.testapp.domain.repositories.EmployeeRepository
import com.example.testapp.domain.repositories.FieldRepository
import com.example.testapp.domain.repositories.LayerRepository
import com.example.testapp.domain.repositories.PhotoRepository
import com.example.testapp.domain.repositories.ReportBlenderRepository
import com.example.testapp.domain.repositories.ReportRepository
import com.example.testapp.domain.repositories.WellRepository
import com.example.testapp.remote.repositories.CustomerRepositoryImpl
import com.example.testapp.remote.repositories.EmployeeRepositoryImpl
import com.example.testapp.remote.repositories.FieldRepositoryImpl
import com.example.testapp.remote.repositories.LayerRepositoryImpl
import com.example.testapp.remote.repositories.PhotoRepositoryImpl
import com.example.testapp.remote.repositories.ReportBlenderRepositoryImpl
import com.example.testapp.remote.repositories.ReportRepositoryImpl
import com.example.testapp.remote.repositories.WellRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFieldRepository(
        fieldRepositoryImpl: FieldRepositoryImpl
    ): FieldRepository

    @Binds
    @Singleton
    abstract fun bindWellRepository(
        wellRepositoryImpl: WellRepositoryImpl
    ): WellRepository

    @Binds
    @Singleton
    abstract fun bindEmployeeRepository(
        employeeRepositoryImpl: EmployeeRepositoryImpl
    ): EmployeeRepository

    @Binds
    @Singleton
    abstract fun bindLayerRepository(
        layerRepositoryImpl: LayerRepositoryImpl
    ): LayerRepository

    @Binds
    @Singleton
    abstract fun bindCustomerRepository(
        customerRepositoryImpl: CustomerRepositoryImpl
    ): CustomerRepository

    @Binds
    @Singleton
    abstract fun bindReportBlenderRepository(
        reportBlenderRepositoryImpl: ReportBlenderRepositoryImpl
    ): ReportBlenderRepository

    @Binds
    @Singleton
    abstract fun bindReportRepository(
        reportRepositoryImpl: ReportRepositoryImpl
    ): ReportRepository

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(
        photoRepositoryImpl: PhotoRepositoryImpl
    ): PhotoRepository
}