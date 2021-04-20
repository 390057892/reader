package com.novel.read.ui.widget.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.novel.read.R
import com.novel.read.base.BaseDialogFragment
import com.novel.read.databinding.DialogPhotoViewBinding
import com.novel.read.service.help.ReadBook
import com.novel.read.utils.viewbindingdelegate.viewBinding
import io.legado.app.ui.book.read.page.provider.ImageProvider

class PhotoDialog : BaseDialogFragment() {

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            chapterIndex: Int,
            src: String
        ) {
            PhotoDialog().apply {
                val bundle = Bundle()
                bundle.putInt("chapterIndex", chapterIndex)
                bundle.putString("src", src)
                arguments = bundle
            }.show(fragmentManager, "photoDialog")
        }

    }

    private val binding by viewBinding(DialogPhotoViewBinding::bind)

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_photo_view, container)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let {
            val chapterIndex = it.getInt("chapterIndex")
            val src = it.getString("src")
            ReadBook.book?.let { book ->
                src?.let {
                    execute {
                        ImageProvider.getImage(book, chapterIndex, src)
                    }.onSuccess { bitmap ->
                        if (bitmap != null) {
                            binding.photoView.setImageBitmap(bitmap)
                        }
                    }
                }
            }
        }

    }

}
