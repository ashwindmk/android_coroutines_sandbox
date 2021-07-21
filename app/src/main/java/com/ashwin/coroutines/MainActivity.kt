package com.ashwin.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*

const val TAG = "coroutines-sandbox"

class MainActivity : AppCompatActivity() {
//    private val uiScope = MainScope()  // Instead, we will use lifecycleScope

    private lateinit var resultTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        resultTextView = findViewById(R.id.result_text_view)

        val testButton = findViewById<Button>(R.id.test_button)
        testButton.setOnClickListener {
            coroutineBuilderTest()
//            coroutineScopeTest()
        }
    }

    private fun coroutineBuilderTest() {
        Log.d(TAG, "coroutineTest: start [${Thread.currentThread().name}]")
        lifecycleScope.launch {
            Log.d(TAG, "lifecycleScope.launch: start [${Thread.currentThread().name}]")

            // synchronous block, change thread, return result
//            val res = withContext(Dispatchers.IO) {
//                Log.d(TAG, "withContext: start [${Thread.currentThread().name}]")
//                delay(5000)
//                Log.d(TAG, "withContext: end [${Thread.currentThread().name}]")
//                "xyz"
//            }

            // synchronous, change thread, return result
            val res = testSuspendWithContext()

            // synchronous block, return result
//            val res = coroutineScope {
//                Log.d(TAG, "coroutineScope: start [${Thread.currentThread().name}]")
//                delay(5000)
//                Log.d(TAG, "coroutineScope: end [${Thread.currentThread().name}]")
//                "xyz"
//            }

            // asynchronous block, change thread
//            val job = launch(Dispatchers.IO) {
//                Log.d(TAG, "launch: start [${Thread.currentThread().name}]")
//                delay(5000)
//                Log.d(TAG, "launch: end [${Thread.currentThread().name}]")
//            }

            // asynchronous, change thread
//            testSuspendLaunch()

            // asynchronous block, return result (now/later), change thread
//            val deferredJob = async(Dispatchers.IO, start = CoroutineStart.LAZY) {
//                Log.d(TAG, "async: start [${Thread.currentThread().name}]")
//                delay(5000)
//                Log.d(TAG, "async: end [${Thread.currentThread().name}]")
//                "xyz"
//            }  //.await()

            Log.d(TAG, "lifecycleScope.launch: end [${Thread.currentThread().name}]")
        }
        Log.d(TAG, "coroutineTest: end [${Thread.currentThread().name}]")
    }

    private fun coroutineScopeTest() {
        lifecycleScope.launchWhenStarted {
            Log.d(TAG, "$this: start [${Thread.currentThread().name}]")

            // This will end with the lifecycleScope
//            val result = networkCall2("key-2")

            val bgScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
            var result: String = bgScope.async {
                // This will not end with lifecycleScope
                networkCall3("key1")
                // Save result to cache
            }.await()

            // This will not end with lifecycleScope
//            val mainRepository = MainRepository(Dispatchers.IO)
//            val result = mainRepository.cacheAndGet("key-1") {
//                Log.d(TAG, "$this: Making network call for $it... [${Thread.currentThread().name}]")
//                networkCall(it)
//            }

            Log.d(TAG, "$this: end result: $result [${Thread.currentThread().name}]")
            resultTextView.text = result
        }
    }

    // This executed on a different scope
    suspend fun networkCall(key: String): String {
        delay(10_000)
        Log.d(TAG, "networkCall: [${Thread.currentThread().name}]")
        return "$key-result-1"
    }

    // This will be cancelled when the parent scope is cancelled
    suspend fun networkCall2(key: String): String = withContext(Dispatchers.IO) {
        delay(10_000L)
        Log.d(TAG, "$this: networkCall2 [${Thread.currentThread().name}]")
        "$key-result-2"
    }

    // This will be cancelled when the parent scope is cancelled
    suspend fun networkCall3(key: String): String {
        delay(10_000L)
        Log.d(TAG, "$this: networkCall3 [${Thread.currentThread().name}]")
        return "$key-result-3"
    }

    suspend fun testSuspendLaunch() {
        GlobalScope.launch {
            Log.d(TAG, "suspend launch: start [${Thread.currentThread().name}]")
            delay(5000)
            Log.d(TAG, "suspend launch: end [${Thread.currentThread().name}]")
        }
    }

    suspend fun testSuspendWithContext(): String {
        return withContext(Dispatchers.IO) {
            Log.d(TAG, "suspend withContext: start [${Thread.currentThread().name}]")
            delay(5000)
            Log.d(TAG, "suspend withContext: end [${Thread.currentThread().name}]")
            "xyz"
        }
    }
}
