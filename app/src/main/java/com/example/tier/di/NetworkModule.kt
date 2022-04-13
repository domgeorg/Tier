package com.example.tier.di

import com.example.tier.common.definitions.Definitions
import com.example.tier.network.NetworkApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideService(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): NetworkApi =
        Retrofit.Builder()
            .baseUrl(Definitions.DOMAIN)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
            .create(NetworkApi::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .connectTimeout(Definitions.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(Definitions.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(Definitions.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .build()

    @Singleton
    @Provides
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi).withNullSerialization()

    @Singleton
    @Provides
    fun provideMoshi(): Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
}
