package com.example.c001apk.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.c001apk.R
import com.example.c001apk.logic.model.BrowseHistory
import com.example.c001apk.logic.model.FeedFavorite
import com.example.c001apk.ui.activity.CopyActivity
import com.example.c001apk.ui.activity.FeedActivity
import com.example.c001apk.ui.activity.UserActivity
import com.example.c001apk.util.DateUtils
import com.example.c001apk.util.ImageUtil
import com.example.c001apk.util.IntentUtil
import com.example.c001apk.util.SpannableStringBuilderUtil
import com.example.c001apk.view.LinkTextView
import com.google.android.material.imageview.ShapeableImageView


class BHistoryAdapter(
    private val mContext: Context,
) :
    RecyclerView.Adapter<BHistoryAdapter.HistoryViewHolder>()/*, PopupMenu.OnMenuItemClickListener */ {

    /* private val browseHistoryDao by lazy {
         BrowseHistoryDatabase.getDatabase(mContext).browseHistoryDao()
     }
     private val feedFavoriteDao by lazy {
         FeedFavoriteDatabase.getDatabase(mContext).feedFavoriteDao()
     }

     var popup: PopupMenu? = null*/

    private var dataList: List<Any> = ArrayList()
    private var type = ""
    private var fid = ""

    @SuppressLint("NotifyDataSetChanged")
    fun setDataListData(type: String, dataList: List<Any>) {
        this.type = type
        this.dataList = dataList
        notifyDataSetChanged()
    }

    private var uid = ""
    private var position = -1


    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var id = ""
        var uid = ""
        val avatar: ShapeableImageView = view.findViewById(R.id.avatar)
        val uname: LinkTextView = view.findViewById(R.id.uname)
        val device: TextView = view.findViewById(R.id.device)
        val message: LinkTextView = view.findViewById(R.id.message)
        val pubDate: TextView = view.findViewById(R.id.pubDate)
        val expand: ImageView = view.findViewById(R.id.expand)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_feed, parent, false)
        val viewHolder = HistoryViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            IntentUtil.startActivity<FeedActivity>(parent.context) {
                putExtra("id", viewHolder.id)
            }
        }
        viewHolder.itemView.setOnLongClickListener {
            IntentUtil.startActivity<CopyActivity>(parent.context) {
                putExtra("text", viewHolder.message.text.toString())
            }
            true
        }
        viewHolder.avatar.setOnClickListener {
            IntentUtil.startActivity<UserActivity>(parent.context) {
                putExtra("id", viewHolder.uid)
            }
        }
        /*viewHolder.expand.setOnClickListener {
                    uid = viewHolder.uid
                    fid = viewHolder.id
                    position = viewHolder.bindingAdapterPosition
                    popup = PopupMenu(mContext, it)
                    val inflater = popup?.menuInflater
                    inflater?.inflate(R.menu.feed_history_menu, popup?.menu)
                    popup?.menu?.findItem(R.id.report)?.isVisible = PrefManager.isLogin
                    popup?.setOnMenuItemClickListener(this@BHistoryAdapter)
                    popup?.show()
                }*/
        return viewHolder
    }

    override fun getItemCount() = dataList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        if (dataList.isNotEmpty()) {
            if (type == "browse") {
                val history = dataList[position] as BrowseHistory
                holder.id = history.fid
                holder.uid = history.uid
                val name =
                    """<a class="feed-link-uname" href="/u/${history.uid}">${history.uname}</a>""" + "\u3000"
                SpannableStringBuilderUtil.isColor = true
                holder.uname.text = SpannableStringBuilderUtil.setText(
                    mContext,
                    name,
                    holder.uname.textSize,
                    null
                )
                holder.uname.movementMethod =
                    LinkTextView.LocalLinkMovementMethod.instance
                SpannableStringBuilderUtil.isColor = false
                if (history.device == "")
                    holder.device.visibility = View.GONE
                else {
                    holder.device.visibility = View.VISIBLE
                    holder.device.text = history.device
                }
                holder.pubDate.text = DateUtils.fromToday(history.pubDate.toLong())
                holder.message.movementMethod =
                    LinkTextView.LocalLinkMovementMethod.instance
                holder.message.text = SpannableStringBuilderUtil.setText(
                    mContext,
                    history.message,
                    holder.message.textSize,
                    null
                )
                ImageUtil.showIMG(holder.avatar, history.avatar)
            } else {
                val history = dataList[position] as FeedFavorite
                holder.id = history.feedId
                holder.uid = history.uid
                val name =
                    """<a class="feed-link-uname" href="/u/${history.uid}">${history.uname}</a>""" + "\u3000"
                SpannableStringBuilderUtil.isColor = true
                holder.uname.text = SpannableStringBuilderUtil.setText(
                    mContext,
                    name,
                    holder.uname.textSize,
                    null
                )
                holder.uname.movementMethod =
                    LinkTextView.LocalLinkMovementMethod.instance
                SpannableStringBuilderUtil.isColor = false
                if (history.device == "")
                    holder.device.visibility = View.GONE
                else {
                    holder.device.visibility = View.VISIBLE
                    holder.device.text = history.device
                }
                holder.pubDate.text = DateUtils.fromToday(history.pubDate.toLong())
                holder.message.movementMethod =
                    LinkTextView.LocalLinkMovementMethod.instance
                holder.message.text = SpannableStringBuilderUtil.setText(
                    mContext,
                    history.message,
                    holder.message.textSize,
                    null
                )
                ImageUtil.showIMG(holder.avatar, history.avatar)
            }
        }
    }
}

/*    override fun onMenuItemClick(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.block -> {
                BlackListUtil.saveUid(uid)
                dataList.removeAt(position)
                notifyItemRemoved(position)
            }

            R.id.report -> {
                IntentUtil.startActivity<WebViewActivity>(mContext) {
                    putExtra(
                        "url",
                        "https://m.coolapk.com/mp/do?c=feed&m=report&type=feed&id=$fid"
                    )
                }
            }

            R.id.delete -> {
                dataList.removeAt(position)
                notifyItemRemoved(position)
                CoroutineScope(Dispatchers.IO).launch {
                    if (type == "browse")
                        browseHistoryDao.delete(fid)
                    else
                        feedFavoriteDao.delete(fid)
                }
            }
        }
        popup?.dismiss()
        popup = null
        return true
    }*/
