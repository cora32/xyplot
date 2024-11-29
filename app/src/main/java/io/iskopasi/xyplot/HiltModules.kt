package io.iskopasi.xyplot

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.iskopasi.xyplot.api.Repository
import io.iskopasi.xyplot.api.RestApi
import io.iskopasi.xyplot.api.getRetrofit
import io.iskopasi.xyplot.room.PointsDao
import io.iskopasi.xyplot.room.getDB
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher

@Retention
@Qualifier
annotation class MainDispatcher

@Module
@InstallIn(SingletonComponent::class)
class HiltModules {
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineContext = Dispatchers.IO

    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineContext = Dispatchers.Default

    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineContext = Dispatchers.Main

    @Provides
    @Singleton
    fun getRestApi(): RestApi = getRetrofit().create(RestApi::class.java)

    @Provides
    @Singleton
    fun getDao(@ApplicationContext context: Context): PointsDao =
        getDB(context).dao()

    @Provides
    @Singleton
    fun getRepo(@ApplicationContext context: Context): Repository =
        Repository(getRestApi(), getDao(context))
}
