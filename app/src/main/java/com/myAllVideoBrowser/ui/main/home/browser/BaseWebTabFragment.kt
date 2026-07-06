package com.myAllVideoBrowser.ui.main.home.browser

import android.view.Gravity
import android.view.View
import androidx.appcompat.widget.PopupMenu
import com.myAllVideoBrowser.R
import com.myAllVideoBrowser.ui.main.base.BaseFragment
import com.myAllVideoBrowser.ui.main.home.MainActivity
import javax.inject.Inject


abstract class BaseWebTabFragment : BaseFragment() {
    @Inject
    lateinit var mainActivity: MainActivity

    private var popupMenu: PopupMenu? = null

    abstract fun shareWebLink()

    abstract fun bookmarkCurrentUrl()

    fun buildWebTabMenu(browserMenu: View, isHomeTab: Boolean) {
        if (popupMenu == null) {
            popupMenu = buildPopupMenu(browserMenu)
            popupMenu!!.setForceShowIcon(true)
        }
    }

    fun showPopupMenu() {
        popupMenu?.show()
    }

    open fun setIsDesktop(isDesktop: Boolean) {
        mainActivity.settingsViewModel.setIsDesktopMode(isDesktop)
    }

    private fun buildPopupMenu(view: View): PopupMenu {
        val popupMenu = PopupMenu(requireContext(), view)

        popupMenu.gravity = Gravity.END
        popupMenu.menuInflater.inflate(R.menu.menu_browser, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.download_manager -> {
                    navigateToDownloads()
                    true
                }

                else -> false
            }
        }

        return popupMenu
    }

    private fun navigateToDownloads() {
        mainActivity.mainViewModel.currentItem.set(1)
    }

    override fun onDestroyView() {
        popupMenu?.dismiss()
        popupMenu = null
        super.onDestroyView()
    }
}
