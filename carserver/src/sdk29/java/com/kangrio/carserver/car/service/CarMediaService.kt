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
    private var mediaSource: ComponentName? = null
    private val iCarMediaSourceListeners = CopyOnWriteArrayList<ICarMediaSourceListener>()
    private val deathRecipients = ConcurrentHashMap<IBinder, IBinder.DeathRecipient>()

    override fun getMediaSource(): ComponentName? {
        return mediaSource
    }

    override fun registerMediaSourceListener(
        listener: ICarMediaSourceListener
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

    override fun unregisterMediaSourceListener(
        listener: ICarMediaSourceListener
    ) {
        LogUtils.i(TAG, "unregisterMediaSourceListener=$listener")
        val binder = listener.asBinder()
        iCarMediaSourceListeners.remove(listener)
        deathRecipients.remove(binder)?.let {
            binder.unlinkToDeath(it, 0)
        }
    }

    override fun setMediaSource(component: ComponentName) {
        LogUtils.i(TAG, "setMediaSource component=$component")
        mediaSource = component
        iCarMediaSourceListeners.forEach { listener ->
            try {
                listener.onMediaSourceChanged(component)
            } catch (e: RemoteException) {
                LogUtils.e(TAG, "Failed to notify listener", e)
                unregisterMediaSourceListener(listener)
            }
        }
    }
}