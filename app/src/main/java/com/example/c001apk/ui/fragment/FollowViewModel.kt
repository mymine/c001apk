package com.example.c001apk.ui.fragment

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.adapter.FooterAdapter
import com.example.c001apk.adapter.ItemListener
import com.example.c001apk.constant.Constants.LOADING_FAILED
import com.example.c001apk.logic.model.HomeFeedResponse
import com.example.c001apk.logic.model.Like
import com.example.c001apk.logic.network.Repository.getDataList
import com.example.c001apk.logic.network.Repository.getFollowList
import com.example.c001apk.util.BlackListUtil
import com.example.c001apk.util.TopicBlackListUtil
import kotlinx.coroutines.launch

class FollowViewModel : ViewModel() {

    val changeState = MutableLiveData<Pair<FooterAdapter.LoadState, String?>>()
    val dataListData = MutableLiveData<List<HomeFeedResponse.Data>>()

    fun fetchFeedList() {
        url = when (type) {
            "feed" -> "/v6/user/feedList?showAnonymous=0&isIncludeTop=1"
            "follow" -> "/v6/user/followList"
            "fans" -> "/v6/user/fansList"
            "apk" -> {
                uid = ""
                "/v6/user/apkFollowList"
            }

            "forum" -> {
                uid = ""
                "/v6/user/forumFollowList"
            }

            "like" -> "/v6/user/likeList"

            "reply" -> "/v6/user/replyList"

            "replyToMe" -> "/v6/user/replyToMeList"

            "recentHistory" -> "/v6/user/recentHistoryList"

            else -> throw IllegalArgumentException("invalid type: $type")
        }
        viewModelScope.launch {
            getFollowList(url.toString(), uid.toString(), page)
                .collect { result ->
                    val dataList = dataListData.value?.toMutableList() ?: ArrayList()
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
                            if (isRefreshing) dataList.clear()
                            if (isRefreshing || isLoadMore) {
                                listSize = dataList.size
                                for (element in feed.data)
                                    if (element.entityType == "feed"
                                        || element.entityType == "contacts"
                                        || element.entityType == "apk"
                                        || element.entityType == "feed_reply"
                                    )
                                        if (!BlackListUtil.checkUid(element.userInfo?.uid.toString())
                                            && !TopicBlackListUtil.checkTopic(
                                                element.tags + element.ttitle
                                            )
                                        )
                                            dataList.add(element)
                            }
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_COMPLETE, null
                                )
                            )
                        } else if (feed.data?.isEmpty() == true) {
                            if (isRefreshing) dataList.clear()
                            changeState.postValue(Pair(FooterAdapter.LoadState.LOADING_END, null))
                            isEnd = true
                        }
                    } else {
                        changeState.postValue(
                            Pair(
                                FooterAdapter.LoadState.LOADING_ERROR, LOADING_FAILED
                            )
                        )
                        isEnd = true
                        result.exceptionOrNull()?.printStackTrace()
                    }
                    dataListData.postValue(dataList)
                }
        }
    }

    fun fetchTopicData() {
        viewModelScope.launch {
            getDataList(url.toString(), title.toString(), null, lastItem, page)
                .collect { result ->
                    val dataList = dataListData.value?.toMutableList() ?: ArrayList()
                    val data = result.getOrNull()
                    if (data != null) {
                        if (!data.message.isNullOrEmpty()) {
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_ERROR, data.message
                                )
                            )
                            return@collect
                        } else if (!data.data.isNullOrEmpty()) {
                            if (isRefreshing)
                                dataList.clear()
                            if (isRefreshing || isLoadMore) {
                                listSize = dataList.size
                                for (element in data.data)
                                    if (element.entityType == "feed"
                                        || element.entityType == "topic"
                                        || element.entityType == "product"
                                        || element.entityType == "user"
                                    )
                                        if (!BlackListUtil.checkUid(element.userInfo?.uid.toString())
                                            && !TopicBlackListUtil.checkTopic(
                                                element.tags + element.ttitle
                                            )
                                        )
                                            dataList.add(element)
                            }
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_COMPLETE, null
                                )
                            )
                        } else if (data.data?.isEmpty() == true) {
                            if (isRefreshing)
                                dataList.clear()
                            changeState.postValue(Pair(FooterAdapter.LoadState.LOADING_END, null))
                            isEnd = true
                        }
                    } else {
                        changeState.postValue(
                            Pair(
                                FooterAdapter.LoadState.LOADING_ERROR, LOADING_FAILED
                            )
                        )
                        isEnd = true
                        result.exceptionOrNull()?.printStackTrace()
                    }
                    dataListData.postValue(dataList)
                }
        }
    }


    var isEnable: Boolean? = null
    val tabList = ArrayList<String>()
    var title: String? = null
    var url: String? = null
    var isInit: Boolean = true
    var type: String? = null
    var listSize: Int = -1
    var listType: String = "lastupdate_desc"
    var page = 1
    var lastItem: String? = null
    var isRefreshing: Boolean = true
    var isLoadMore: Boolean = false
    var isEnd: Boolean = false
    var lastVisibleItemPosition: Int = 0
    var itemCount = 1
    var uid: String? = null
    var avatar: String? = null
    var device: String? = null
    var replyCount: String? = null
    var dateLine: Long? = null
    var feedType: String? = null
    var errorMessage: String? = null
    var firstVisibleItemPosition = 0
    var id: String? = null

    inner class ItemClickListener : ItemListener {

    }

}