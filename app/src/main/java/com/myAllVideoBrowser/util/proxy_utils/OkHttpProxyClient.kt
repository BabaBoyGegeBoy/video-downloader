package com.myAllVideoBrowser.util.proxy_utils

import okhttp3.OkHttpClient
import javax.inject.Inject

class OkHttpProxyClient @Inject constructor(val client: OkHttpClient) {
    fun getProxyOkHttpClient(): OkHttpClient = client
}