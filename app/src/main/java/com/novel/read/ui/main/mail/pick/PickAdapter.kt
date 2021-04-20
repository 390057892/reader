package com.novel.read.ui.main.mail.pick

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.getViewBinding
import com.novel.read.base.withBinding
import com.novel.read.constant.AppConst
import com.novel.read.constant.IntentAction
import com.novel.read.constant.LayoutType
import com.novel.read.data.model.*
import com.novel.read.databinding.ItemHotBinding
import com.novel.read.databinding.ItemPickHeadBinding
import com.novel.read.lib.ATH
import com.novel.read.ui.channel.ChannelActivity
import com.novel.read.ui.daily.DailyActivity
import com.novel.read.ui.end.EndActivity
import com.novel.read.ui.rank.RankActivity
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.startActivity


class PickAdapter<T : MultiItemEntity>(data: MutableList<T>?) :
    BaseMultiItemQuickAdapter<T, BaseViewHolder>(data) {

    init {
        addItemType(LayoutType.OTHER, R.layout.item_pick_head)
        addItemType(LayoutType.HOT, R.layout.item_hot)
        addItemType(LayoutType.CLICK, R.layout.item_hot)
        addItemType(LayoutType.RECOMMEND, R.layout.item_hot)
        addItemType(LayoutType.END, R.layout.item_hot)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val viewHolder = super.onCreateDefViewHolder(parent, viewType)
        return when (viewType) {
            LayoutType.OTHER -> viewHolder.withBinding(ItemPickHeadBinding::bind)
            LayoutType.HOT -> viewHolder.withBinding(ItemHotBinding::bind)
            LayoutType.CLICK -> viewHolder.withBinding(ItemHotBinding::bind)
            LayoutType.RECOMMEND -> viewHolder.withBinding(ItemHotBinding::bind)
            LayoutType.END -> viewHolder.withBinding(ItemHotBinding::bind)
            else -> throw IllegalStateException()
        }
    }

    override fun convert(holder: BaseViewHolder, item: T) {
        when (item) {
            is TypeEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                }
                holder.getViewBinding<ItemPickHeadBinding>().apply {
                    tvDaily.onClick {
                        context.startActivity<DailyActivity>()
                    }
                    tvRank.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.HOT),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    tvEnd.onClick {
                        context.startActivity<EndActivity>()
                    }
                    tvType.onClick {
                        context.startActivity<ChannelActivity>()
                    }
                }
            }
            is HotEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                }
                holder.getViewBinding<ItemHotBinding>().apply{
                    rlvHot.setHasFixedSize(true)
                    llTitle.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.HOT),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    if (rlvHot.layoutManager == null) {
                        rlvHot.layoutManager = GridLayoutManager(context, 4)
                    }
                    if (rlvHot.adapter == null) {
                        val hotAdapter = HotAdapter()
                        hotAdapter.setList(item.hotRanks)
                        rlvHot.adapter = hotAdapter
                    }
                }
            }
            is ClickEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                }
                holder.getViewBinding<ItemHotBinding>().apply{
                    ivHead.setImageResource(R.drawable.ic_click)
                    tvHead.text=context.getString(R.string.click_title)
                    llTitle.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.CLICK),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    rlvHot.setHasFixedSize(true)
                    if (rlvHot.layoutManager == null) {
                        rlvHot.layoutManager = GridLayoutManager(context, 2)
                    }
                    if (rlvHot.adapter == null) {
                        val clickAdapter = ClickAdapter()
                        clickAdapter.setList(item.clickRanks)
                        rlvHot.adapter = clickAdapter
                    }
                }
            }
            is RecommendEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                }
                holder.getViewBinding<ItemHotBinding>().apply{
                    ivHead.setImageResource(R.drawable.ic_recommend)
                    tvHead.text=context.getString(R.string.recommend_title)
                    llTitle.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.RECOMMEND),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    rlvHot.setHasFixedSize(true)
                    if (rlvHot.layoutManager == null) {
                        rlvHot.layoutManager = GridLayoutManager(context, 4)
                    }
                    if (rlvHot.adapter == null) {
                        val recommendAdapter = RecommendAdapter()
                        recommendAdapter.setList(item.recommendRanks)
                        rlvHot.adapter = recommendAdapter
                    }
                }
            }
            is EndEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                }
                holder.getViewBinding<ItemHotBinding>().apply{
                    ivHead.setImageResource(R.drawable.ic_end)
                    tvHead.text=context.getString(R.string.end_title)
                    llTitle.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.END),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    rlvHot.setHasFixedSize(true)
                    if (rlvHot.layoutManager == null) {
                        rlvHot.layoutManager = GridLayoutManager(context, 4)
                    }
                    if (rlvHot.adapter == null) {
                        val endAdapter = EndAdapter()
                        endAdapter.setList(item.endRanks)
                        rlvHot.adapter = endAdapter
                    }
                }
            }
        }
    }

}



