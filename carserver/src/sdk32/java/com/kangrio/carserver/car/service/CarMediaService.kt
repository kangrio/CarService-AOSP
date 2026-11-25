package com.kangrio.carserver.car.service

import android.car.media.ICarMedia
import android.car.media.ICarMediaSourceListener
import android.content.ComponentName
import android.os.IBinder
import android.os.RemoteException
import com.kangrio.carserver.util.LogUtils
import com.kangrio.carserver.util.TAG
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

class CarMediaService : ICarMedia.Stub() {
    @Volatile
    private var lastMediaSource: ComponentName? = null
//    private var mediaSources: ConcurrentHashMap<Int, ComponentName>? = null
    private val iCarMediaSourceListeners = CopyOnWriteArrayList<ICarMediaSourceListener>()
    private val deathRecipients = ConcurrentHashMap<IBinder, IBinder.DeathRecipient>()
    override fun getLastMediaSources(p0: Int): List<ComponentName?>? {
        return lastMediaSource?.let {
            listOf(it)
        }
    }

    override fun getMediaSource(id: Int): ComponentName? {
        return lastMediaSource
    }

    override fun isIndependentPlaybackConfig(): Boolean {
        return false
    }

    override fun registerMediaSourceListener(
        listener: ICarMediaSourceListener,
        id: Int
    ) {
        LogUtils.i(TAG, "registerMediaSourceListener=$listener")
        val binder = listener.asBinder()
        if (deathRecipients.containsKey(binder)) return

        val recipient = IBinder.DeathRecipient {
            LogUtils.i(TAG, "MediaSourceListener binder died")
            iCarMediaSourceListeners.remove(listener)
            deathRecipients.remove(binder)
        }

        try {
            binder.linkToDeath(recipient, 0)
            deathRecipients[binder] = recipient
            iCarMediaSourceListeners.addIfAbsent(listener)
        } catch (e: RemoteException) {
            LogUtils.e(TAG, "Failed to link listener death", e)
        }
    }

    override fun setIndependentPlaybackConfig(p0: Boolean) {
        LogUtils.i(TAG, "setIndependentPlaybackConfig p0=$p0")
    }

    override fun unregisterMediaSourceListener(
        listener: ICarMediaSourceListener,
        id: Int
    ) {
        LogUtils.i(TAG, "unregisterMediaSourceListener=$listener")
        val binder = listener.asBinder()
        iCarMediaSourceListeners.remove(listener)
        deathRecipients.remove(binder)?.let {
            binder.unlinkToDeath(it, 0)
        }
    }

    override fun setMediaSource(component: ComponentName, id: Int) {
        LogUtils.i(TAG, "setMediaSource component=$component")
        lastMediaSource = component
        iCarMediaSourceListeners.forEach { listener ->
            try {
                listener.onMediaSourceChanged(component)
            } catch (e: RemoteException) {
                LogUtils.e(TAG, "Failed to notify listener", e)
                unregisterMediaSourceListener(listener, id)
            }
        }
    }
}