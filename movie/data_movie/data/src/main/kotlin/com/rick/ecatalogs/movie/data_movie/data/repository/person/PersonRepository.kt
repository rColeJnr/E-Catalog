package com.rick.ecatalogs.movie.data_movie.data.repository.person

import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.Person
import kotlinx.coroutines.flow.Flow

interface PersonRepository {
    /** The actor with their best-known movies and series. Errors are thrown into the flow. */
    fun getPerson(id: Int): Flow<Person>
}
