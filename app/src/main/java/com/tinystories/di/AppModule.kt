package com.tinystories.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context
    }
    
    // TODO: Add repository and ML service providers when implemented
    /*
    @Provides
    @Singleton
    fun provideStoryRepository(
        @ApplicationContext context: Context
    ): StoryRepository {
        return StoryRepositoryImpl(context)
    }
    
    @Provides
    @Singleton
    fun provideMLRepository(
        @ApplicationContext context: Context
    ): MLRepository {
        return MLRepositoryImpl(context)
    }
    */
}