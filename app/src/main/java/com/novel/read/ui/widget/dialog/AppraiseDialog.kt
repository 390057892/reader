package com.novel.read.ui.widget.dialog

import android.content.Context
import android.os.Bundle
import android.view.*
import com.novel.read.R
import com.novel.read.base.BaseDialogFragment
import com.novel.read.databinding.DialogGoAppraiseBinding
import com.novel.read.utils.ext.goShop
import com.novel.read.utils.viewbindingdelegate.viewBinding

class AppraiseDialog : BaseDialogFragment() {
    private val binding by viewBinding(DialogGoAppraiseBinding::bind)

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_go_appraise, container)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.dialog_app)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?){
        binding.tvAppraise.setOnClickListener{
            context?.goShop()
            dismiss()
        }
        binding.clBg.setBackgroundResource(R.color.transparent)
        binding.tvCancel.setOnClickListener { dismiss() }
    }
}