package com.novel.read.ui.read.config

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.core.view.get
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.BaseDialogFragment
import com.novel.read.constant.EventBus
import com.novel.read.help.ReadBookConfig
import com.novel.read.service.help.ReadBook
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.ext.*
import kotlinx.android.synthetic.main.dialog_read_book_style.*
import kotlinx.android.synthetic.main.activity_read_book.*
import kotlinx.android.synthetic.main.item_read_style.view.*
import org.jetbrains.anko.sdk27.listeners.onCheckedChange
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.sdk27.listeners.onLongClick

class ReadStyleDialog : BaseDialogFragment() {

    val callBack get() = activity as? ReadBookActivity
    private lateinit var styleAdapter: StyleAdapter


    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            it.setBackgroundDrawableResource(R.color.background)
            it.decorView.setPadding(0, 0, 0, 0)
            val attr = it.attributes
            attr.dimAmount = 0.0f
            attr.gravity = Gravity.BOTTOM
            it.attributes = attr
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_read_book_style, container)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initData()
        initViewEvent()
    }

    private fun initView() {
        val bg = requireContext().bottomBackground
        root_view.setBackgroundColor(bg)
        styleAdapter = StyleAdapter()
        rv_style.adapter = styleAdapter

    }

    private fun initData() {
        upView()
        styleAdapter.setList(ReadBookConfig.configList)
    }

    private fun initViewEvent() {
        tv_font_type.onChanged {
            postEvent(EventBus.UP_CONFIG, true)
        }

        fl_text_Bold.onChanged {
            postEvent(EventBus.UP_CONFIG, true)
        }

        fl_text_font.onClick {
//            FontSelectDialog().show(childFragmentManager, "fontSelectDialog")
        }

        rg_page_anim.onCheckedChange { _, checkedId ->
            ReadBookConfig.pageAnim = -1
            ReadBookConfig.pageAnim = rg_page_anim.getIndexById(checkedId)
            callBack?.page_view?.upPageAnim()
        }

        nbTextSizeAdd.setOnClickListener {
            ReadBookConfig.textSize = ReadBookConfig.textSize + 2
            postEvent(EventBus.UP_CONFIG, true)
            nbTextSize.text = ReadBookConfig.textSize.toString()
        }
        nbTextSizeDec.setOnClickListener {
            ReadBookConfig.textSize = ReadBookConfig.textSize - 2
            postEvent(EventBus.UP_CONFIG, true)
            nbTextSize.text = ReadBookConfig.textSize.toString()
        }
    }


    inner class StyleAdapter :
        BaseQuickAdapter<ReadBookConfig.Config, BaseViewHolder>(R.layout.item_read_style) {

        override fun convert(holder: BaseViewHolder, item: ReadBookConfig.Config) {
            holder.itemView.run {
                iv_style.setText(item.name.ifBlank { "文字" })
                iv_style.setTextColor(item.curTextColor())
                iv_style.setImageDrawable(item.curBgDrawable(100, 150))
                if (ReadBookConfig.styleSelect == holder.layoutPosition) {
                    iv_style.borderColor = accentColor
                    iv_style.setTextBold(true)
                } else {
                    iv_style.borderColor = item.curTextColor()
                    iv_style.setTextBold(false)
                }
                iv_style.onClick {
                    if (iv_style.isInView) {
                        changeBg(holder.layoutPosition)
                    }
                }
                iv_style.onLongClick {
                    if (iv_style.isInView) {
                        showBgTextConfig(holder.layoutPosition)
                    } else {
                        false
                    }
                }
            }
        }
    }


    private fun changeBg(index: Int) {
        val oldIndex = ReadBookConfig.styleSelect
        if (index != oldIndex) {
            ReadBookConfig.styleSelect = index
            ReadBookConfig.upBg()
            upView()
            styleAdapter.notifyItemChanged(oldIndex)
            styleAdapter.notifyItemChanged(index)
            postEvent(EventBus.UP_CONFIG, true)
        }
    }

    private fun showBgTextConfig(index: Int): Boolean {
        dismiss()
        changeBg(index)
        callBack?.showBgTextConfig()
        return true
    }

    private fun upView() {

        ReadBook.pageAnim().let {
            if (it >= 0 && it < rg_page_anim.childCount) {
                rg_page_anim.check(rg_page_anim[it].id)
            }
        }
        ReadBookConfig.let {

        }
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        ReadBookConfig.save()
    }
}