package de.kniederelz.pawplan.core.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.tracking.repositories.reports.data.FirestoreIncidentReportRepositoryImpl
import de.kniederelz.pawplan.tracking.repositories.reports.domain.IncidentReportRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ReportModule {
    @Provides
    @Singleton
    fun provideRepository(): IncidentReportRepository {
        return FirestoreIncidentReportRepositoryImpl(
            FirebaseFirestore.getInstance()
        )
    }
}