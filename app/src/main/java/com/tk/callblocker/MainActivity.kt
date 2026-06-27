package com.tk.callblocker

import android.app.role.RoleManager
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.tk.callblocker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val roleRequestLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                Prefs.setBlockingEnabled(this, true)
            } else {
                Toast.makeText(this, getString(R.string.role_request_failed), Toast.LENGTH_LONG).show()
            }
            refreshUi()
        }

    private val contactsPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (!granted) {
                Toast.makeText(this, getString(R.string.permission_contacts_denied), Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonToggle.setOnClickListener { onToggleClicked() }
        binding.buttonViewLogs.setOnClickListener {
            startActivity(Intent(this, LogsActivity::class.java))
        }

        if (!ContactsLookup.hasContactsPermission(this)) {
            contactsPermissionLauncher.launch(android.Manifest.permission.READ_CONTACTS)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshUi()
    }

    private fun onToggleClicked() {
        val isEnabled = Prefs.isBlockingEnabled(this)
        if (isEnabled) {
            Prefs.setBlockingEnabled(this, false)
            refreshUi()
            return
        }

        if (hasCallScreeningRole()) {
            Prefs.setBlockingEnabled(this, true)
            refreshUi()
        } else {
            requestCallScreeningRole()
        }
    }

    private fun hasCallScreeningRole(): Boolean {
        val roleManager = getSystemService(RoleManager::class.java)
        return roleManager?.isRoleHeld(RoleManager.ROLE_CALL_SCREENING) == true
    }

    private fun requestCallScreeningRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) {
            val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
            roleRequestLauncher.launch(intent)
        } else {
            Toast.makeText(this, getString(R.string.role_request_failed), Toast.LENGTH_LONG).show()
        }
    }

    private fun refreshUi() {
        val enabled = Prefs.isBlockingEnabled(this) && hasCallScreeningRole()
        if (Prefs.isBlockingEnabled(this) && !hasCallScreeningRole()) {
            // Role was revoked outside the app; keep state consistent.
            Prefs.setBlockingEnabled(this, false)
        }

        binding.buttonToggle.text = if (enabled) getString(R.string.button_disable) else getString(R.string.button_enable)
        binding.textStatusTitle.text = if (enabled) getString(R.string.status_enabled) else getString(R.string.status_disabled)
        binding.textStatusDescription.text = when {
            enabled -> getString(R.string.status_description_enabled)
            !hasCallScreeningRole() -> getString(R.string.status_description_role_missing)
            else -> getString(R.string.status_description_disabled)
        }
    }
}
