package com.onthecrow.caffeine.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @DispatcherMain
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    @Provides
    @DispatcherIO
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @DispatcherDefault
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}