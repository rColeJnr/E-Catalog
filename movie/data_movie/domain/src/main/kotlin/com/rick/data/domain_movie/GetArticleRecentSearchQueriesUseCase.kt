/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rick.data.domain_movie

import com.rick.data.model_movie.ArticleRecentSearchQuery
import com.rick.movie.data_movie.data.repository.article.ArticleRecentSearchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * A use case which returns the recent search queries.
 */
class GetArticleRecentSearchQueriesUseCase @Inject constructor(
    private val recentSearchRepository: ArticleRecentSearchRepository,
) {
    operator fun invoke(limit: Int = 10): Flow<List<ArticleRecentSearchQuery>> =
        recentSearchRepository.getArticleRecentSearchQueries(limit)
}
