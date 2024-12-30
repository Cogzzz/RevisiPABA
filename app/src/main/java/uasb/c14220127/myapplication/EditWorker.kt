package uasb.c14220127.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class EditWorker : AppCompatActivity() {
    private lateinit var editWorkeName: EditText
    private lateinit var editWorkeAge: EditText
    private lateinit var buttonSave: Button

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.worker_edit)

        // Inisialisasi Firebase Auth dan Firestore
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Inisialisasi view
        initializeViews()

        // Setup tombol simpan
        setupButtonListeners()
    }

    private fun initializeViews() {
        editWorkeName = findViewById(R.id.etname)
        editWorkeAge = findViewById(R.id.etAge)
        buttonSave = findViewById(R.id.btnSave)
    }

    private fun setupButtonListeners() {
        buttonSave.setOnClickListener {
            updateWorkerData()
        }
    }

    private fun updateWorkerData() {
        val workerId = intent.getStringExtra("worker_id") // Dapatkan workerId dari Intent
        if (workerId.isNullOrEmpty()) {
            // Jika workerId tidak ada, kembali ke halaman sebelumnya
            Toast.makeText(this, "Worker ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val workerName = editWorkeName.text.toString().trim()
        val workerAgeStr = editWorkeAge.text.toString().trim()

        // Validasi input
        if (workerName.isEmpty()) {
            editWorkeName.error = "Nama tidak boleh kosong"
            return
        }

        if (workerAgeStr.isEmpty()) {
            editWorkeAge.error = "Umur tidak boleh kosong"
            return
        }

        val workerAge = workerAgeStr.toIntOrNull()
        if (workerAge == null) {
            editWorkeAge.error = "Umur harus berupa angka"
            return
        }

        // Update data ke Firestore
        val workerData = mapOf(
            "name" to workerName,
            "age" to workerAge
        )

        db.collection("workers")
            .document(workerId)
            .update(workerData)
            .addOnSuccessListener {
                // Tampilkan pesan sukses
                Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                // Tampilkan pesan error
                Toast.makeText(this, "Gagal memperbarui data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
