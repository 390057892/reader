package com.novel.read.ui.main.mail.pick

import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.entity.MultiItemEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.constant.AppConst
import com.novel.read.constant.IntentAction
import com.novel.read.constant.LayoutType
import com.novel.read.data.model.*
import com.novel.read.lib.ATH
import com.novel.read.ui.channel.ChannelActivity
import com.novel.read.ui.daily.DailyActivity
import com.novel.read.ui.end.EndActivity
import com.novel.read.ui.rank.RankActivity
import kotlinx.android.synthetic.main.item_hot.view.*
import kotlinx.android.synthetic.main.item_pick_head.view.*
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

    override fun convert(holder: BaseViewHolder, item: T) {
        when (item) {
            is TypeEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                    tv_daily.onClick {
                        context.startActivity<DailyActivity>()
                    }
                    tv_rank.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.HOT),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    tv_end.onClick {
                        context.startActivity<EndActivity>()
                    }

                    tv_type.onClick {
                        context.startActivity<ChannelActivity>()
                    }

                }
            }
            is HotEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                    rlv_hot.setHasFixedSize(true)
                    ll_title.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.HOT),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    if (rlv_hot.layoutManager == null) {
                        rlv_hot.layoutManager = GridLayoutManager(context, 4)
                    }
                    if (rlv_hot.adapter == null) {
                        val hotAdapter = HotAdapter()
                        hotAdapter.setList(item.hotRanks)
                        rlv_hot.adapter = hotAdapter
                    }
                }
            }
            is ClickEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                    iv_head.setImageResource(R.drawable.ic_click)
                    tv_head.text=context.getString(R.string.click_title)
                    ll_title.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.CLICK),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    rlv_hot.setHasFixedSize(true)
                    if (rlv_hot.layoutManager == null) {
                        rlv_hot.layoutManager = GridLayoutManager(context, 2)
                    }
                    if (rlv_hot.adapter == null) {
                        val clickAdapter = ClickAdapter()
                        clickAdapter.setList(item.clickRanks)
                        rlv_hot.adapter = clickAdapter
                    }
                }
            }
            is RecommendEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                    iv_head.setImageResource(R.drawable.ic_recommend)
                    tv_head.text=context.getString(R.string.recommend_title)
                    ll_title.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.RECOMMEND),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    rlv_hot.setHasFixedSize(true)
                    if (rlv_hot.layoutManager == null) {
                        rlv_hot.layoutManager = GridLayoutManager(context, 4)
                    }
                    if (rlv_hot.adapter == null) {
                        val recommendAdapter = RecommendAdapter()
                        recommendAdapter.setList(item.recommendRanks)
                        rlv_hot.adapter = recommendAdapter
                    }
                }
            }
            is EndEntity -> {
                holder.itemView.run {
                    ATH.applyBackgroundTint(this)
                    iv_head.setImageResource(R.drawable.ic_end)
                    tv_head.text=context.getString(R.string.end_title)
                    ll_title.onClick {
                        context.startActivity<RankActivity>(
                            Pair(IntentAction.rankType, LayoutType.END),
                            Pair(IntentAction.homeType, AppConst.home)
                        )
                    }
                    rlv_hot.setHasFixedSize(true)
                    if (rlv_hot.layoutManager == null) {
                        rlv_hot.layoutManager = GridLayoutManager(context, 4)
                    }
                    if (rlv_hot.adapter == null) {
                        val endAdapter = EndAdapter()
                        endAdapter.setList(item.endRanks)
                        rlv_hot.adapter = endAdapter
                    }
                }
            }
        }
    }

}



