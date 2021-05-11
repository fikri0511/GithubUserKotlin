package org.sonicboom.githubapplication.view

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.DataSource
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.sonicboom.githubapplication.constant.ApiStatus
import org.sonicboom.githubapplication.model.ResponseDetailUser
import org.sonicboom.githubapplication.model.UserItem
import org.sonicboom.githubapplication.network.ApiService
import java.lang.Exception

class MainActivityViewModel:ViewModel() {

    //START GETLIST SEARCH USER

    //Config
    private val config = PagedList.Config.Builder()
        .setPageSize(MainActivityDataSource.PER_PAGE)
        .setEnablePlaceholders(false)
        .build()

    //List Github Users
    private lateinit var listGithubUsers: LiveData<PagedList<UserItem>>
    lateinit var statusGithubUsers: LiveData<ApiStatus>
    private val _listGithubUsersDataSource = MutableLiveData<MainActivityDataSource>()

    //SETUP LIST USERS SEARCH

    private fun initializedGithubUsersPAgedListBuilder(
        config: PagedList.Config,
        query: String
    ): LivePagedListBuilder<Int, UserItem> {
        val dataSourceFactory = object : DataSource.Factory<Int, UserItem>() {
            override fun create(): DataSource<Int, UserItem> {
                val usersSearchDataSource = MainActivityDataSource(Dispatchers.IO,query)
                _listGithubUsersDataSource.postValue(usersSearchDataSource)
                return usersSearchDataSource
            }
        }
        return LivePagedListBuilder(dataSourceFactory, config)
    }

    fun setUpListUsers(query: String) {
        statusGithubUsers = Transformations.switchMap(
            _listGithubUsersDataSource, MainActivityDataSource::apiState
        )
        listGithubUsers = initializedGithubUsersPAgedListBuilder(config,query).build()
    }

    fun getDataListGithubUsers(): LiveData<PagedList<UserItem>> = listGithubUsers

    //END GETLIST SEARCH USER


    //START GET DETAIL USER
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    private val _detailUser = MutableLiveData<ResponseDetailUser>()
    val detailUser: LiveData<ResponseDetailUser>
        get() = _detailUser

    private val apiServiceProduct = ApiService().serviceGithub

    private suspend fun requestDetailUser(user:String){
        try {
            _status.postValue(ApiStatus.LOADING)
            _detailUser.postValue(apiServiceProduct.getDetailUser(user))
            Log.d("DATA_DETAIL", apiServiceProduct.getDetailUser(user).toString())

            _status.postValue(ApiStatus.SUCCESS)
        }catch (e:Exception){
            Log.d("DETAIL_USER", e.localizedMessage!!)
            _status.postValue(ApiStatus.FAILED)
        }
    }

    fun getDetailUser(user:String){
        viewModelScope.launch {
            requestDetailUser(user)
        }
    }
    //END GET DETAIL USER





}