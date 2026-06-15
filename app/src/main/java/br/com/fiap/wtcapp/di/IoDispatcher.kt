package br.com.fiap.wtcapp.di

import javax.inject.Qualifier

/** Qualifier so the IO [kotlinx.coroutines.CoroutineDispatcher] can be swapped in tests. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher
