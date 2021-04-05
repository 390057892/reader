package com.novel.read.ui.record

import android.os.Bundle
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.novel.read.App
import com.novel.read.R
import com.novel.read.base.BaseActivity
import com.novel.read.data.db.entity.ReadRecord
import kotlinx.android.synthetic.main.activity_read_record.*
import kotlinx.android.synthetic.main.item_book_common.view.tv_book_name
import kotlinx.android.synthetic.main.item_read_record.view.*

class ReadRecordActivity : BaseActivity(R.layout.activity_read_record) {
    lateinit var adapter: RecordAdapter
    private var sortMode = 0
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        initView()
        initData()
    }

    private fun initData() {
        val readRecords = App.db.getReadRecordDao().getAll()
        adapter.setList(readRecords)
    }

    private fun initView() {
        adapter = RecordAdapter()
        recycler_view.adapter = adapter
    }


    inner class RecordAdapter :
        BaseQuickAdapter<ReadRecord, BaseViewHolder>(R.layout.item_read_record) {
        override fun convert(holder: BaseViewHolder, item: ReadRecord) {
            holder.itemView.run {
                tv_book_name.text = item.bookName
                tv_read_time.text = formatDuring(item.readTime)
            }
        }

//        private fun sureDelAlert(item: ReadRecord) {
//            alert(R.string.delete) {
//                setMessage(getString(R.string.sure_del_any, item.bookName))
//                okButton {
//                    App.db.getReadRecordDao().delete(item)
//                    initData()
//                }
//                noButton()
//            }.show()
//        }

        private fun formatDuring(mss: Long): String {
            val days = mss / (1000 * 60 * 60 * 24)
            val hours = mss % (1000 * 60 * 60 * 24) / (1000 * 60 * 60)
            val minutes = mss % (1000 * 60 * 60) / (1000 * 60)
            val seconds = mss % (1000 * 60) / 1000
            val d = if (days > 0) "${days}天" else ""
            val h = if (hours > 0) "${hours}小时" else ""
            val m = if (minutes > 0) "${minutes}分钟" else ""
            val s = if (seconds > 0) "${seconds}秒" else ""
            var time = "$d$h$m$s"
            if (time.isBlank()) {
                time = "0秒"
            }
            return time
        }
    }
}