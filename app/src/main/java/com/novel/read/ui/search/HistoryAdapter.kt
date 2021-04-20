package com.novel.read.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.db.entity.SearchHistory
import com.novel.read.databinding.ItemLabelBinding

class HistoryAdapter : BaseBindingAdapter<SearchHistory, ItemLabelBinding>() {

    override fun convert(holder: VBViewHolder<ItemLabelBinding>, item: SearchHistory) {
        holder.vb.run {
            tvLabel.text = item.getBKey()
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemLabelBinding {
        return ItemLabelBinding.inflate(inflater, parent, false)
    }

}