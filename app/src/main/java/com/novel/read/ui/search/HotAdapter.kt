package com.novel.read.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.constant.AppConst
import com.novel.read.databinding.ItemLabelBinding
import com.novel.read.utils.StringUtils

class HotAdapter : BaseBindingAdapter<String, ItemLabelBinding>() {

    override fun convert(holder: VBViewHolder<ItemLabelBinding>, item: String) {
        holder.vb.run {
            tvLabel.text = StringUtils.convertCC(item)
            tvLabel.setBackgroundColor(AppConst.tagColors[holder.adapterPosition % 8])
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemLabelBinding {
        return ItemLabelBinding.inflate(inflater, parent, false)
    }

}