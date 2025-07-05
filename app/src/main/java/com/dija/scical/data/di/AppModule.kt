package com.dija.scical.data.di

import com.dija.scical.data.repository.CalculatorRepository
import com.dija.scical.data.repository.impl.InMemoryCalculatorRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRepository(): CalculatorRepository = InMemoryCalculatorRepository()
}