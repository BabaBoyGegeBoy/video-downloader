package com.myAllVideoBrowser.di.module

import com.myAllVideoBrowser.di.ActivityScoped
import com.myAllVideoBrowser.ui.main.home.MainActivity
import com.myAllVideoBrowser.di.module.activity.MainModule
import com.myAllVideoBrowser.ui.main.splash.SplashActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector
    internal abstract fun bindSplashActivity(): SplashActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [MainModule::class])
    internal abstract fun bindMainActivity(): MainActivity
}