package com.example.c001apk.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.c001apk.R
import com.example.c001apk.databinding.ItemMessageContentBinding
import com.example.c001apk.databinding.ItemSearchUserBinding
import com.example.c001apk.logic.model.MessageResponse
import com.example.c001apk.util.DateUtils
import com.example.c001apk.util.ImageUtil
import com.example.c001apk.util.ImageUtil.getImageLp
import com.example.c001apk.util.SpannableStringBuilderUtil
import com.example.c001apk.util.Utils.getColorFromAttr
import com.example.c001apk.view.LinkTextView


class MessageContentAdapter(
    private val type: String
) : ListAdapter<MessageResponse.Data, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    inner class UserViewHolder(val binding: ItemSearchUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind() {
            val message = currentList[bindingAdapterPosition]
            binding.uname.text = message.fromusername
            binding.follow.text = DateUtils.fromToday(message.dateline)
            if (type == "contactsFollow")
                binding.fans.text = "关注了你"
            ImageUtil.showIMG(binding.avatar, message.fromUserAvatar)
            binding.executePendingBindings()
        }

    }


    inner class MessageViewHolder(val binding: ItemMessageContentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind() {
            val message = currentList[position]
            if (type == "atMe" || type == "atCommentMe") {
                //      binding.id = message.id
                //    binding.uid = message.uid
                //    binding.entityType = message.entityType
                binding.uname.text = message.username
                ImageUtil.showIMG(binding.avatar, message.userAvatar)
                if (message.deviceTitle != "") {
                    binding.device.text = message.deviceTitle
                    val drawable: Drawable = itemView.context.getDrawable(R.drawable.ic_device)!!
                    drawable.setBounds(
                        0,
                        0,
                        binding.device.textSize.toInt(),
                        binding.device.textSize.toInt()
                    )
                    binding.device.setCompoundDrawables(drawable, null, null, null)
                    binding.device.visibility = View.VISIBLE
                } else {
                    binding.device.visibility = View.INVISIBLE
                }
                binding.pubDate.text = DateUtils.fromToday(message.dateline)
                val drawable1: Drawable = itemView.context.getDrawable(R.drawable.ic_date)!!
                drawable1.setBounds(
                    0,
                    0,
                    binding.pubDate.textSize.toInt(),
                    binding.pubDate.textSize.toInt()
                )
                binding.pubDate.setCompoundDrawables(drawable1, null, null, null)

                val drawableLike: Drawable = itemView.context.getDrawable(R.drawable.ic_like)!!
                drawableLike.setBounds(
                    0,
                    0,
                    binding.like.textSize.toInt(),
                    binding.like.textSize.toInt()
                )
                if (message.userAction?.like == 1) {
                    DrawableCompat.setTint(
                        drawableLike,
                        itemView.context.getColorFromAttr(
                            rikka.preference.simplemenu.R.attr.colorPrimary
                        )
                    )
                    binding.like.setTextColor(
                        itemView.context.getColorFromAttr(
                            rikka.preference.simplemenu.R.attr.colorPrimary
                        )
                    )
                } else {
                    DrawableCompat.setTint(
                        drawableLike,
                        itemView.context.getColor(android.R.color.darker_gray)
                    )
                    binding.like.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                }
                binding.like.text = message.likenum
                binding.like.setCompoundDrawables(drawableLike, null, null, null)

                binding.reply.text = message.replynum
                val drawableReply: Drawable = itemView.context.getDrawable(R.drawable.ic_message)!!
                drawableReply.setBounds(
                    0,
                    0,
                    binding.like.textSize.toInt(),
                    binding.like.textSize.toInt()
                )
                binding.reply.setCompoundDrawables(drawableReply, null, null, null)

                binding.message.movementMethod =
                    LinkTextView.LocalLinkMovementMethod.instance
                binding.message.text = SpannableStringBuilderUtil.setText(
                    itemView.context,
                    message.message,
                    binding.message.textSize,
                    null
                )
            } else if (type == "feedLike") {
                // binding.entityType = message.entityType
                binding.uname.text = message.likeUsername
                // binding.uid = message.likeUid
                ImageUtil.showIMG(binding.avatar, message.likeAvatar)
                binding.pubDate.text = DateUtils.fromToday(message.likeTime)
                val drawable1: Drawable = itemView.context.getDrawable(R.drawable.ic_date)!!
                drawable1.setBounds(
                    0,
                    0,
                    binding.pubDate.textSize.toInt(),
                    binding.pubDate.textSize.toInt()
                )
                binding.pubDate.setCompoundDrawables(drawable1, null, null, null)
                binding.message.text = "赞了你的${message.infoHtml}"
            }


            if (message.forwardSourceFeed != null) {
                binding.forward.visibility = View.VISIBLE
                //    binding.forwardEntityType = message.forwardSourceFeed.entityType
                //   binding.forwardId = message.forwardSourceFeed.id
                //   binding.forwardUid = message.forwardSourceFeed.uid
                //   binding.forwardUname = message.forwardSourceFeed.username
                val title =
                    """<a class="feed-link-uname" href="/u/${message.forwardSourceFeed.uid}">@${message.forwardSourceFeed.username}: </a>${message.forwardSourceFeed.messageTitle}"""
                binding.forwardTitle.movementMethod =
                    LinkTextView.LocalLinkMovementMethod.instance
                binding.forwardTitle.text = SpannableStringBuilderUtil.setText(
                    itemView.context,
                    title,
                    binding.forwardMessage.textSize,
                    null
                )
                binding.forwardMessage.movementMethod =
                    LinkTextView.LocalLinkMovementMethod.instance
                binding.forwardMessage.text = SpannableStringBuilderUtil.setText(
                    itemView.context,
                    message.forwardSourceFeed.message,
                    binding.forwardMessage.textSize,
                    null
                )
                if (!message.forwardSourceFeed.picArr.isNullOrEmpty()) {
                    binding.multiImage.visibility = View.VISIBLE
                    if (message.forwardSourceFeed.picArr.size == 1) {
                        val imageLp = getImageLp(message.forwardSourceFeed.pic)
                        binding.multiImage.imgWidth = imageLp.first
                        binding.multiImage.imgHeight = imageLp.second
                    }
                    binding.multiImage.apply {
                        val urlList: MutableList<String> = ArrayList()
                        for (element in message.forwardSourceFeed.picArr)
                            urlList.add("$element.s.jpg")
                        setUrlList(urlList)
                    }
                } else {
                    binding.multiImage.visibility = View.GONE
                }
            } else {
                binding.forward.visibility = View.GONE
            }

            if (message.feed != null) {
                binding.forward1.visibility = View.VISIBLE
                if (type == "atCommentMe") {
                    // binding.forwardId1 = message.feed.id
                    // binding.forwardUid1 = message.feed.uid
                    binding.forwardUname1.text = "@${message.feed.username}"
                    binding.forwardMessage1.text = SpannableStringBuilderUtil.setText(
                        itemView.context,
                        message.feed.message,
                        binding.forwardMessage.textSize,
                        null
                    )
                    if (message.feed.pic.isNullOrEmpty())
                        binding.forward1Pic.visibility = View.GONE
                    else
                        ImageUtil.showIMG(binding.forward1Pic, message.feed.pic)
                } else if (type == "feedLike") {
                    //    binding.forwardId1 = message.fid
                    binding.forward1Pic.visibility = View.GONE
                    binding.forwardUname1.text = "@${message.username}"
                    binding.forwardMessage1.text = SpannableStringBuilderUtil.setText(
                        itemView.context,
                        message.message,
                        binding.forwardMessage.textSize,
                        null
                    )
                }
            } else {
                binding.forward1.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                MessageViewHolder(
                    ItemMessageContentBinding.inflate(
                        LayoutInflater.from(parent.context), parent,
                        false
                    )
                )
                /*viewbinding.uname.setOnClickListener {
                    IntentUtil.startActivity<UserActivity>(parent.context) {
                        putExtra("id", viewbinding.uid)
                    }
                }
                viewbinding.avatar.setOnClickListener {
                    IntentUtil.startActivity<UserActivity>(parent.context) {
                        putExtra("id", viewbinding.uid)
                    }
                }
                viewbinding.itemView.setOnLongClickListener {
                    IntentUtil.startActivity<CopyActivity>(parent.context) {
                        putExtra("text", viewbinding.message.text.toString())
                    }
                    true
                }
                viewbinding.itemView.setOnClickListener {
                    if (viewbinding.entityType == "feed") {
                        IntentUtil.startActivity<FeedActivity>(parent.context) {
                            putExtra("id", viewbinding.id)
                        }
                    }
                }
                viewbinding.forward.setOnClickListener {
                    if (viewbinding.forwardEntityType == "feed") {
                        IntentUtil.startActivity<FeedActivity>(parent.context) {
                            putExtra("id", viewbinding.forwardId)
                        }
                    }
                }
                viewbinding.forwardMessage.setOnClickListener {
                    if (viewbinding.forwardEntityType == "feed") {
                        IntentUtil.startActivity<FeedActivity>(parent.context) {
                            putExtra("id", viewbinding.forwardId)
                        }
                    }
                }
                viewbinding.forwardMessage.setOnLongClickListener {
                    IntentUtil.startActivity<CopyActivity>(parent.context) {
                        putExtra("text", viewbinding.forwardMessage.text.toString())
                    }
                    true
                }
                viewbinding.like.setOnClickListener {
                    if (PrefManager.isLogin) {
                        appListener?.onPostLike(
                            null,
                            viewbinding.isLike,
                            viewbinding.id,
                            viewbinding.bindingAdapterPosition
                        )
                    }
                }
                viewbinding.forward1.setOnClickListener {
                    IntentUtil.startActivity<FeedActivity>(parent.context) {
                        putExtra("id", viewbinding.forwardId1)
                    }
                }*/
            }


            1 -> {
                UserViewHolder(
                    ItemSearchUserBinding.inflate(
                        LayoutInflater.from(parent.context), parent,
                        false
                    )
                )
            }

            else -> throw IllegalArgumentException("invalid type")
        }

    }


    override fun getItemViewType(position: Int): Int {
        return when (type) {
            "atMe" -> 0
            "atCommentMe" -> 0
            "feedLike" -> 0
            "contactsFollow" -> 1
            "list" -> 1
            else -> throw IllegalArgumentException("invalid type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {

            is UserViewHolder -> {
                holder.bind()
            }


            is MessageViewHolder -> {
                holder.bind()
            }
        }
    }

}

class MessageDiffCallback : DiffUtil.ItemCallback<MessageResponse.Data>() {
    override fun areItemsTheSame(
        oldItem: MessageResponse.Data,
        newItem: MessageResponse.Data
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: MessageResponse.Data,
        newItem: MessageResponse.Data
    ): Boolean {
        return oldItem.id == newItem.id
    }
}