package com.rick.moviecatalog.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
//
//    @Provides
//    @Singleton
//    fun providesMovieCatalogApi(): MovieCatalogApi {
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .client(
//                OkHttpClient.Builder()
//                    .addInterceptor(HttpLoggingInterceptor().apply {
//                        level = HttpLoggingInterceptor.Level.BASIC
//                    }).build()
//            )
//            .build()
//            .create()
//    }
//
//    @Provides
//    @Singleton
//    fun providesMovieDatabase(@ApplicationContext context: Context): MovieCatalogDatabase =
//        Room.databaseBuilder(
//            context,
//            MovieCatalogDatabase::class.java,
//            DATABASE_NAME
//        ).addTypeConverter(Converters(GsonParser(Gson()))).build()
//
//    @Provides
//    @Singleton
//    fun bindMovieCatalogRepository(api: MovieCatalogApi, db: MovieCatalogDatabase, @ApplicationContext context: Context):
//            MovieCatalogRepository = MovieCatalogRepository(api, db, context)
}