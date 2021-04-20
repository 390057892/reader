package com.novel.read.ui.rank

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chad.library.adapter.base.module.LoadMoreModule
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.VBViewHolder
import com.novel.read.data.model.TypeName
import com.novel.read.databinding.ItemTypeBinding

class TypeAdapter :
    BaseBindingAdapter<TypeName, ItemTypeBinding>(), LoadMoreModule {

    override fun convert(holder: VBViewHolder<ItemTypeBinding>, item: TypeName) {
        holder.vb.run {
            tvType.text = item.name
            if (item.check) {
                ivCheck.visibility = View.VISIBLE
                llBg.setBackgroundColor(context.resources.getColor(R.color.background))
            } else {
                ivCheck.visibility = View.GONE
                llBg.setBackgroundColor(context.resources.getColor(R.color.background_menu))
            }
        }
    }

    override fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): ItemTypeBinding {
        return ItemTypeBinding.inflate(inflater, parent, false)
    }
}