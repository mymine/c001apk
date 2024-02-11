package com.example.c001apk.ui.activity

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.adapter.Event
import com.example.c001apk.adapter.FooterAdapter
import com.example.c001apk.adapter.ItemListener
import com.example.c001apk.constant.Constants.LOADING_FAILED
import com.example.c001apk.logic.model.HomeFeedResponse
import com.example.c001apk.logic.model.Like
import com.example.c001apk.logic.model.UserProfileResponse
import com.example.c001apk.logic.network.Repository
import com.example.c001apk.logic.network.Repository.getUserFeed
import com.example.c001apk.logic.network.Repository.getUserSpace
import com.example.c001apk.util.BlackListUtil
import com.example.c001apk.util.TopicBlackListUtil
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    var url: String? = null
    var isInit: Boolean = true
    var uid: String? = null
    var errorMessage: String? = null
    var uname: String? = null
    var lastVisibleItemPosition: Int = 0
    var isRefreshing: Boolean = true
    var isLoadMore: Boolean = false
    var isEnd: Boolean = false
    var firstVisibleItemPosition = 0
    var page = 1
    var listSize: Int = -1
    var followType: Boolean = false
    var avatar: String? = null
    var cover: String? = null
    var level: String? = null
    var like: String? = null
    var follow: String? = null
    var fans: String? = null

    val showError = MutableLiveData<Event<Boolean>>()
    val showUser = MutableLiveData<Event<Boolean>>()
    val changeState = MutableLiveData<Pair<FooterAdapter.LoadState, String?>>()
    val feedData = MutableLiveData<List<HomeFeedResponse.Data>>()
    var userData: UserProfileResponse.Data? = null
    var afterFollow = MutableLiveData<Event<Boolean>>()

    fun fetchUser() {
        viewModelScope.launch {
            getUserSpace(uid.toString())
                .collect { result ->
                    val user = result.getOrNull()
                    if (user?.message != null) {
                        errorMessage = user.message
                        changeState.postValue(
                            Pair(
                                FooterAdapter.LoadState.LOADING_ERROR, errorMessage
                            )
                        )
                        return@collect
                    } else if (user?.data != null) {
                        followType = user.data.isFollow == 1
                        uname = user.data.username
                        userData = user.data
                        isRefreshing = true
                        showUser.postValue(Event(true))
                        fetchUserFeed()
                    } else {
                        uid = null
                        showError.postValue(Event(false))
                        result.exceptionOrNull()?.printStackTrace()
                    }
                }
        }
    }

    fun fetchUserFeed() {
        viewModelScope.launch {
            getUserFeed(uid.toString(), page)
                .collect { result ->
                    val feedList = feedData.value?.toMutableList() ?: ArrayList()
                    val feed = result.getOrNull()
                    if (feed != null) {
                        if (!feed.message.isNullOrEmpty()) {
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_ERROR, feed.message
                                )
                            )
                            return@collect
                        } else if (!feed.data.isNullOrEmpty()) {
                            if (isRefreshing) feedList.clear()
                            if (isRefreshing || isLoadMore) {
                                listSize = feedList.size
                                feed.data.forEach {
                                    if (it.entityType == "feed")
                                        if (!BlackListUtil.checkUid(it.userInfo?.uid.toString())
                                            && !TopicBlackListUtil.checkTopic(
                                                it.tags + it.ttitle
                                            )
                                        )
                                            feedList.add(it)
                                }
                            }
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_COMPLETE, null
                                )
                            )
                        } else if (feed.data?.isEmpty() == true) {
                            if (isRefreshing) feedList.clear()
                            changeState.postValue(Pair(FooterAdapter.LoadState.LOADING_END, null))
                            isEnd = true
                        }
                    } else {
                        changeState.postValue(
                            Pair(
                                FooterAdapter.LoadState.LOADING_ERROR,
                                LOADING_FAILED
                            )
                        )
                        isEnd = true
                        result.exceptionOrNull()?.printStackTrace()
                    }
                    feedData.postValue(feedList)
                }
        }
    }

    fun onPostFollowUnFollow() {
        viewModelScope.launch {
            Repository.postFollowUnFollow(url.toString(), uid.toString())
                .collect{result->
                    val response = result.getOrNull()
                    if (response != null) {
                        followType= !followType
                        afterFollow.postValue(Event(true))
                    } else {
                        result.exceptionOrNull()?.printStackTrace()
                    }
                }
        }
    }


    inner class ItemClickListener : ItemListener {

    }

}