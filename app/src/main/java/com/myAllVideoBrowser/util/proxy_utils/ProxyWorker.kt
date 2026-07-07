package com.myAllVideoBrowser.util.proxy_utils

import com.myAllVideoBrowser.util.SharedPrefHelper

class ProxyWorker {
    lateinit var sharedPrefHelper: SharedPrefHelper
    companion object {
        const val WORK_NAME = "ProxyWorker"
    }
}