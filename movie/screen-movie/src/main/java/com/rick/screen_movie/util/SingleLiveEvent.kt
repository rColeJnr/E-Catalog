package com.rick.screen_movie.util

import android.util.Log
import androidx.annotation.MainThread
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A lifecycle-aware observable that sends only new updates after subscription, used for events like
 * navigation and Snackbar messages.
 */
class SingleLiveEvent<R> : MutableLiveData<R>() {
    private val pending = AtomicBoolean(false)

    @MainThread
    override fun observe(owner: LifecycleOwner, observer: Observer<in R>) {
        if (hasActiveObservers()) {
            Log.w(TAG, "Multiple observers registered but only one will be notified of changes.")
        }

        // Observe the internal MutableLiveData
        super.observe(owner, Observer { r ->
            if (pending.compareAndSet(true, false)) {
                observer.onChanged(r)
            }
        })
    }

    @MainThread
    override fun setValue(value: R) {
        pending.set(true)
        super.setValue(value)
    }

    companion object {
        private val TAG = "SingleLiveEvent"
    }
}