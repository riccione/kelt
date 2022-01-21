package com.ignes.kelt.view

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.navigation.findNavController
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.firebase.ui.auth.AuthUI
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.ignes.kelt.BuildConfig
import com.ignes.kelt.R

/**
 * SettingsFragment
 */
class SettingsFragment : PreferenceFragmentCompat() {
    private lateinit var auth: FirebaseAuth
    private lateinit var safeContext: Context

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        // Initialize Firebase Auth
        auth = Firebase.auth

        val versionName: Preference? = findPreference(getString(R.string.settings_version_by_key))
        //https://stackoverflow.com/questions/4616095/how-can-you-get-the-build-version-number-of-your-android-application
        versionName?.summary = BuildConfig.VERSION_NAME

        val feedback: Preference? = findPreference(getString(R.string.settings_feedback_key))
        feedback?.setOnPreferenceClickListener{
            composeEmail("feedback")
            true
        }

        val mSignOut: Preference? = findPreference(getString(R.string.settings_sign_out))
        mSignOut?.setOnPreferenceClickListener {
            signOut()
            true
        }

        val mDeleteAcc: Preference? = findPreference(getString(R.string.settings_delete_acc))
        mDeleteAcc?.setOnPreferenceClickListener {
            //show dialog
            createDialog()
            true
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        safeContext = context
    }

    private fun signOut() {
        //https://firebase.google.com/docs/auth/android/firebaseui#kotlin+ktx_5
        AuthUI.getInstance()
            .signOut(safeContext)
            .addOnCompleteListener {
                view?.let {
                    Snackbar.make(it, "Sign out", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                }
                view?.findNavController()?.navigate(R.id.action_settings_dest_to_login_dest)
            }
            .addOnFailureListener {
                view?.let { v ->
                    Snackbar.make(v, "Error: $it. Please, try later", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                }
            }
    }

    private fun deleteAcc() {
        //https://firebase.google.com/docs/auth/android/firebaseui#kotlin+ktx_5
        //show email intent
        composeEmail("delete")
        //attempt to delete account
        AuthUI.getInstance()
            .delete(safeContext)
            .addOnCompleteListener {
                view?.let {
                    Snackbar.make(it, "Account deleted", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show()
                }
                view?.findNavController()?.navigate(R.id.action_settings_dest_to_login_dest)
                //TODO: handle deleting all related boxes and images!
            }
            .addOnFailureListener {
                view?.let { v ->
                    Snackbar.make(
                        v,
                        "Error during account deleting: $it. Account will be deleted manually in 48 hours",
                        Snackbar.LENGTH_LONG
                    )
                        .setAction("Action", null)
                        .show()
                }
            }
    }

    private fun createDialog(): AlertDialog? {
        // 1. Instantiate an <code><a href="/reference/android/app/AlertDialog.Builder.html">AlertDialog.Builder</a></code> with its constructor
        //val alertDialog: AlertDialog? =
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setPositiveButton(R.string.ok) { _, _ ->
                    // User clicked OK button
                    deleteAcc()
                }
                setNegativeButton(R.string.cancel) { dialog, _ ->
                    // User cancelled the dialog
                    dialog.cancel()
                }
            }
            // Set other dialog properties
            builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title)

            // Create the AlertDialog
            builder.create()
        }
    }

    private fun composeEmail(s: String) {
        val subject = when (s) {
            "delete" -> getString(R.string.subject_delete_account)
            else -> getString(R.string.subject_feedback)
        }
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // only email apps should handle this
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email)))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            when (s) {
                "delete" -> {
                    val user = auth.currentUser
                    val username = user?.email
                    val txt = getString(R.string.text_delete_account, username)
                    putExtra(Intent.EXTRA_TEXT, txt)
                }
            }
        }
        if (intent.resolveActivity(requireContext().packageManager) != null) {
            startActivity(intent)
        }
    }
}