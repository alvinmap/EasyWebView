package com.lzx.easyweb.cache


import android.content.Context
import com.lzx.easyweb.utils.CacheUtils
import java.io.File

class CacheConfig {

    companion object {
        val instance: CacheConfig by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            CacheConfig()
        }
    }

    var context: Context? = null
    var connectTimeout = 5000
    var readTimeout = 15000
    var cacheSize = 100 * 1024 * 1024.toLong()
    var version: Long = 0
    var memCacheSize: Int = CacheUtils.getMemorySize()
    var cacheFileLists = mutableListOf<String>()
    var ignoreUrl = mutableListOf<String>()
    var defaultCachePath = context?.cacheDir?.absolutePath + File.separator + "WebViewCache"

    var cacheFile: File? = null

    fun getCacheDirectory(destFileDir: String?): File? {
        if (cacheFile == null && !destFileDir.isNullOrEmpty()) {
            cacheFile = File(destFileDir)
            if (cacheFile?.exists() == false) {
                cacheFile?.mkdirs()
            }
        }
        if (cacheFile == null) {
            cacheFile = context?.getExternalFilesDir(null)
            if (cacheFile == null) {
                cacheFile = context?.filesDir
            }
        }
        return cacheFile
    }


    fun isCacheFile(extension: String): Boolean {
        if (cacheFileLists.isEmpty()) {
            return extension.isNotEmpty()
        }
        return cacheFileLists.contains(extension)
    }

    fun isIgnoreUrl(url: String): Boolean {
        for (value in ignoreUrl) {
            return if (value.startsWith(url)) {
                true
            } else {
                value.contains(url)
            }
        }
        return false
    }

    fun addCacheFile(extension: String) = apply {
        if (!cacheFileLists.contains(extension)) {
            cacheFileLists.add(extension)
        }
    }

    fun addIgnoreUrl(url: String) = apply {
        if (!ignoreUrl.contains(url)) {
            ignoreUrl.add(url)
        }
    }
}