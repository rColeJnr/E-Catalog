package com.rick.ecatalogs.book.screen_book.common

import dagger.hilt.android.AndroidEntryPoint
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.transition.MaterialSharedAxis
import com.rick.ecatalogs.book.screen_book.common.databinding.BookScreenBookCommonReaderFragmentBinding
import org.json.JSONObject
import com.rick.ecatalogs.data.ui_design.R as DesignR

/**
 * Reads a Project Gutenberg book (its HTML, or plain text) in the app's own look: theme colours,
 * a serif font, comfortable margins, adjustable text size, and the reading position remembered
 * per book. Pages are served from the WebView cache when possible, so reopening a book doesn't
 * download it again.
 */
@AndroidEntryPoint
class ReaderFragment : Fragment() {

    private var _binding: BookScreenBookCommonReaderFragmentBinding? = null
    private val binding get() = _binding!!

    private val prefs by lazy {
        requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    private var bookId = 0
    private var link = ""
    private var bookTitle = ""
    private var loadFailed = false

    /** Position to return to once the page has loaded, as a fraction of the book (0..1). */
    private var pendingProgress = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val duration = resources.getInteger(DesignR.integer.data_ui_design_motion_duration_medium)
            .toLong()
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).setDuration(duration)
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).setDuration(duration)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = BookScreenBookCommonReaderFragmentBinding.inflate(inflater, container, false)
        val args = ReaderFragmentArgs.fromBundle(requireArguments())
        bookId = args.book
        link = args.link
        bookTitle = args.title
        binding.readerToolbar.title = args.title
        return binding.root
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.readerToolbar.apply {
            setNavigationIcon(DesignR.drawable.data_ui_design_back_icon)
            setNavigationOnClickListener { findNavController().navigateUp() }
            inflateMenu(R.menu.book_screen_book_common_reader_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.reader_text_smaller -> changeTextZoom(-TEXT_ZOOM_STEP)
                    R.id.reader_text_larger -> changeTextZoom(TEXT_ZOOM_STEP)
                    R.id.reader_adaptations -> ScreenAdaptationsSheet.newInstance(bookTitle)
                        .show(childFragmentManager, "adaptations")
                    else -> return@setOnMenuItemClickListener false
                }
                true
            }
        }
        binding.readerError.setOnClickListener { load() }

        binding.readerWebView.apply {
            // JavaScript is only used by the app itself, to apply the reading style and to
            // restore the position; Gutenberg pages don't need it.
            settings.javaScriptEnabled = true
            settings.textZoom = prefs.getInt(KEY_TEXT_ZOOM, DEFAULT_TEXT_ZOOM)
            settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            settings.builtInZoomControls = false
            setBackgroundColor(color(DesignR.color.data_ui_design_color_surface))
            webViewClient = ReaderClient()
            setOnScrollChangeListener { _, _, scrollY, _, _ -> showProgress(progressAt(scrollY)) }
        }

        pendingProgress = prefs.getFloat(progressKey(), 0f)
        showProgress(pendingProgress)
        load()
    }

    private fun load() {
        loadFailed = false
        binding.readerError.isVisible = false
        binding.readerLoading.isVisible = true
        binding.readerWebView.visibility = View.INVISIBLE
        binding.readerWebView.loadUrl(link)
    }

    private inner class ReaderClient : WebViewClient() {

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            loadFailed = false
        }

        override fun onPageFinished(view: WebView, url: String?) {
            if (_binding == null || loadFailed) return
            view.evaluateJavascript(readerStyleScript()) {
                if (_binding == null) return@evaluateJavascript
                scrollTo(pendingProgress) {
                    binding.readerLoading.isVisible = false
                    binding.readerWebView.alpha = 0f
                    binding.readerWebView.visibility = View.VISIBLE
                    binding.readerWebView.animate().alpha(1f).setDuration(FADE_MILLIS).start()
                }
            }
        }

        override fun onReceivedError(
            view: WebView,
            request: WebResourceRequest,
            error: WebResourceError,
        ) {
            if (request.isForMainFrame) showError()
        }

        override fun onReceivedHttpError(
            view: WebView,
            request: WebResourceRequest,
            errorResponse: WebResourceResponse,
        ) {
            if (request.isForMainFrame && errorResponse.statusCode >= 400) showError()
        }

        /** Links inside the book (chapters, notes) stay here; anything else opens the browser. */
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            val target = request.url
            val book = Uri.parse(link)
            if (target.host == book.host && target.path == book.path) return false
            try {
                startActivity(Intent(Intent.ACTION_VIEW, target))
            } catch (e: ActivityNotFoundException) {
                // No browser installed; stay on the book
            }
            return true
        }
    }

    private fun showError() {
        if (_binding == null) return
        loadFailed = true
        binding.readerLoading.isVisible = false
        binding.readerWebView.visibility = View.INVISIBLE
        binding.readerError.isVisible = true
    }

    /** A/A buttons: resize the text and keep the reader at the same place in the book. */
    private fun changeTextZoom(delta: Int) {
        val webView = binding.readerWebView
        val zoom = (webView.settings.textZoom + delta).coerceIn(MIN_TEXT_ZOOM, MAX_TEXT_ZOOM)
        if (zoom == webView.settings.textZoom) return
        val progress = progressAt(webView.scrollY)
        webView.settings.textZoom = zoom
        prefs.edit { putInt(KEY_TEXT_ZOOM, zoom) }
        // Let the text reflow before scrolling back to the same spot
        webView.postDelayed({ if (_binding != null) scrollTo(progress) {} }, REFLOW_MILLIS)
    }

    private fun scrollTo(progress: Float, then: () -> Unit) {
        val script = "(function(){var e=document.scrollingElement||document.documentElement;" +
            "window.scrollTo(0,$progress*(e.scrollHeight-window.innerHeight));})()"
        binding.readerWebView.evaluateJavascript(script) { then() }
    }

    private fun progressAt(scrollY: Int): Float {
        val webView = _binding?.readerWebView ?: return 0f
        val contentPx = webView.contentHeight * resources.displayMetrics.density
        val scrollable = contentPx - webView.height
        return if (scrollable <= 0f) 0f else (scrollY / scrollable).coerceIn(0f, 1f)
    }

    private fun showProgress(progress: Float) {
        _binding?.readerProgress?.setProgressCompat((progress * PROGRESS_MAX).toInt(), false)
    }

    override fun onPause() {
        super.onPause()
        val webView = _binding?.readerWebView ?: return
        // Only remember positions of a book that actually loaded
        if (webView.isVisible && !loadFailed) {
            val progress = progressAt(webView.scrollY)
            pendingProgress = progress
            prefs.edit { putFloat(progressKey(), progress) }
        }
    }

    override fun onDestroyView() {
        binding.readerWebView.apply {
            stopLoading()
            webViewClient = WebViewClient()
            destroy()
        }
        super.onDestroyView()
        _binding = null
    }

    private fun progressKey() = "$KEY_PROGRESS_PREFIX$bookId"

    private fun color(id: Int) = ContextCompat.getColor(requireContext(), id)

    private fun Int.css() = String.format("#%06X", this and 0xFFFFFF)

    /** Overrides the book's own styling with the app's theme and a comfortable reading layout. */
    private fun readerStyleScript(): String {
        val background = color(DesignR.color.data_ui_design_color_surface).css()
        val text = color(DesignR.color.data_ui_design_black).css()
        val accent = color(DesignR.color.data_ui_design_color_buttons).css()
        val rule = color(DesignR.color.data_ui_design_outline).css()
        val css = """
            html, body { background: $background !important; color: $text !important; }
            body {
                font-family: Georgia, 'Noto Serif', 'Droid Serif', serif;
                line-height: 1.65; max-width: 40em; margin: 0 auto !important;
                padding: 12px 20px 64px !important; overflow-wrap: break-word;
            }
            body * { background-color: transparent !important; color: inherit !important; }
            a, a * { color: $accent !important; }
            h1, h2, h3, h4 { line-height: 1.3; }
            img { max-width: 100% !important; height: auto !important; }
            pre { white-space: pre-wrap !important; font-family: inherit; }
            table { max-width: 100%; }
            hr { border: 0; border-top: 1px solid $rule; }
        """.trimIndent()
        return "(function(){var d=document,h=d.head||d.documentElement;" +
            "var s=d.createElement('style');s.textContent=${JSONObject.quote(css)};h.appendChild(s);" +
            "var m=d.createElement('meta');m.name='viewport';" +
            "m.content='width=device-width, initial-scale=1';h.appendChild(m);})()"
    }
}

/**
 * Deep link to [ReaderFragment]. Any book screen can open it: the book common nav graph is
 * included in all of them.
 */
fun gutenbergReaderUri(bookId: Int, title: String, link: String): Uri = Uri.Builder()
    .scheme("com.rick.ecs")
    .authority("gutenberg_reader")
    .appendPath(bookId.toString())
    .appendQueryParameter("link", link)
    .appendQueryParameter("title", title)
    .build()

private const val PREFS = "gutenberg_reader"
private const val KEY_TEXT_ZOOM = "text_zoom"
private const val KEY_PROGRESS_PREFIX = "progress_"
private const val DEFAULT_TEXT_ZOOM = 110
private const val MIN_TEXT_ZOOM = 70
private const val MAX_TEXT_ZOOM = 200
private const val TEXT_ZOOM_STEP = 10
private const val PROGRESS_MAX = 1000
private const val REFLOW_MILLIS = 300L
private const val FADE_MILLIS = 200L
