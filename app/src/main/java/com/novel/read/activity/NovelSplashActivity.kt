package com.novel.read.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.novel.read.R
import com.novel.read.utlis.PermissionUtil
import com.novel.read.utlis.StatusBarUtil
import kotlinx.android.synthetic.main.activity_splash.*

class NovelSplashActivity : AppCompatActivity(), PermissionUtil.PermissionCallBack {

    private var flag = false
    private var runnable: Runnable? = null

    private var mPermissionUtil: PermissionUtil = PermissionUtil.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBarUtil.setBarsStyle(this, R.color.colorPrimary, true)
        setContentView(R.layout.activity_splash)
        mPermissionUtil.requestPermissions(this, PERMISSION_CODE, this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        mPermissionUtil.requestResult(this, permissions, grantResults, this)
    }

    private fun init() {
        runnable = Runnable { goHome() }
        tvSkip.postDelayed(runnable, 2000)
        tvSkip.setOnClickListener { goHome() }
    }

    @Synchronized
    private fun goHome() {
        if (!flag) {
            flag = true
            startActivity(Intent(this, NovelMainActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        flag = true
        tvSkip.removeCallbacks(runnable)
    }

    override fun onPermissionSuccess() {
        init()
    }

    override fun onPermissionReject(strMessage: String) {
        finish()
    }

    override fun onPermissionFail() {
        mPermissionUtil.requestPermissions(this, PERMISSION_CODE, this)
    }

    companion object {
        private val PERMISSION_CODE = 999
    }
}
