package de.kniederelz.pawplan.core.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.user.data.FirestoreUserRepositoryImpl
import de.kniederelz.pawplan.user.domain.UserRepository
import kotlinx.coroutines.coroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UserModule {
    @Provides
    @Singleton
    fun provideRepository(): UserRepository {
        return FirestoreUserRepositoryImpl(
             FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance(),

        )
    }
}
