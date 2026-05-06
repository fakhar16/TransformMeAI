package com.transformmeai.di

import com.transformmeai.data.repository.TransformRepositoryImpl
import com.transformmeai.domain.repository.TransformRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTransformRepository(impl: TransformRepositoryImpl): TransformRepository
}
