package de.kniederelz.pawplan.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.core.time.ClockProvider
import de.kniederelz.pawplan.core.time.SystemClockProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TimeModule {
    @Provides
    @Singleton
    fun provideClockProvider(): ClockProvider {
        return SystemClockProvider()
    }
}