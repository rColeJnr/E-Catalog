package com.rick.ecatalogs.data.ui_components.common

import android.app.Application
import android.net.Uri
import coil.Coil
import coil.ImageLoader
import coil.decode.DataSource
import coil.decode.ImageSource
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.fetch.SourceResult
import coil.request.Options
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runInterruptible
import okio.FileSystem
import okio.Path.Companion.toOkioPath
import java.io.File
import java.util.concurrent.ExecutionException

/**
 * One disk cache for every image in the app, kept regardless of the servers' cache headers.
 *
 * XML screens load images with Glide and Compose screens with Coil. On their own they keep two
 * caches, so a poster shown in both was downloaded twice; and Coil follows the CDNs' cache
 * headers (TMDB and MyAnimeList allow a few hours to a week), downloading images again once
 * they "expire". Posters never change at a given URL, so here Glide's cache, which ignores those
 * headers, also serves Coil.
 *
 * Call once from `Application.onCreate`, before anything loads an image.
 */
fun installImageCaching(application: Application) {
    Glide.init(
        application,
        GlideBuilder().setDiskCache(
            // Same folder as Glide's default, so what's already cached is kept
            InternalCacheDiskCacheFactory(application, GLIDE_CACHE_FOLDER, DISK_CACHE_BYTES),
        ),
    )
    Coil.setImageLoader {
        ImageLoader.Builder(application)
            .components { add(GlideCacheFetcher.Factory()) }
            .diskCache(null) // on disk in Glide's cache (above)
            .build()
    }
    // Coil's own cache isn't used any more
    Thread { File(application.cacheDir, OLD_COIL_CACHE_FOLDER).deleteRecursively() }.start()
}

/** Coil fetches http(s) images as files from Glide's disk cache, which downloads them once. */
private class GlideCacheFetcher(
    private val url: String,
    private val options: Options,
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        val glide = Glide.with(options.context.applicationContext)
        val download = glide.asFile().load(url).submit()
        val file = try {
            runInterruptible(Dispatchers.IO) { download.get() }
        } catch (e: ExecutionException) {
            throw e.cause ?: e // e.g. the GlideException for an unreachable image
        } finally {
            glide.clear(download)
        }
        return SourceResult(
            source = ImageSource(file = file.toOkioPath(), fileSystem = FileSystem.SYSTEM),
            mimeType = null, // sniffed by the decoder
            dataSource = DataSource.DISK,
        )
    }

    class Factory : Fetcher.Factory<Uri> {
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? =
            if (data.scheme == "http" || data.scheme == "https") {
                GlideCacheFetcher(data.toString(), options)
            } else {
                null // resources, files, content URIs: Coil's own fetchers
            }
    }
}

private const val GLIDE_CACHE_FOLDER = "image_manager_disk_cache"
private const val OLD_COIL_CACHE_FOLDER = "image_cache"
private const val DISK_CACHE_BYTES = 512L * 1024 * 1024
