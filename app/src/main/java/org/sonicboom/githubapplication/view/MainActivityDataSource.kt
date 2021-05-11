package org.sonicboom.githubapplication.view

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.sonicboom.githubapplication.constant.ApiStatus
import org.sonicboom.githubapplication.model.UserItem
import org.sonicboom.githubapplication.network.ApiService
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MainActivityDataSource(coroutineContext: CoroutineContext, private val query: String) :
    PageKeyedDataSource<Int, UserItem>() {
    private val apiService = ApiService().serviceGithub
    val apiState = MutableLiveData<ApiStatus>()
    private val job = Job()
    private val scope = CoroutineScope(coroutineContext + job)

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, UserItem>
    ) {
        scope.launch {
            try {
                apiState.postValue(ApiStatus.LOADING)
                val response = apiService.getUserSearch(query, FIRST_PAGE, PER_PAGE)
                val responseItem = response.body()!!.items
                when {
                    response.isSuccessful -> {
                        apiState.postValue(ApiStatus.SUCCESS)
                        responseItem?.let {
                            if (responseItem.isEmpty()) {
                                apiState.postValue((ApiStatus.EMPTY))
                            } else {
                                apiState.postValue(ApiStatus.LOADED)
                            }
                            callback.onResult(responseItem, null, FIRST_PAGE + 1)
                        }
                    }
                }
            } catch (e: Exception) {
                apiState.postValue(ApiStatus.FAILED)
                Log.d(TAG, e.localizedMessage!!)
            }

        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserItem>) {
        scope.launch {
            try {
                apiState.postValue(ApiStatus.LOADING)
                val response = apiService.getUserSearch(query, params.key, PER_PAGE)
                val responseItem = response.body()!!.items
                val key = if (params.key > 0) params.key - 1 else -1
                when {
                    response.isSuccessful -> {
                        apiState.postValue(ApiStatus.SUCCESS)
                        responseItem?.let {
                            if (responseItem.isEmpty()) {
                                apiState.postValue((ApiStatus.EMPTY_BEFORE))
                            } else {
                                apiState.postValue(ApiStatus.LOADED)
                            }
                            callback.onResult(responseItem, key)
                        }
                    }
                }
            } catch (e: Exception) {
                apiState.postValue(ApiStatus.FAILED)
                Log.d(TAG, e.localizedMessage!!)
            }

        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserItem>) {
        scope.launch {
            try {
                if (params.key < 9) {
                    apiState.postValue(ApiStatus.LOADING)
                    val response = apiService.getUserSearch(query, params.key, PER_PAGE)
                    val responseItem: List<UserItem>

                    if (response.body()!!.items!!.isNotEmpty()) {
                        responseItem = response.body()!!.items!!
                        val key = params.key + 1
                        when {
                            response.isSuccessful -> {
                                apiState.postValue(ApiStatus.SUCCESS)
                                responseItem.let {
                                    if (responseItem.isEmpty()) {
                                        apiState.postValue(ApiStatus.EMPTY_AFTER)
                                    } else {
                                        apiState.postValue(ApiStatus.LOADED)
                                    }
                                    callback.onResult(responseItem, key)
                                }
                            }
                            else -> {
                                apiState.postValue(ApiStatus.FAILED)
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                apiState.postValue(ApiStatus.FAILED)
                Log.d("ERR_REQ_PRMNTAAN_WD", e.localizedMessage!!)
            }
        }
    }

    companion object {
        const val FIRST_PAGE = 1
        const val PER_PAGE = 30
        const val TAG = "GITHUB_USERS"
    }
}