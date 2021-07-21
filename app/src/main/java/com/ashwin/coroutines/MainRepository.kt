package com.ashwin.coroutines

import android.util.Log
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainRepository(context: CoroutineContext) {
    private val ioScope = CoroutineScope(context + SupervisorJob())

    suspend fun cacheAndGet(key: String, request: suspend (key: String) -> String): String {
        val data = fromCache(key)
        return if (data != null) {
            data
        } else {
            ioScope.async {
                Log.d("coroutines-sandbox", "$this: MainRepository: ioScope")
                request(key).also {
                    delay(5000)
                    toCache(key, it)
                }
            }.await()
        }
    }

    private fun fromCache(key: String): String? {
        Log.d("coroutines-sandbox", "MainRepository: Getting from cache: $key")
        return null
    }

    private fun toCache(key: String, data: String) {
        Log.d("coroutines-sandbox", "MainRepository: Saving to cache: $key = $data")
    }
}
