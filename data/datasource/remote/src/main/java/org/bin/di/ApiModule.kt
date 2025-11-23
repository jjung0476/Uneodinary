package org.bin.di

import org.bin.demo.repository.model.interfaces.AppRepository

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import org.bin.demo.repository.model.interfaces.ApiService

@Module
// 애플리케이션 수명 주기 동안 단일 인스턴스 유지 (Singleton)
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val BASE_URL = "http://13.209.89.222:8080/"

    // Retrofit 인스턴스 제공
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // OkHttpClient 인스턴스 제공
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            // 필요한 경우 인터셉터 추가 (예: 로깅, 토큰)
            .build()
    }

    // ApiService 인터페이스 구현체 제공
    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAppRepository(apiService: ApiService): AppRepository {
        return AppRepository(apiService)
    }
}