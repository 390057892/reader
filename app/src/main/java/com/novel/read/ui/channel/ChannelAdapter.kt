package com.novel.read.ui.channel

import android.annotation.SuppressLint
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.entity.SectionEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.getViewBinding
import com.novel.read.base.withBinding
import com.novel.read.constant.IntentAction
import com.novel.read.data.model.AllType
import com.novel.read.data.model.ChannelSection
import com.novel.read.databinding.ItemChannelBinding
import com.novel.read.databinding.ItemChannelHeadBinding
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.startActivity

class ChannelAdapter : BaseSectionQuickAdapter<ChannelSection, BaseViewHolder>(
    R.layout.item_channel_head,
    R.layout.item_channel
) {

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder = super.onCreateDefViewHolder(parent, viewType)
        return when (viewType) {
            SectionEntity.HEADER_TYPE -> viewHolder.withBinding(ItemChannelHeadBinding::bind)
            SectionEntity.NORMAL_TYPE -> viewHolder.withBinding(ItemChannelBinding::bind)
            else -> throw IllegalStateException()
        }
    }

    override fun convertHeader(helper: BaseViewHolder, item: ChannelSection) {
        val value = item.obj as String
        helper.getViewBinding<ItemChannelHeadBinding>().apply {
            tvSex.text = value
        }
    }

    @SuppressLint("SetTextI18n")
    override fun convert(holder: BaseViewHolder, item: ChannelSection) {
        val value = item.obj as AllType
        holder.itemView.onClick {
            context.startActivity<ChannelInfoActivity>(
                Pair(IntentAction.bookTypeId, value.bookTypeId),
                Pair(IntentAction.channelName, value.getBChannel()),
            )
        }
        holder.getViewBinding<ItemChannelBinding>().apply {
            tvName.text = value.getBChannel()
            ivCover.load(value.typeImageUrl, "", "")
            tvCount.text = "${value.bookCount}本"
        }
    }


}