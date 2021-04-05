package com.novel.read.ui.channel

import android.annotation.SuppressLint
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.constant.IntentAction
import com.novel.read.data.model.AllType
import com.novel.read.data.model.ChannelSection
import kotlinx.android.synthetic.main.item_channel.view.*
import kotlinx.android.synthetic.main.item_channel_head.view.*
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.startActivity

class ChannelAdapter : BaseSectionQuickAdapter<ChannelSection, BaseViewHolder>(
    R.layout.item_channel_head,
    R.layout.item_channel
) {

    override fun convertHeader(helper: BaseViewHolder, item: ChannelSection) {
        val value = item.obj as String
        helper.itemView.run {
            tv_sex.text = value
        }
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: ChannelSection) {
        val value = item.obj as AllType
        holder.itemView.run {
            tv_name.text = value.getBChannel()
            iv_cover.load(value.typeImageUrl, "", "")
            tv_count.text= "${value.bookCount}本"
            onClick {
                context.startActivity<ChannelInfoActivity>(
                    Pair(IntentAction.bookTypeId, value.bookTypeId),
                    Pair(IntentAction.channelName, value.getBChannel()),
                )
            }
        }
    }


}