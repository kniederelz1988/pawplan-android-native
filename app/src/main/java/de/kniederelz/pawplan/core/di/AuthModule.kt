package de.kniederelz.pawplan.core.di

import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.auth.data.FirebaseAuthRepositoryImpl
import de.kniederelz.pawplan.auth.domain.AuthRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideRepository(): AuthRepository {
        return FirebaseAuthRepositoryImpl(FirebaseAuth.getInstance())
    }
}