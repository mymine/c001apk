package com.example.c001apk.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.setPadding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.absinthe.libraries.utils.extensions.dp
import com.example.c001apk.R
import com.example.c001apk.logic.model.TotalReplyResponse
import com.example.c001apk.ui.activity.CopyActivity
import com.example.c001apk.ui.activity.UserActivity
import com.example.c001apk.util.DateUtils
import com.example.c001apk.util.ImageUtil
import com.example.c001apk.util.ImageUtil.getImageLp
import com.example.c001apk.util.IntentUtil
import com.example.c001apk.util.SpannableStringBuilderUtil
import com.example.c001apk.util.Utils.getColorFromAttr
import com.example.c001apk.view.LinkTextView
import com.example.c001apk.view.ninegridimageview.NineGridImageView
import com.google.android.material.imageview.ShapeableImageView


class Reply2ReplyTotalAdapter(
    private val mContext: Context,
    private val fuid: String,
    private val uid: String,
    private val position: Int,
    //  private val replyList: ArrayList<TotalReplyResponse.Data>
) : ListAdapter<TotalReplyResponse.Data, Reply2ReplyTotalAdapter.ReplyViewHolder>(
    Reply2ReplyDiffCallback()
) {


    private var rid = ""
    private var ruid = ""
    private var rposition = -1


    class ReplyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val uname: LinkTextView = view.findViewById(R.id.uname)
        var id = ""
        var uid = ""
        var name = ""
        var isLike = false
        val message: LinkTextView = view.findViewById(R.id.message)
        val pubDate: TextView = view.findViewById(R.id.pubDate)
        val like: TextView = view.findViewById(R.id.like)
        val avatar: ShapeableImageView = view.findViewById(R.id.avatar)
        val reply: TextView = view.findViewById(R.id.reply)
        val multiImage: NineGridImageView = view.findViewById(R.id.multiImage)
        val expand: ImageView = view.findViewById(R.id.expand)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReplyViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reply_to_reply_item, parent, false)
        val viewHolder = ReplyViewHolder(view)
        viewHolder.avatar.setOnClickListener {
            IntentUtil.startActivity<UserActivity>(parent.context) {
                putExtra("id", viewHolder.uid)
            }
        }
        viewHolder.itemView.setOnLongClickListener {
            IntentUtil.startActivity<CopyActivity>(parent.context) {
                putExtra("text", viewHolder.message.text.toString())
            }
            true
        }
        /*viewHolder.itemView.setOnClickListener {
            appListener?.onReply2Reply(
                position,
                viewHolder.bindingAdapterPosition,
                viewHolder.id,
                viewHolder.uid,
                viewHolder.name,
                "reply"
            )
        }
        viewHolder.like.setOnClickListener {
            if (PrefManager.isLogin) {
                if (PrefManager.SZLMID == "") {
                    Toast.makeText(
                        parent.context,
                        parent.context.getString(R.string.szlm_id_should_not_be_null),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    appListener?.onPostLike(
                        null,
                        viewHolder.isLike,
                        viewHolder.id,
                        viewHolder.bindingAdapterPosition
                    )
                }
            }
        }
        viewHolder.multiImage.apply {
            appListener = this@Reply2ReplyTotalAdapter.appListener
        }*/
        /*viewHolder.expand.setOnClickListener {
            rid = viewHolder.id
            ruid = viewHolder.uid
            rposition = viewHolder.bindingAdapterPosition
            popup = PopupMenu(parent.context, it)
            val inflater = popup?.menuInflater
            inflater?.inflate(R.menu.feed_reply_menu, popup?.menu)
            popup?.menu?.findItem(R.id.copy)?.isVisible = false
            popup?.menu?.findItem(R.id.delete)?.isVisible =
                PrefManager.uid == viewHolder.uid
            popup?.menu?.findItem(R.id.report)?.isVisible = PrefManager.isLogin
            popup?.setOnMenuItemClickListener(this@Reply2ReplyTotalAdapter)
            popup?.show()
        }*/
        return viewHolder


    }

    /*override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {

            val viewType = getItemViewType(position)

            if (viewType == 0) {
                if (payloads[0] == "like") {
                    (holder as ReplyViewHolder).like.text = replyList[position].likenum
                    holder.isLike = replyList[position].userAction?.like == 1
                    val drawableLike: Drawable =
                        holder.itemView.context.getDrawable(R.drawable.ic_like)!!
                    drawableLike.setBounds(
                        0,
                        0,
                        holder.like.textSize.toInt(),
                        holder.like.textSize.toInt()
                    )
                    if (replyList[position].userAction?.like == 1) {
                        DrawableCompat.setTint(
                            drawableLike,
                            holder.itemView.context.getColorFromAttr(
                                rikka.preference.simplemenu.R.attr.colorPrimary
                            )
                        )
                        holder.like.setTextColor(
                            holder.itemView.context.getColorFromAttr(
                                rikka.preference.simplemenu.R.attr.colorPrimary
                            )
                        )
                    } else {
                        DrawableCompat.setTint(
                            drawableLike,
                            holder.itemView.context.getColor(android.R.color.darker_gray)
                        )
                        holder.like.setTextColor(holder.itemView.context.getColor(android.R.color.darker_gray))
                    }
                    holder.like.setCompoundDrawables(drawableLike, null, null, null)
                }
            }

        }
    }*/

    override fun onBindViewHolder(holder: ReplyViewHolder, position: Int) {

        val reply = currentList[position]

        holder.itemView.also {
            if (it.layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                if (position == 0) {
                    it.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    it.background =
                        holder.itemView.context.getDrawable(R.drawable.text_card_bg)
                    it.foreground =
                        holder.itemView.context.getDrawable(R.drawable.selector_bg_12_trans)
                    it.setPadding(10.dp)
                }
            } else {
                if (position == 0) {
                    it.setBackgroundColor(Color.TRANSPARENT)
                } else {
                    it.setBackgroundColor(holder.itemView.context.getColor(R.color.home_card_background_color))
                }
                it.foreground =
                    holder.itemView.context.getDrawable(R.drawable.selector_bg_trans)
                it.setPadding(15.dp, 12.dp, 15.dp, 12.dp)
            }
        }

        holder.id = reply.id
        holder.uid = reply.uid
        holder.name = reply.username
        holder.isLike = reply.userAction?.like == 1
        ImageUtil.showIMG(holder.avatar, reply.userAvatar)

        val replyTag =
            when (reply.uid) {
                fuid -> " [楼主] "
                uid -> " [层主] "
                else -> ""
            }

        val rReplyTag =
            when (reply.ruid) {
                fuid -> " [楼主] "
                uid -> " [层主] "
                else -> ""
            }

        val text =
            if (reply.ruid == "0")
                """<a class="feed-link-uname" href="/u/${reply.uid}">${reply.username}$replyTag</a>""" + "\u3000"
            else
                """<a class="feed-link-uname" href="/u/${reply.uid}">${reply.username}$replyTag</a>回复<a class="feed-link-uname" href="/u/${reply.rusername}">${reply.rusername}$rReplyTag</a>""" + "\u3000"


        holder.uname.text = SpannableStringBuilderUtil.setText(
            holder.itemView.context,
            text,
            holder.uname.textSize,
            null
        )
        holder.uname.movementMethod = LinkTextView.LocalLinkMovementMethod.instance

        if (reply.message == "[图片]") {
            holder.message.text = "[图片]"
            holder.message.visibility = View.GONE
        } else {
            holder.message.visibility = View.VISIBLE
            holder.message.movementMethod =
                LinkTextView.LocalLinkMovementMethod.instance
            holder.message.text = SpannableStringBuilderUtil.setText(
                holder.itemView.context,
                reply.message,
                holder.message.textSize,
                null
            )
        }

        holder.pubDate.text = DateUtils.fromToday(reply.dateline)
        val drawable1: Drawable = holder.itemView.context.getDrawable(R.drawable.ic_date)!!
        drawable1.setBounds(
            0,
            0,
            holder.pubDate.textSize.toInt(),
            holder.pubDate.textSize.toInt()
        )
        holder.pubDate.setCompoundDrawables(drawable1, null, null, null)

        val drawableLike: Drawable =
            holder.itemView.context.getDrawable(R.drawable.ic_like)!!
        drawableLike.setBounds(
            0,
            0,
            holder.like.textSize.toInt(),
            holder.like.textSize.toInt()
        )
        if (reply.userAction?.like == 1) {
            DrawableCompat.setTint(
                drawableLike,
                holder.itemView.context.getColorFromAttr(
                    rikka.preference.simplemenu.R.attr.colorPrimary
                )
            )
            holder.like.setTextColor(
                holder.itemView.context.getColorFromAttr(
                    rikka.preference.simplemenu.R.attr.colorPrimary
                )
            )
        } else {
            DrawableCompat.setTint(
                drawableLike,
                holder.itemView.context.getColor(android.R.color.darker_gray)
            )
            holder.like.setTextColor(holder.itemView.context.getColor(android.R.color.darker_gray))
        }
        holder.like.text = reply.likenum
        holder.like.setCompoundDrawables(drawableLike, null, null, null)

        holder.reply.text = reply.replynum
        val drawableReply: Drawable =
            holder.itemView.context.getDrawable(R.drawable.ic_message)!!
        drawableReply.setBounds(
            0,
            0,
            holder.like.textSize.toInt(),
            holder.like.textSize.toInt()
        )
        holder.reply.setCompoundDrawables(drawableReply, null, null, null)

        if (!reply.picArr.isNullOrEmpty()) {
            holder.multiImage.visibility = View.VISIBLE
            if (reply.picArr.size == 1) {
                val imageLp = getImageLp(reply.pic)
                holder.multiImage.imgWidth = imageLp.first
                holder.multiImage.imgHeight = imageLp.second
            }
            holder.multiImage.apply {
                val urlList: MutableList<String> = ArrayList()
                for (element in reply.picArr)
                    urlList.add("$element.s.jpg")
                setUrlList(urlList)
            }
        } else {
            holder.multiImage.visibility = View.GONE
        }
    }

}

/*    override fun onMenuItemClick(p0: MenuItem): Boolean {
        when (p0.itemId) {
            R.id.block -> {
                BlackListUtil.saveUid(ruid)
                replyList.removeAt(rposition)
                notifyItemRemoved(rposition)
            }

            R.id.report -> {
                IntentUtil.startActivity<WebViewActivity>(mContext) {
                    putExtra(
                        "url",
                        "https://m.coolapk.com/mp/do?c=feed&m=report&type=feed_reply&id=$rid"
                    )
                }
            }

            R.id.delete -> {
                appListener?.onDeleteFeedReply(rid, rposition, null)
            }

            R.id.show -> {
                appListener?.onShowTotalReply(rposition, ruid, rid, null)
            }
        }
        popup?.dismiss()
        popup = null
        return true
    }*/



class Reply2ReplyDiffCallback : DiffUtil.ItemCallback<TotalReplyResponse.Data>() {
    override fun areItemsTheSame(
        oldItem: TotalReplyResponse.Data,
        newItem: TotalReplyResponse.Data
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: TotalReplyResponse.Data,
        newItem: TotalReplyResponse.Data
    ): Boolean {
        return oldItem.likenum == newItem.likenum
    }
}