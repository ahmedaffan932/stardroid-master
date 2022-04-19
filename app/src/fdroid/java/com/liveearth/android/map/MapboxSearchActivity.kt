package com.liveearth.android.map

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.search.*
import com.mapbox.search.result.SearchResult

@SuppressLint("LogNotTimber")
class MapboxSearchActivity : AppCompatActivity() {

    private lateinit var categorySearchEngine: CategorySearchEngine
    private lateinit var searchRequestTask: SearchRequestTask

    private val searchCallback: SearchCallback = object : SearchCallback {

        override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
            if (results.isEmpty()) {
                Log.i("SearchApiExample", "No category search results")
            } else {
                Log.i("SearchApiExample", "Category search results: $results")
            }
        }

        override fun onError(e: Exception) {
            Log.i("SearchApiExample", "Search error", e)
        }
    }

   override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)

        categorySearchEngine = MapboxSearchSdk.createCategorySearchEngine()

        searchRequestTask = categorySearchEngine.search(
            "cafe",
            CategorySearchOptions(limit = 1),
            searchCallback
        )
    }

    override fun onDestroy() {
        searchRequestTask.cancel()
        super.onDestroy()
    }
}
