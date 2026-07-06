package com.myAllVideoBrowser.util.fragment

import androidx.fragment.app.Fragment
import com.myAllVideoBrowser.ui.main.home.browser.BrowserFragment
import com.myAllVideoBrowser.ui.main.home.browser.homeTab.BrowserHomeFragment
import com.myAllVideoBrowser.ui.main.home.browser.webTab.WebTabFragment
import com.myAllVideoBrowser.ui.main.home.browser.detectedVideos.DetectedVideosTabFragment
import com.myAllVideoBrowser.ui.main.progress.ProgressFragment
import javax.inject.Inject

interface FragmentFactory {
    fun createBrowserFragment(): Fragment
    fun createProgressFragment(): Fragment

    fun createBrowserHomeFragment(): Fragment

    fun createWebTabFragment(): Fragment

    fun createDetectedVideosTabFragment(): Fragment
}

class FragmentFactoryImpl @Inject constructor() : FragmentFactory {
    override fun createBrowserFragment() = BrowserFragment.newInstance()

    override fun createProgressFragment() = ProgressFragment.newInstance()

    override fun createBrowserHomeFragment() = BrowserHomeFragment.newInstance()

    override fun createWebTabFragment() = WebTabFragment.newInstance()

    override fun createDetectedVideosTabFragment() = DetectedVideosTabFragment.newInstance()
}