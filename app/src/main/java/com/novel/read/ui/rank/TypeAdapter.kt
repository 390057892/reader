package com.novel.read.ui.rank

import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.data.model.TypeName
import kotlinx.android.synthetic.main.item_type.view.*

class TypeAdapter :
    BaseQuickAdapter<TypeName, BaseViewHolder>(R.layout.item_type), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: TypeName) {
        holder.itemView.run {
            tv_type.text = item.name
            if (item.check) {
                iv_check.visibility = View.VISIBLE
                ll_bg.setBackgroundColor(context.resources.getColor(R.color.background))
            } else {
                iv_check.visibility = View.GONE
                ll_bg.setBackgroundColor(context.resources.getColor(R.color.background_menu))
            }
        }
    }
}