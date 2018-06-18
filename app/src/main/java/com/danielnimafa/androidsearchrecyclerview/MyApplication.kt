package com.danielnimafa.androidsearchrecyclerview

import android.app.Application
import android.text.TextUtils
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley


/**
 * Created by danielnimafa on 16/03/18.
 */
class MyApplication : Application() {

    companion object {
        var instance: MyApplication by DelegatesExt.notNullSingleValue()
        val TAG = MyApplication::class.java.getSimpleName()
    }

    var mRequestQueue: RequestQueue? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    fun <T> addToRequestQueue(req: Request<T>, tag: String) {
        // set the default tag if tag is empty
        req.tag = if (TextUtils.isEmpty(tag)) TAG else tag
        getRequestQueue()?.add<T>(req)
    }

    fun <T> addToRequestQueue(req: Request<T>) {
        req.tag = TAG
        getRequestQueue()?.add<T>(req)
    }

    fun cancelPendingRequests(tag: Any) {
        mRequestQueue?.cancelAll(tag)
    }

    fun getRequestQueue(): RequestQueue? {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(instance)
        } else println("Request is null")
        return mRequestQueue
    }
}