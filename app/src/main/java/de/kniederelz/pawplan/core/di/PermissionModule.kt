package de.kniederelz.pawplan.core.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionManager
import de.kniederelz.pawplan.tracking.permissions.WalkingTrackerPermissionManagerImp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PermissionModule {
    @Provides
    @Singleton
    fun providePermissionManager(
        @ApplicationContext context: Context
    ): WalkingTrackerPermissionManager {
        return WalkingTrackerPermissionManagerImp(context)
    }
}