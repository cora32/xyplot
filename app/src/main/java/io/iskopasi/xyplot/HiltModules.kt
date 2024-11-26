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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HiltModules {
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
