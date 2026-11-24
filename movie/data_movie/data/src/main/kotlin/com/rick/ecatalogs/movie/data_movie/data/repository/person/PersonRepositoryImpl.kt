package com.rick.ecatalogs.movie.data_movie.data.repository.person

import com.rick.ecatalogs.movie.data_movie.data.model.asPerson
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.Person
import com.rick.ecatalogs.movie.data_movie.network.TmdbNetworkDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

internal class PersonRepositoryImpl @Inject constructor(
    private val network: TmdbNetworkDataSource,
) : PersonRepository {

    // Network errors propagate so the screen can show its error state (and retry).
    override fun getPerson(id: Int): Flow<Person> = flow {
        emit(network.fetchPerson(id = id).asPerson())
    }
}
