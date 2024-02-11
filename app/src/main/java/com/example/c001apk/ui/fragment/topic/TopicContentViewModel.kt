package com.example.c001apk.ui.fragment.topic

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
import com.example.c001apk.util.BlackListUtil
import com.example.c001apk.util.TopicBlackListUtil
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class TopicContentViewModel : ViewModel() {

    var url: String? = null
    var title: String? = null
    var isEnable: Boolean? = null
    val changeState = MutableLiveData<Pair<FooterAdapter.LoadState, String?>>()
    val topicData = MutableLiveData<List<HomeFeedResponse.Data>>()

    fun fetchTopicData() {
        viewModelScope.launch {
            getDataList(url.toString(), title.toString(), "", lastItem, page)
                .onStart {
                    if (isLoadMore)
                        changeState.postValue(Pair(FooterAdapter.LoadState.LOADING, null))
                }
                .collect { result ->
                    val topicDataList = topicData.value?.toMutableList() ?: ArrayList()
                    val data = result.getOrNull()
                    if (data != null) {
                        if (!data.message.isNullOrEmpty()) {
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_ERROR,
                                    data.message
                                )
                            )
                            return@collect
                        } else if (!data.data.isNullOrEmpty()) {
                            if (isRefreshing)
                                topicDataList.clear()
                            if (isRefreshing || isLoadMore) {
                                lastItem = data.data.last().id
                                listSize = topicDataList.size
                                for (element in data.data) {
                                    if (element.id == lastItem)
                                        continue
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
                                            topicDataList.add(element)
                                }
                            }
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_COMPLETE,
                                    null
                                )
                            )
                        } else if (data.data?.isEmpty() == true) {
                            if (isRefreshing)
                                topicDataList.clear()
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
                    topicData.postValue(topicDataList)
                }
        }

    }

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