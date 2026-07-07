package com.myAllVideoBrowser.util.downloaders.generic_downloader

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.myAllVideoBrowser.data.repository.ProgressRepository
import com.myAllVideoBrowser.util.FileUtil
import com.myAllVideoBrowser.util.NotificationsHelper
import com.myAllVideoBrowser.util.SharedPrefHelper
import com.myAllVideoBrowser.util.downloaders.QueueWorker
import com.myAllVideoBrowser.util.downloaders.SystemDownloadManager
import com.myAllVideoBrowser.util.downloaders.generic_downloader.workers.GenericDownloadWorkerWrapper
import javax.inject.Inject


class DaggerWorkerFactory @Inject constructor(
    private val progress: ProgressRepository,
    private val fileUtil: FileUtil,
    private val notificationsHelper: NotificationsHelper,
    private val sharedPrefHelper: SharedPrefHelper,
    private val systemDownloadManager: SystemDownloadManager
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context, workerClassName: String, workerParameters: WorkerParameters
    ): ListenableWorker? {

        val workerKlass = Class.forName(workerClassName).asSubclass(ListenableWorker::class.java)
        val constructor =
            workerKlass.getDeclaredConstructor(Context::class.java, WorkerParameters::class.java)
        val instance = constructor.newInstance(appContext, workerParameters)

        when (instance) {
            is GenericDownloadWorkerWrapper -> {
                instance.sharedPrefHelper = sharedPrefHelper
                instance.progressRepository = progress
                instance.fileUtil = fileUtil
                instance.notificationsHelper = notificationsHelper
                instance.systemDownloadManager = systemDownloadManager
            }

            is QueueWorker -> {
                instance.systemDownloadManager = systemDownloadManager
            }
        }

        return instance
    }
}