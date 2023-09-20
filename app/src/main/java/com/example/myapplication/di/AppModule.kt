package com.example.myapplication.di

import android.app.Application
import com.example.myapplication.data.*
import com.example.myapplication.data.dataSource.BeaconDataSource
import com.example.myapplication.data.dataSource.BeaconDataSourceImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesFirebaseAuth()  = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun providesRepositoryImpl(firebaseAuth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth)
    }


    @Provides
    @Singleton
    fun providesBeaconDataSource(app:Application, db: DataBaseRepository): BeaconDataSource{
        return BeaconDataSourceImpl(app, db)
    }

    @Provides
    @Singleton
    fun providesBeaconRepositoryImpl(beaconDataSource: BeaconDataSource): BeaconRepository{
        return BeaconRepositoryImpl(beaconDataSource)
    }

    @Provides
    @Singleton
    fun providesDataBaseRepositoryImpl(): DataBaseRepository{
        return DataBaseRepositoryImpl()
    }
}