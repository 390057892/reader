package com.novel.read.ui.channel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.constant.AppConst
import com.novel.read.data.model.ChannelSection
import com.novel.read.databinding.ActivityChannelBinding
import com.novel.read.lib.ATH
import com.novel.read.utils.ext.getViewModel
import java.util.ArrayList

class ChannelActivity : VMBaseActivity<ActivityChannelBinding, ChannelViewModel>() {

    override val viewModel: ChannelViewModel
        get() = getViewModel(ChannelViewModel::class.java)

    override fun getViewBinding(): ActivityChannelBinding {
        return ActivityChannelBinding.inflate(layoutInflater)
    }

    private lateinit var adapter: ChannelAdapter
    private val data: MutableList<ChannelSection> = ArrayList()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        ATH.applyEdgeEffectColor(binding.rlvChannel)
        initRecyclerView()
        upRecyclerData()
    }

    private fun initRecyclerView() = with(binding) {
        rlvChannel.layoutManager = GridLayoutManager(this@ChannelActivity, 2)
        adapter = ChannelAdapter()
        rlvChannel.adapter = adapter
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
        val errorView: View =
            layoutInflater.inflate(R.layout.view_net_error, binding.rlvChannel, false)
        errorView.setOnClickListener { onRefresh() }
        return errorView
    }


}