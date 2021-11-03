
package com.goshapp.geoguessrgame.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import com.goshapp.geoguessrgame.data.local.FoodiumPostsDatabase
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class FoodiumDatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(application: Application) = FoodiumPostsDatabase.getInstance(application)

    @Singleton
    @Provides
    fun providePostsDao(database: FoodiumPostsDatabase) = database.getPostsDao()
}
