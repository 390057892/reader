package com.novel.read.base

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.util.AttributeSet
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.novel.read.R
import com.novel.read.constant.AppConst
import com.novel.read.constant.Theme
import com.novel.read.lib.*
import com.novel.read.ui.widget.TitleBar
import com.novel.read.utils.ColorUtils
import com.novel.read.utils.LanguageUtils
import com.novel.read.utils.ext.disableAutoFill
import com.novel.read.utils.ext.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel

abstract class BaseActivity<VB : ViewBinding>(
    val fullScreen: Boolean = true,
    private val theme: Theme = Theme.Auto,
    private val toolBarTheme: Theme = Theme.Auto,
    private val transparent: Boolean = false
) : AppCompatActivity(),
    CoroutineScope by MainScope() {

    protected val binding: VB by lazy { getViewBinding() }

    val isInMultiWindow: Boolean
        get() {
            return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                isInMultiWindowMode
            } else {
                false
            }
        }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LanguageUtils.setConfiguration(newBase))
    }

    protected abstract fun getViewBinding(): VB

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        if (AppConst.menuViewNames.contains(name) && parent?.parent is FrameLayout) {
            (parent.parent as View).setBackgroundColor(backgroundColor)
        }
        return super.onCreateView(parent, name, context, attrs)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        window.decorView.disableAutoFill()
        initTheme()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupSystemBar()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            findViewById<TitleBar>(R.id.title_bar)
                ?.onMultiWindowModeChanged(isInMultiWindowMode, fullScreen)
        }
        onActivityCreated(savedInstanceState)
        observeLiveBus()
    }

    override fun onMultiWindowModeChanged(isInMultiWindowMode: Boolean, newConfig: Configuration?) {
        super.onMultiWindowModeChanged(isInMultiWindowMode, newConfig)
        findViewById<TitleBar>(R.id.title_bar)
            ?.onMultiWindowModeChanged(isInMultiWindowMode, fullScreen)
        setupSystemBar()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        findViewById<TitleBar>(R.id.title_bar)
            ?.onMultiWindowModeChanged(isInMultiWindow, fullScreen)
        setupSystemBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }

    abstract fun onActivityCreated(savedInstanceState: Bundle?)

    final override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return menu?.let {
            val bool = onCompatCreateOptionsMenu(it)
            it.applyTint(this, toolBarTheme)
            bool
        } ?: super.onCreateOptionsMenu(menu)
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        menu.applyOpenTint(this)
        return super.onMenuOpened(featureId, menu)
    }

    open fun onCompatCreateOptionsMenu(menu: Menu) = super.onCreateOptionsMenu(menu)

    final override fun onOptionsItemSelected(item: MenuItem): Boolean {
        item.let {
            if (it.itemId == android.R.id.home) {
                supportFinishAfterTransition()
                return true
            }
        }
        return onCompatOptionsItemSelected(item)
    }

    open fun onCompatOptionsItemSelected(item: MenuItem) = super.onOptionsItemSelected(item)

    private fun initTheme() {
        when (theme) {
            Theme.Transparent -> setTheme(R.style.AppTheme_Transparent)
            Theme.Dark -> {
                setTheme(R.style.AppTheme_Dark)
                ATH.applyBackgroundTint(window.decorView)
            }
            Theme.Light -> {
                setTheme(R.style.AppTheme_Light)
                ATH.applyBackgroundTint(window.decorView)
            }
            else -> {
                if (ColorUtils.isColorLight(primaryColor)) {
                    setTheme(R.style.AppTheme_Light)
                } else {
                    setTheme(R.style.AppTheme_Dark)
                }
                ATH.applyBackgroundTint(window.decorView)
            }
        }
    }

    private fun setupSystemBar() {
        if (fullScreen && !isInMultiWindow) {
            window.clearFlags(
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                        or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
            )
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        ATH.setStatusBarColorAuto(this, fullScreen)
        if (toolBarTheme == Theme.Dark) {
            ATH.setLightStatusBar(window, false)
        } else if (toolBarTheme == Theme.Light) {
            ATH.setLightStatusBar(window, true)
        }
        upNavigationBarColor()
    }

    open fun upNavigationBarColor() {
        ATH.setNavigationBarColorAuto(this)
    }

    open fun observeLiveBus() {
    }

    override fun finish() {
        currentFocus?.hideSoftInput()
        super.finish()
    }
}