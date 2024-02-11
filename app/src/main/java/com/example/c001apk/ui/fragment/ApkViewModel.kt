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
import com.example.c001apk.util.BlackListUtil
import com.example.c001apk.util.TopicBlackListUtil
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ApkViewModel : ViewModel() {

    val changeState = MutableLiveData<Pair<FooterAdapter.LoadState, String?>>()
    val appCommentData = MutableLiveData<List<HomeFeedResponse.Data>>()

    private val commentBaseUrl = "/page?url=/feed/apkCommentList?id="
    fun fetchAppComment() {
        viewModelScope.launch {
            getDataList(
                commentBaseUrl + appId + appCommentSort, appCommentTitle, null, lastItem, page
            )
                .onStart {
                    if (isLoadMore)
                        changeState.postValue(Pair(FooterAdapter.LoadState.LOADING, null))
                }
                .collect { result ->
                    val appCommentList = appCommentData.value?.toMutableList() ?: ArrayList()
                    val comment = result.getOrNull()
                    if (!comment?.data.isNullOrEmpty()) {
                        if (isRefreshing)
                            appCommentList.clear()
                        if (isRefreshing || isLoadMore) {
                            comment?.data!!.forEach {
                                if (it.entityType == "feed")
                                    if (!BlackListUtil.checkUid(
                                            it.userInfo?.uid.toString()
                                        ) && !TopicBlackListUtil.checkTopic(
                                            it.tags + it.ttitle
                                        )
                                    )
                                        appCommentList.add(it)
                            }
                        }
                        changeState.postValue(Pair(FooterAdapter.LoadState.LOADING_COMPLETE, null))
                    } else if (comment?.data?.isEmpty() == true) {
                        if (isRefreshing) appCommentList.clear()
                        changeState.postValue(Pair(FooterAdapter.LoadState.LOADING_END, null))
                        isEnd = true
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
                    appCommentData.postValue(appCommentList)
                }
        }

    }

    var isInit: Boolean = true
    var type: String? = null
    var appCommentTitle = "最近回复"
    var appCommentSort: String? = null
    var appId: String? = null
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