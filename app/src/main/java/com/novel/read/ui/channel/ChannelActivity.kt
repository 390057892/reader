package com.novel.read.ui.channel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.data.model.ChannelSection
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.getViewModel
import kotlinx.android.synthetic.main.activity_channel.*
import java.util.ArrayList

class ChannelActivity : VMBaseActivity<ChannelViewModel>(R.layout.activity_channel) {

    override val viewModel: ChannelViewModel
        get() = getViewModel(ChannelViewModel::class.java)

    private lateinit var adapter: ChannelAdapter
    private val data: MutableList<ChannelSection> = ArrayList()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ATH.applyEdgeEffectColor(rlv_channel)
        initRecyclerView()
        upRecyclerData()
    }

    private fun initRecyclerView() {
        rlv_channel.layoutManager = GridLayoutManager(this, 2)
        adapter = ChannelAdapter()
        rlv_channel.adapter = adapter
    }

    private fun upRecyclerData() {
        onRefresh()
        viewModel.run {
            channelResp.observe(this@ChannelActivity) {
                for (i in it.allType.indices) {
                    data.add(ChannelSection(false, it.allType[i]))
                }
                adapter.setList(data)
            }

            refreshStatus.observe(this@ChannelActivity) {
                when (it) {
                    AppConst.loading -> {
                        adapter.setList(null)
                        adapter.isUseEmpty = true
                    }
                    AppConst.complete -> {
                        adapter.isUseEmpty = false
                    }
                    else -> {
                        adapter.setList(null)
                        adapter.setEmptyView(getErrorView())
                        adapter.isUseEmpty = true
                    }
                }
            }
        }

    }

    private fun onRefresh() {
        adapter.setEmptyView(R.layout.view_loading)
        viewModel.getChannel()
    }

    private fun getErrorView(): View {
        val errorView: View = layoutInflater.inflate(R.layout.view_net_error, rlv_channel, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }

}