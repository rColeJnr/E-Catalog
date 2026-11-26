package com.rick.ecatalogs.data.ui_components.common

import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.rick.ecatalogs.data.translation.TextTranslator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * The "Show translation" / "Show original" action of an XML screen or list item: tapping
 * [button] swaps the text of [targets] between the original and the phone's language.
 *
 * Call [bind] with the original texts (one per target) whenever they're shown, including from
 * `onBindViewHolder`: a different text resets the toggle to the original, so a recycled card
 * never shows the previous item's translation. The button hides itself when there's nothing
 * to translate (blank text, or a phone in English). [onTextChanged] runs after each swap, for
 * screens that measure the text (e.g. to show "Read more").
 */
class TranslationToggle(
    private val button: TextView,
    private val targets: List<TextView>,
    private val translator: TextTranslator,
    private val onTextChanged: () -> Unit = {},
) {
    constructor(
        button: TextView,
        target: TextView,
        translator: TextTranslator,
        onTextChanged: () -> Unit = {},
    ) : this(button, listOf(target), translator, onTextChanged)

    private var originals: List<String> = emptyList()
    private var translations: List<String>? = null
    private var showingTranslation = false
    private var job: Job? = null

    init {
        button.setOnClickListener { toggle() }
    }

    /** Shows [texts] (one per target, null for none) and resets the toggle if they're new. */
    fun bind(vararg texts: String?) {
        val shown = texts.map { it.orEmpty() }
        require(shown.size == targets.size) { "${shown.size} texts for ${targets.size} views" }
        if (shown != originals) {
            job?.cancel()
            originals = shown
            translations = null
            showingTranslation = false
        }
        targets.forEach { it.animate().cancel(); it.alpha = 1f }
        setTexts(if (showingTranslation) translations ?: originals else originals)
        setBusy(job?.isActive == true) // same text rebound mid-request: still translating
        val offered = translator.isAvailable && originals.any { it.isNotBlank() }
        button.visibility = if (offered) View.VISIBLE else View.GONE
    }

    private fun toggle() {
        if (job?.isActive == true) return
        val translated = translations
        when {
            showingTranslation -> crossFade(originals, translated = false)
            translated != null -> crossFade(translated, translated = true)
            else -> {
                val scope = button.findViewTreeLifecycleOwner()?.lifecycleScope ?: return
                val requested = originals
                setBusy(true)
                job = scope.launch {
                    val result = translator.translate(requested)
                    if (requested != originals) return@launch // rebound to other text meanwhile
                    setBusy(false)
                    if (result == null) {
                        Toast.makeText(
                            button.context,
                            R.string.data_ui_components_common_translation_failed,
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        translations = result
                        crossFade(result, translated = true)
                    }
                }
            }
        }
    }

    private fun crossFade(texts: List<String>, translated: Boolean) {
        val fadingFrom = originals
        showingTranslation = translated
        setLabel()
        targets.forEachIndexed { index, target ->
            target.animate().alpha(0f).setDuration(FADE_MILLIS).withEndAction {
                if (fadingFrom != originals) return@withEndAction // rebound meanwhile
                target.text = texts[index]
                target.animate().alpha(1f).setDuration(FADE_MILLIS).start()
                if (index == targets.lastIndex) onTextChanged()
            }.start()
        }
    }

    private fun setTexts(texts: List<String>) {
        targets.forEachIndexed { index, target -> target.text = texts[index] }
        setLabel()
        onTextChanged()
    }

    private fun setBusy(busy: Boolean) {
        button.isEnabled = !busy
        button.alpha = if (busy) BUSY_ALPHA else 1f
        if (busy) button.setText(R.string.data_ui_components_common_translating) else setLabel()
    }

    private fun setLabel() {
        button.setText(
            if (showingTranslation) R.string.data_ui_components_common_show_original
            else R.string.data_ui_components_common_show_translation,
        )
    }
}

private const val FADE_MILLIS = 150L
private const val BUSY_ALPHA = 0.5f
