package com.example.c001apk.ui.activity

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.adapter.Event
import com.example.c001apk.adapter.FooterAdapter
import com.example.c001apk.adapter.ItemListener
import com.example.c001apk.logic.model.HomeFeedResponse
import com.example.c001apk.logic.model.Like
import com.example.c001apk.logic.model.TopicBean
import com.example.c001apk.logic.network.Repository.getDataList
import com.example.c001apk.util.BlackListUtil
import com.example.c001apk.util.TopicBlackListUtil
import kotlinx.coroutines.launch

class CarouselViewModel : ViewModel() {

    var barTitle: String? = null
    var isResume: Boolean = true
    val tabList = ArrayList<String>()
    var url: String? = null
    var type: String? = null
    var isFollow: Boolean? = null
    var fid: String? = null
    var title: String? = null
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
    var avatar: String? = null
    var cover: String? = null
    var level: String? = null
    var like: String? = null
    var follow: String? = null
    var fans: String? = null
    var packageName: String? = null


    val changeState = MutableLiveData<Pair<FooterAdapter.LoadState, String?>>()
    val carouselData = MutableLiveData<List<HomeFeedResponse.Data>>()
    val doNext = MutableLiveData<Event<Boolean>>()
    val topicList: MutableList<TopicBean> = ArrayList()
    val initBar = MutableLiveData<Event<Boolean>>()
    val showView = MutableLiveData<Event<Boolean>>()
    val initView = MutableLiveData<Event<Boolean>>()
    val initRvView = MutableLiveData<Event<Boolean>>()
    val error = MutableLiveData<Event<Boolean>>()
    val finish = MutableLiveData<Event<Boolean>>()


    fun fetchCarouselList() {
        viewModelScope.launch {
            getDataList(url.toString(), title.toString(), null, null, page)
                .collect { result ->
                    val carouselList = carouselData.value?.toMutableList() ?: ArrayList()
                    val response = result.getOrNull()
                    if (!response?.data.isNullOrEmpty()) {
                        if (isInit) {
                            isInit = false

                            barTitle =
                                if (response?.data!![response.data.size - 1].extraDataArr == null)
                                    title
                                else
                                    response.data[response.data.size - 1].extraDataArr?.pageTitle.toString()
                            initBar.postValue(Event(true))

                            var index = 0
                            var isIconTabLinkGridCard = false
                            for (element in response.data) {
                                if (element.entityTemplate == "iconTabLinkGridCard") {
                                    showView.postValue(Event(true))
                                    isIconTabLinkGridCard = true
                                    break
                                } else index++
                            }

                            if (isIconTabLinkGridCard) {
                                if (!response.data[index].entities.isNullOrEmpty()) {
                                    response.data[index].entities?.forEach {
                                        tabList.add(it.title)
                                        topicList.add(TopicBean(it.url, it.title))
                                        initView.postValue(Event(true))
                                    }
                                }

                            } else {
                                initRvView.postValue(Event(true))
                                for (element in response.data)
                                    if (element.entityType == "feed")
                                        if (!BlackListUtil.checkUid(element.userInfo?.uid.toString())
                                            && !TopicBlackListUtil.checkTopic(
                                                element.tags + element.ttitle
                                            )
                                        )
                                            carouselList.add(element)
                                carouselData.postValue(carouselList)
                            }
                        } else {
                            if (isRefreshing)
                                carouselList.clear()
                            if (isRefreshing || isLoadMore) {
                                listSize = carouselList.size
                                for (element in response?.data!!)
                                    if (element.entityType == "feed")
                                        if (!BlackListUtil.checkUid(element.userInfo?.uid.toString())
                                            && !TopicBlackListUtil.checkTopic(
                                                element.tags + element.ttitle
                                            )
                                        )
                                            carouselList.add(element)
                            }
                            carouselData.postValue(carouselList)
                        }
                    } else if (response?.data?.isEmpty() == true) {
                        if (isRefreshing)
                            carouselList.clear()
                        changeState.postValue(Pair(FooterAdapter.LoadState.LOADING_END, null))
                        isEnd = true
                    } else {
                        error.postValue(Event(true))
                        isEnd = true
                        result.exceptionOrNull()?.printStackTrace()
                    }
                    finish.postValue(Event(true))
                    isLoadMore = false
                    isRefreshing = false
                }
        }
    }

    inner class ItemClickListener : ItemListener {

    }

}