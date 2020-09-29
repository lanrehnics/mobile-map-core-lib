package com.kobo.mobile_map_core.mobile_map_core.utils.client


import android.util.ArrayMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import okhttp3.*

import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody


class LiteHttp private constructor() {

    private val executor: Executor


    init {
        val executor = ThreadPoolExecutor(5, 5,
                5L, TimeUnit.SECONDS,
                LinkedBlockingQueue()
        ) { r -> Thread(r, LOG_TAG + "Thread") }
        executor.allowCoreThreadTimeOut(true)
        this.executor = executor
    }

    private fun addHeaders(builder: Request.Builder, headers: ArrayMap<String, String>?) {

        headers?.let {
            for (key in it.keys) {
                builder.addHeader(key, it[key]!!)
            }
        }

    }

    suspend fun get(serviceUrl: String): String? {
        return get(serviceUrl, null, null)
    }

    suspend fun get(serviceUrl: String, params: ArrayMap<String, Any>): String? {
        return get(serviceUrl, null, params)
    }

    suspend fun get(serviceUrl: String, headers: ArrayMap<String, String>?, params: ArrayMap<String, Any>?): String? {
        val builder = Request.Builder()
        if (headers != null)
            addHeaders(builder, headers)
        builder.url(generateUrlParams(serviceUrl, params))
        return execute(builder)
    }

    suspend fun getWithHeader(serviceUrl: String, headers: ArrayMap<String, String>): String? {
        val builder = Request.Builder()
        if (headers != null)
            addHeaders(builder, headers)
        builder.url(generateUrlParams(serviceUrl, null))
        return execute(builder)
    }

    suspend fun post(serviceUrl: String, params: ArrayMap<String, Any>): String? {
        return post(serviceUrl, null, params)
    }

    suspend fun post(serviceUrl: String, headers: ArrayMap<String, String>?, params: ArrayMap<String, Any>): String? {
        val builder = Request.Builder()
        if (headers != null)
            addHeaders(builder, headers)
        val urls = StringBuffer()
        if (defaultBaseUrl.length > 0)
            urls.append(defaultBaseUrl)
        urls.append(serviceUrl)
        builder.url(urls.toString())
        builder.post(generateRequestBody(params))
        return execute(builder)
    }

    suspend fun put(serviceUrl: String, params: ArrayMap<String, Any>): String? {
        return put(serviceUrl, null, params)
    }

    suspend fun put(serviceUrl: String, headers: ArrayMap<String, String>?, params: ArrayMap<String, Any>?): String? {
        val builder = Request.Builder()
        if (headers != null)
            addHeaders(builder, headers)
        val urls = StringBuffer()
        if (defaultBaseUrl.length > 0)
            urls.append(defaultBaseUrl)
        urls.append(serviceUrl)
        builder.url(urls.toString())
        builder.put(generateRequestBody(params))
        return execute(builder)
    }

    suspend fun delete(serviceUrl: String, params: ArrayMap<String, Any>): String? {
        return delete(serviceUrl, null, params)
    }

    suspend fun delete(serviceUrl: String, headers: ArrayMap<String, String>?, params: ArrayMap<String, Any>): String? {
        val builder = Request.Builder()
        if (headers != null)
            addHeaders(builder, headers)
        val urls = StringBuffer()
        if (defaultBaseUrl.length > 0)
            urls.append(defaultBaseUrl)
        urls.append(serviceUrl)
        builder.url(urls.toString())
        builder.delete(generateRequestBody(params))
        return execute(builder)
    }

    suspend fun postMultipart(serviceUrl: String, files: ArrayMap<String, File>) {
        postMultipart(serviceUrl, null, null, files)
    }

    suspend fun postMultipart(serviceUrl: String, params: ArrayMap<String, Any>, files: ArrayMap<String, File>) {
        postMultipart(serviceUrl, null, params, files)
    }

    suspend fun postMultipart(serviceUrl: String, headers: ArrayMap<String, String>?, params: ArrayMap<String, Any>?, files: ArrayMap<String, File>): String? {
        val builder = Request.Builder()
        if (headers != null)
            addHeaders(builder, headers)
        val urls = StringBuffer()
        if (defaultBaseUrl.length > 0)
            urls.append(defaultBaseUrl)
        urls.append(serviceUrl)
        builder.url(urls.toString())
        builder.post(generateMultipartBody(params, files))
        return execute(builder)
    }

    private suspend fun execute(builder: Request.Builder): String? {
        var responseData: String? = null
        val client = OkHttpClient.Builder().connectTimeout(2, TimeUnit.MINUTES).writeTimeout(2, TimeUnit.MINUTES).readTimeout(2, TimeUnit.MINUTES).build()

        return GlobalScope.async(Dispatchers.IO) {
            System.setProperty("http.keepAlive", "false")
            val result: Response = client.newCall(builder.build()).execute()

            if(result.code == 200 || result.code == 201) {
                result.body!!.string()
            }else{
                throw  Throwable(message = result.message)
            }
        }.await()
    }

    private fun generateUrlParams(serviceUrl: String, params: ArrayMap<String, Any>?): String {
        val urls = StringBuffer()
        if (defaultBaseUrl.length > 0)
            urls.append(defaultBaseUrl)
        urls.append(serviceUrl)
        if (params != null) {
            var i = 0
            for (key in params.keys) {
                if (i == 0) {
                    urls.append("?" + key + "=" + params[key])
                } else {
                    urls.append("&" + key + "=" + params[key])
                }
                i++
            }
        }
        return urls.toString()
    }

    private fun generateRequestBody(params: ArrayMap<String, Any>?): RequestBody {
        val jsonObj = JSONObject()
        if (params != null) {
            for (key in params.keys) {
                try {
                    jsonObj.put(key, params[key])
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

            }
        }
        return jsonObj.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
    }

    private fun generateMultipartBody(params: ArrayMap<String, Any>?, files: ArrayMap<String, File>?): RequestBody {
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        if (params != null) {
            for (key in params.keys) {
                builder.addFormDataPart(key, params[key].toString())
            }
        }
        if (files != null) {
            for (key in files.keys) {
                builder.addFormDataPart(key, key, files[key]!!.asRequestBody("image/png".toMediaTypeOrNull()))
            }
        }
        return builder.build()
    }

    companion object {
        private val LOG_TAG = "LiteHttp"
        var defaultBaseUrl = ""
        private val lockObject = Any()
        private var restClientHelper: LiteHttp? = null

        val instance: LiteHttp?
            get() {
                if (restClientHelper == null)
                    synchronized(lockObject) {
                        if (restClientHelper == null)
                            restClientHelper = LiteHttp()
                    }
                return restClientHelper
            }
    }
}
