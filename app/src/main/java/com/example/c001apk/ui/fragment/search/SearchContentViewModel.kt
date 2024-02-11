package com.example.c001apk.ui.fragment.search

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.adapter.FooterAdapter
import com.example.c001apk.adapter.ItemListener
import com.example.c001apk.constant.Constants.LOADING_FAILED
import com.example.c001apk.logic.model.HomeFeedResponse
import com.example.c001apk.logic.model.Like
import com.example.c001apk.logic.network.Repository.getSearch
import com.example.c001apk.util.BlackListUtil
import com.example.c001apk.util.TopicBlackListUtil
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class SearchContentViewModel : ViewModel() {

    var tabList: MutableList<String>? = null
    var title: String? = null
    var feedType: String = "all"
    var sort: String = "default" //hot // reply
    var pageParam: String? = null
    var pageType: String? = null
    var keyWord: String? = null
    val changeState = MutableLiveData<Pair<FooterAdapter.LoadState, String?>>()
    val searchData = MutableLiveData<List<HomeFeedResponse.Data>>()

    fun fetchSearchData() {
        viewModelScope.launch {
            getSearch(
                type.toString(), feedType, sort, keyWord.toString(),
                pageType.toString(), pageParam.toString(), page,
                -1
            )
                .onStart {
                    if (isLoadMore)
                        changeState.postValue(Pair(FooterAdapter.LoadState.LOADING, null))
                }
                .collect { result ->
                    val searchList = searchData.value?.toMutableList() ?: ArrayList()
                    val search = result.getOrNull()
                    if (search != null) {
                        if (!search.message.isNullOrEmpty()) {
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_ERROR, search.message
                                )
                            )
                            return@collect
                        } else if (!search.data.isNullOrEmpty()) {
                            if (isRefreshing)
                                searchList.clear()
                            if (isRefreshing || isLoadMore) {
                                listSize = searchList.size
                                if (type == "feed")
                                    for (element in search.data) {
                                        if (element.entityType == "feed")
                                            if (!BlackListUtil.checkUid(element.userInfo?.uid.toString())
                                                && !TopicBlackListUtil.checkTopic(
                                                    element.tags + element.ttitle
                                                )
                                            )
                                                searchList.add(element)
                                    }
                                else
                                    searchList.addAll(search.data)
                            }
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_COMPLETE,
                                    null
                                )
                            )
                        } else {
                            if (isRefreshing)
                                searchList.clear()
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
                    searchData.postValue(searchList)
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
    var errorMessage: String? = null
    var firstVisibleItemPosition = 0
    var id: String? = null

    inner class ItemClickListener : ItemListener {

    }

}