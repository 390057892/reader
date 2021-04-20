package com.novel.read.ui.read.config

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.core.view.get
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.R
import com.novel.read.base.BaseBindingAdapter
import com.novel.read.base.BaseDialogFragment
import com.novel.read.base.VBViewHolder
import com.novel.read.constant.EventBus
import com.novel.read.databinding.DialogReadBookStyleBinding
import com.novel.read.databinding.ItemReadStyleBinding
import com.novel.read.help.ReadBookConfig
import com.novel.read.service.help.ReadBook
import com.novel.read.ui.read.ReadBookActivity
import com.novel.read.utils.ext.*
import com.novel.read.utils.viewbindingdelegate.viewBinding
import org.jetbrains.anko.sdk27.listeners.onCheckedChange
import org.jetbrains.anko.sdk27.listeners.onClick
import org.jetbrains.anko.sdk27.listeners.onLongClick

class ReadStyleDialog : BaseDialogFragment() {
    private val binding by viewBinding(DialogReadBookStyleBinding::bind)
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

    private fun initView()= with(binding) {
        val bg = requireContext().bottomBackground
        rootView.setBackgroundColor(bg)
        styleAdapter = StyleAdapter()
        rvStyle.adapter = styleAdapter

    }

    private fun initData() {
        upView()
        styleAdapter.setList(ReadBookConfig.configList)
    }

    private fun initViewEvent() = with(binding){
        tvFontType.onChanged {
            postEvent(EventBus.UP_CONFIG, true)
        }

        flTextBold.onChanged {
            postEvent(EventBus.UP_CONFIG, true)
        }

        flTextFont.onClick {
//            FontSelectDialog().show(childFragmentManager, "fontSelectDialog")
        }

        rgPageAnim.onCheckedChange { _, checkedId ->
            ReadBookConfig.pageAnim = -1
            ReadBookConfig.pageAnim = binding.rgPageAnim.getIndexById(checkedId)
            callBack?.upPageAnim()
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
        BaseBindingAdapter<ReadBookConfig.Config, ItemReadStyleBinding>() {

        override fun convert(
            holder: VBViewHolder<ItemReadStyleBinding>,
            item: ReadBookConfig.Config
        ) {
            holder.vb.run {
                ivStyle.setText(item.name.ifBlank { "文字" })
                ivStyle.setTextColor(item.curTextColor())
                ivStyle.setImageDrawable(item.curBgDrawable(100, 150))
                if (ReadBookConfig.styleSelect == holder.layoutPosition) {
                    ivStyle.borderColor = accentColor
                    ivStyle.setTextBold(true)
                } else {
                    ivStyle.borderColor = item.curTextColor()
                    ivStyle.setTextBold(false)
                }
                ivStyle.onClick {
                    if (ivStyle.isInView) {
                        changeBg(holder.layoutPosition)
                    }
                }
                ivStyle.onLongClick {
                    if (ivStyle.isInView) {
                        showBgTextConfig(holder.layoutPosition)
                    } else {
                        false
                    }
                }
            }
        }

        override fun createViewBinding(
            inflater: LayoutInflater,
            parent: ViewGroup
        ): ItemReadStyleBinding {
            return ItemReadStyleBinding.inflate(inflater, parent, false)
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

    private fun upView() = with(binding){

        ReadBook.pageAnim().let {
            if (it >= 0 && it < rgPageAnim.childCount) {
                rgPageAnim.check(rgPageAnim[it].id)
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