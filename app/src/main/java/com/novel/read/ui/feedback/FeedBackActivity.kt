package com.novel.read.ui.feedback

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.observe
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.utils.ext.getViewModel
import kotlinx.android.synthetic.main.activity_feed_back.*
import org.jetbrains.anko.toast

class FeedBackActivity : VMBaseActivity<FeedBackViewModel>(R.layout.activity_feed_back) {

    override val viewModel: FeedBackViewModel
        get() = getViewModel(FeedBackViewModel::class.java)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        button_report.setOnClickListener {
            if (TextUtils.isEmpty(et_feed.text.toString().trim())){
                toast(getString(R.string.feedback_not_empty))
                return@setOnClickListener
            }
            viewModel.feedback(et_feed.text.toString().trim())
        }

        viewModel.success.observe(this){
            if(it){
                toast(getString(R.string.feedback_success))
                finish()
            }
        }

    }

}