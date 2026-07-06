package com.myAllVideoBrowser.ui.component.binding

import androidx.databinding.BindingAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.myAllVideoBrowser.R

object BottomNavigationViewBinding {

    @BindingAdapter("app:selectedItemId")
    @JvmStatic
    fun BottomNavigationView.setSelectedItemId(position: Int) {
        selectedItemId = when (position) {
            0 -> R.id.tab_browser
            else -> R.id.tab_progress
        }
    }
}