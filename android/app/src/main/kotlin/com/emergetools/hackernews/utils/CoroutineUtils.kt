package com.emergetools.hackernews.utils

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun <T, R> Sequence<T>.mapAsync(transform: suspend (T) -> R): List<Deferred<R>> {
  return map {
    GlobalScope.async { transform(it) }
  }.toList()
}

fun <T, R> Iterable<T>.mapAsync(transform: suspend (T) -> R): List<Deferred<R>> {
  return map {
    GlobalScope.async { transform(it) }
  }
}

fun <T> Iterable<T>.forEachInParallel(block: suspend (T) -> Unit) {
  mapAsync(block).awaitAllBlocking()
}

/**
 * Helper to synchronously wait for all deferred items to complete.
 */
fun <T> Sequence<Deferred<T>>.awaitAllBlocking(): List<T> {
  return runBlocking { toList().awaitAll() }
}

/**
 * Helper to synchronously wait for all deferred items to complete.
 */
fun <T> List<Deferred<T>>.awaitAllBlocking(): List<T> {
  return runBlocking { awaitAll() }
}
