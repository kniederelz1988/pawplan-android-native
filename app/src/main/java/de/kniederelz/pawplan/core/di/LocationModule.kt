package de.kniederelz.pawplan.core.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.tracking.repositories.location.data.LocationRepositoryImpl
import de.kniederelz.pawplan.tracking.repositories.location.domain.LocationRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocationModule {
    @Provides
    @Singleton
    fun provideRepository(
        @ApplicationContext context: Context
    ): LocationRepository {
        return LocationRepositoryImpl(context)
    }
}
