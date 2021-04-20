package com.novel.read.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseSectionQuickAdapter
import com.chad.library.adapter.base.entity.SectionEntity
import com.chad.library.adapter.base.viewholder.BaseViewHolder

abstract class BaseBindingAdapter<T, VB : ViewBinding>(data: MutableList<T>? = null) :
    BaseQuickAdapter<T, VBViewHolder<VB>>(0, data) {

    abstract fun createViewBinding(inflater: LayoutInflater, parent: ViewGroup): VB

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): VBViewHolder<VB> {
        val viewBinding = createViewBinding(LayoutInflater.from(parent.context), parent)
        return VBViewHolder(viewBinding, viewBinding.root)
    }

}

class VBViewHolder<VB : ViewBinding>(val vb: VB, view: View) : BaseViewHolder(view)


@JvmName("bind")
fun <VB : ViewBinding> BaseViewHolder.withBinding(bind: (View) -> VB): BaseViewHolder =
    BaseViewHolderWithBinding(bind(itemView))

@JvmName("getBinding")
@Suppress("UNCHECKED_CAST")
fun <VB : ViewBinding> BaseViewHolder.getViewBinding(): VB {
    if (this is BaseViewHolderWithBinding<*>) {
        return binding as VB
    } else {
        throw IllegalStateException("The binding could not be found.")
    }
}

class BaseViewHolderWithBinding<VB : ViewBinding>(val binding: VB) : BaseViewHolder(binding.root)