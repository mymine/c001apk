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
import com.example.c001apk.logic.network.Repository.getDyhDetail
import com.example.c001apk.util.BlackListUtil
import com.example.c001apk.util.TopicBlackListUtil
import kotlinx.coroutines.launch

class DyhViewModel : ViewModel() {
    fun fetchDyhDetail() {
        viewModelScope.launch {
            getDyhDetail(id.toString(), type.toString(), page)
                .collect{result->
                    val dyhDataList = dataListData.value?.toMutableList() ?: ArrayList()
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
                                dyhDataList.clear()
                            if (isRefreshing || isLoadMore) {
                                listSize = dyhDataList.size
                                for (element in data.data)
                                    if (element.entityType == "feed")
                                        if (!BlackListUtil.checkUid(element.userInfo?.uid.toString())
                                            && !TopicBlackListUtil.checkTopic(
                                                element.tags + element.ttitle
                                            )
                                        )
                                            dyhDataList.add(element)
                            }
                            changeState.postValue(
                                Pair(
                                    FooterAdapter.LoadState.LOADING_COMPLETE, null
                                )
                            )
                        } else if (data.data?.isEmpty() == true) {
                            if (isRefreshing) dyhDataList.clear()
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
                    dataListData.postValue(dyhDataList)
                }
        }
    }

    val changeState = MutableLiveData<Pair<FooterAdapter.LoadState, String?>>()
    val dataListData = MutableLiveData<List<HomeFeedResponse.Data>>()


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