package com.myAllVideoBrowser.ui.main.home.browser.adblocker

import javax.inject.Inject

class AdBlockEngine @Inject constructor() {
    fun isAdUrl(url: String): Boolean = false
    fun isAd(url: String, tabText: String, resourceType: String): Boolean = false
    fun enable(enable: Boolean) {}
    fun updateLists() {}
}