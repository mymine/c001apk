package com.example.c001apk.ui.fragment.feed

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.adapter.FooterAdapter
import com.example.c001apk.adapter.ItemListener
import com.example.c001apk.constant.Constants.LOADING_FAILED
import com.example.c001apk.logic.model.Like
import com.example.c001apk.logic.model.TotalReplyResponse
import com.example.c001apk.logic.network.Repository
import com.example.c001apk.logic.network.Repository.getReply2Reply
import com.example.c001apk.logic.network.Repository.postReply
import com.example.c001apk.util.BlackListUtil
import kotlinx.coroutines.launch

class Reply2ReplyBottomSheetViewModel : ViewModel() {

    val uname: String? = null
    val ruid: String? = null
    val rid: String? = null
    var position: Int? = null
    var fuid: String? = null
    var listSize: Int = -1
    var listType: String = "lastupdate_desc"
    var page = 1
    var lastItem: String? = null
    var isInit: Boolean = true
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

    val changeState = MutableLiveData<Pair<FooterAdapter.LoadState, String?>>()
    val totalReplyData = MutableLiveData<List<TotalReplyResponse.Data>>()
    var oriReply: ArrayList<TotalReplyResponse.Data> = ArrayList()

    fun fetchReplyTotal() {
        viewModelScope.launch {
            getReply2Reply(id.toString(), page)
                .collect { result ->
                    val replyTotalList = totalReplyData.value?.toMutableList() ?: ArrayList()
                    val reply = result.getOrNull()
                    if (reply?.message != null) {
                        changeState.postValue(
                            Pair(
                                FooterAdapter.LoadState.LOADING_ERROR,
                                reply.message
                            )
                        )
                        return@collect
                    } else if (!reply?.data.isNullOrEmpty()) {
                        if (!isLoadMore) {
                            replyTotalList.clear()
                            replyTotalList.addAll(oriReply)
                        }
                        listSize = replyTotalList.size
                        for (element in reply?.data!!)
                            if (element.entityType == "feed_reply")
                                if (!BlackListUtil.checkUid(element.uid))
                                    replyTotalList.add(element)
                        changeState.postValue(Pair(FooterAdapter.LoadState.LOADING_COMPLETE, null))
                    } else if (reply?.data?.isEmpty() == true) {
                        if (replyTotalList.isEmpty())
                            replyTotalList.addAll(oriReply)
                        changeState.postValue(Pair(FooterAdapter.LoadState.LOADING_END, null))
                        isEnd = true
                        result.exceptionOrNull()?.printStackTrace()
                    } else {
                        if (replyTotalList.isEmpty())
                            replyTotalList.addAll(oriReply)
                        changeState.postValue(
                            Pair(
                                FooterAdapter.LoadState.LOADING_ERROR, LOADING_FAILED
                            )
                        )
                        isEnd = true
                        result.exceptionOrNull()?.printStackTrace()
                    }
                    totalReplyData.postValue(replyTotalList)
                }
        }

    }

    var replyData = HashMap<String, String>()
    fun onPostReply() {
      //postReply(replyData, id.toString(), type)
    }




    inner class ItemClickListener : ItemListener {

    }

}