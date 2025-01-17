package de.baumann.browser.view.dialog

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.ImageButton
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import de.baumann.browser.Ninja.R
import de.baumann.browser.Ninja.databinding.DialogToggleBinding
import de.baumann.browser.preference.ConfigManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FastToggleDialog(
        private val context: Context,
        private val url: String,
        private val okAction: () -> Unit,
) : KoinComponent {
    private val sp: SharedPreferences by inject()
    private val config: ConfigManager by inject()
    private lateinit var dialog: AlertDialog
    private lateinit var binding: DialogToggleBinding


    fun show() {
        binding = DialogToggleBinding.inflate(LayoutInflater.from(context))
        val builder = AlertDialog.Builder(context, R.style.TouchAreaDialog).apply { setView(binding.root) }

        initViews()
        dialog = builder.create().apply {
            window?.setGravity(if (config.isToolbarOnTop) Gravity.CENTER else Gravity.BOTTOM)
            window?.setBackgroundDrawableResource(R.drawable.background_with_border_margin)
        }
        dialog.show()
    }

    private fun initViews() {
        initButtons()
        initSwitches()
        initToggles()
        initOkCancelBar()
    }

    private fun initToggles() {
        updateViewVisibility(binding.toggleHistoryView, config.saveHistory)
        updateViewVisibility(binding.toggleLocationView, R.string.sp_location)
        updateViewVisibility(binding.toggleMediaContinueView, sp.getBoolean("sp_media_continue", false))
        updateViewVisibility(binding.toggleDesktopView, config.desktop)
        updateViewVisibility(binding.toggleVolumePageTurn, config.volumePageTurn)

        binding.toggleHistory.setOnClickListener {
            config.saveHistory = !config.saveHistory
            updateViewVisibility(binding.toggleHistoryView, config.saveHistory)
            dialog.dismiss()
        }
        binding.toggleLocation.setOnClickListener {
            updateBooleanPref(getString(R.string.sp_location), false)
            updateViewVisibility(binding.toggleLocationView, R.string.sp_location)
            dialog.dismiss()
        }
        binding.toggleVolume.setOnClickListener {
            config.volumePageTurn = !config.volumePageTurn
            updateViewVisibility(binding.toggleVolumePageTurn, config.volumePageTurn)
            dialog.dismiss()
        }
        binding.toggleMediaContinue.setOnClickListener {
            updateBooleanPref("sp_media_continue", false)
            updateViewVisibility(binding.toggleMediaContinueView, sp.getBoolean("sp_media_continue", false))
            dialog.dismiss()
        }
        binding.toggleDesktop.setOnClickListener {
            config.desktop = !config.desktop
            updateViewVisibility(binding.toggleDesktopView, config.desktop)
            dialog.dismiss()
        }
    }

    private fun initButtons() { }

    private fun initSwitches() {
        binding.switchIncognito.isChecked = config.isIncognitoMode
        binding.switchAdBlock.isChecked = sp.getBoolean(getString(R.string.sp_ad_block), true)
        binding.switchCookie.isChecked = sp.getBoolean(getString(R.string.sp_cookies), true)

        binding.switchIncognito.setOnCheckedChangeListener { _, isChecked ->
            config.isIncognitoMode = isChecked
            config.cookies = !config.isIncognitoMode
            config.saveHistory = !config.isIncognitoMode

            okAction.invoke()
            dialog.dismiss()
        }

        binding.switchAdBlock.setOnCheckedChangeListener { _, isChecked ->
            config.adBlock = isChecked
            okAction.invoke()
            dialog.dismiss()
        }
        binding.switchCookie.setOnCheckedChangeListener { _, isChecked ->
            config.cookies = isChecked
            okAction.invoke()
            dialog.dismiss()
        }
    }

    private fun initOkCancelBar() {
        binding.actionCancel.setOnClickListener { dialog.dismiss() }
    }

    private fun getString(resId: Int): String = context.getString(resId)

    private fun setImgButtonResource(imgButton: ImageButton, isEnabled: Boolean) {
        val resId = if (isEnabled) R.drawable.check_green else R.drawable.ic_action_close_red
        imgButton.setImageResource(resId)
    }

    private fun updateBooleanPref(prefKey: String, defaultValue: Boolean = true) =
            sp.edit { putBoolean(prefKey, !sp.getBoolean(prefKey, defaultValue)) }

    private fun updateViewVisibility(view: View, shouldBeVisible: Boolean) {
        view.visibility = if (shouldBeVisible) VISIBLE else INVISIBLE
    }

    private fun updateViewVisibility(view: View, stringResId: Int) {
        val shouldBeVisible = sp.getBoolean(getString(stringResId), false)
        updateViewVisibility(view, shouldBeVisible)
    }
}