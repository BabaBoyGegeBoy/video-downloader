package com.myAllVideoBrowser.util.proxy_utils

import com.myAllVideoBrowser.data.local.model.Proxy
import javax.inject.Inject

class CustomProxyController @Inject constructor() {
    fun getProxyCredentials(): Pair<String, String> = Pair("", "")
    fun getCurrentRunningProxy(): Proxy = Proxy.noProxy()
}