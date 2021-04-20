package com.novel.read.ui.feedback

import android.os.Bundle
import android.text.TextUtils
import androidx.lifecycle.observe
import com.novel.read.R
import com.novel.read.base.VMBaseActivity
import com.novel.read.databinding.ActivityFeedBackBinding
import com.novel.read.utils.ext.getViewModel
import org.jetbrains.anko.toast

class FeedBackActivity : VMBaseActivity<ActivityFeedBackBinding,FeedBackViewModel>() {

    override val viewModel: FeedBackViewModel
        get() = getViewModel(FeedBackViewModel::class.java)

    override fun getViewBinding(): ActivityFeedBackBinding {
        return ActivityFeedBackBinding.inflate(layoutInflater)
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        binding.buttonReport.setOnClickListener {
            if (TextUtils.isEmpty(binding.etFeed.text.toString().trim())){
                toast(getString(R.string.feedback_not_empty))
                return@setOnClickListener
            }
            viewModel.feedback(binding.etFeed.text.toString().trim())
        }

        viewModel.success.observe(this){
            if(it){
                toast(getString(R.string.feedback_success))
                finish()
            }
        }

    }


}