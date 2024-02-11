package com.example.c001apk.ui.activity

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.c001apk.logic.database.BrowseHistoryDatabase
import com.example.c001apk.logic.database.FeedFavoriteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    var listSize: Int = -1
    var type: String? = null

    val browseLiveData: MutableLiveData<List<Any>> = MutableLiveData()

    fun getBrowseList(type: String, context: Context) {
        val newList = ArrayList<Any>()
        viewModelScope.launch(Dispatchers.IO) {
            if (type == "browse") {
                val browseHistoryDao = BrowseHistoryDatabase.getDatabase(context).browseHistoryDao()
                newList.addAll(browseHistoryDao.loadAllHistory())
            } else {
                val feedFavoriteDao = FeedFavoriteDatabase.getDatabase(context).feedFavoriteDao()
                newList.addAll(feedFavoriteDao.loadAllHistory())
            }
            browseLiveData.postValue(newList)
        }
    }
}