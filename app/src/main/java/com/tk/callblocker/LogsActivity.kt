package com.tk.callblocker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.tk.callblocker.databinding.ActivityLogsBinding

class LogsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLogsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.logs_title)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        refreshList()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_logs, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            R.id.action_clear_logs -> {
                confirmClear()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun refreshList() {
        val logs = Prefs.getBlockedCallLogs(this)
        if (logs.isEmpty()) {
            binding.recyclerView.visibility = android.view.View.GONE
            binding.textEmpty.visibility = android.view.View.VISIBLE
        } else {
            binding.recyclerView.visibility = android.view.View.VISIBLE
            binding.textEmpty.visibility = android.view.View.GONE
            binding.recyclerView.adapter = BlockedCallAdapter(logs)
        }
    }

    private fun confirmClear() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.logs_clear_title))
            .setMessage(getString(R.string.logs_clear_message))
            .setPositiveButton(getString(R.string.logs_clear_confirm)) { _, _ ->
                Prefs.clearBlockedCallLogs(this)
                refreshList()
            }
            .setNegativeButton(getString(R.string.logs_clear_cancel), null)
            .show()
    }
}
