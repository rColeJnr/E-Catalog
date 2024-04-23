package com.rick.core

import javax.inject.Qualifier
import kotlin.annotation.AnnotationRetention.RUNTIME

@Qualifier
@Retention(RUNTIME)
annotation class Dispatcher(val niaDispatcher: EcsDispatchers)

enum class EcsDispatchers {
    Default,
    IO,
}
