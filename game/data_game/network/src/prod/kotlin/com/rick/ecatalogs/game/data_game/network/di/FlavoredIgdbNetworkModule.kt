package com.rick.ecatalogs.game.data_game.network.di

import com.rick.ecatalogs.game.data_game.network.IgdbNetworkDataSource
import com.rick.ecatalogs.game.data_game.network.retrofit.IgdbRetrofitNetwork
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface FlavoredIgdbNetworkModule {
    @Binds
    fun binds(impl: IgdbRetrofitNetwork): IgdbNetworkDataSource
}
