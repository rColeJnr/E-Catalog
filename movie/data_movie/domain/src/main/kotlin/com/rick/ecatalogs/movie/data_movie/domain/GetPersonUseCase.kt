package com.rick.ecatalogs.movie.data_movie.domain

import com.rick.ecatalogs.movie.data_movie.data.repository.person.PersonRepository
import com.rick.ecatalogs.movie.data_movie.model.tmdb.person.Person
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** An actor's details and best-known movies and series. */
class GetPersonUseCase @Inject constructor(
    private val personRepository: PersonRepository,
) {
    operator fun invoke(id: Int): Flow<Person> = personRepository.getPerson(id)
}
