package com.novel.read.fragment

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.mango.mangolib.event.EventManager
import com.novel.read.R
import com.novel.read.activity.NovelSearchActivity
import com.novel.read.activity.NovelSettingActivity
import com.novel.read.base.NovelBaseFragment
import com.novel.read.constants.Constant
import com.novel.read.event.ReStartEvent
import com.novel.read.showToast
import com.novel.read.utlis.VersionUtil
import com.novel.read.widget.dialog.AppraiseDialog
import com.squareup.otto.Subscribe
import kotlinx.android.synthetic.main.fragment_more.*

/**
 * create by 赵利君 on 2019/6/10
 * describe:
 */
class MoreFragment : NovelBaseFragment() {

    override fun getLayoutId(): Int {
        return R.layout.fragment_more
    }

    override fun initView() {
        EventManager.instance.registerSubscriber(this)
        toolbar.inflateMenu(R.menu.title_more)
    }

    override fun initData() {
        toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_search) {
                toActivity(NovelSearchActivity::class.java)
                activity!!.overridePendingTransition(
                    R.anim.message_fade_in,
                    R.anim.message_fade_out
                )
            }
            true
        }

        //意见反馈
        tv_options.setOnClickListener {
            feedback()
        }

        //评价
        tv_appraise.setOnClickListener {
            val dialog = AppraiseDialog(activity!!)
            dialog.appraiseDialog(View.OnClickListener {
                val uri = Uri.parse("https://github.com/390057892/reader")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
                dialog.dismiss()
            })
            dialog.show()
        }

        //设置
        tv_setting.setOnClickListener {
            toActivity(NovelSettingActivity::class.java)
        }
    }

    private fun feedback() {
        val email = Intent(Intent.ACTION_SEND)
        //邮件发送类型：无附件，纯文本
        email.type = "plain/text"
        //邮件接收者（数组，可以是多位接收者）
        val emailReceiver = arrayOf(Constant.FeedBackEmail)
        val emailTitle = getString(R.string.opinions)
        val emailContent = ""
        //设置邮件地址
        email.putExtra(Intent.EXTRA_EMAIL, emailReceiver)
        //设置邮件标题
        email.putExtra(Intent.EXTRA_SUBJECT, emailTitle)
        //设置发送的内容
        email.putExtra(Intent.EXTRA_TEXT, emailContent)
        //调用系统的邮件系统
        startActivity(Intent.createChooser(email, "请选择邮件发送软件"))

    }


    @Subscribe
    fun restart(event: ReStartEvent) {
        activity!!.recreate()
    }

    companion object {
        fun newInstance(): MoreFragment {
            val args = Bundle()
            val fragment = MoreFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        EventManager.instance.unregisterSubscriber(this)
    }
}
