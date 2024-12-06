package com.example.psikotes

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.psikotes.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private fun registerToFirebase(email: String, password: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Mendapatkan userId dari Firebase Auth
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Menyimpan data ke Firestore
                        val userMap = hashMapOf(
                            "phone" to phone,
                        )

                        // Menyimpan data pengguna ke Firestore
                        firestore.collection("users")
                            .document(userId)
                            .set(userMap)
                            .addOnSuccessListener {
                                // Menampilkan toast berhasil
                                Toast.makeText(this, "Register Berhasil", Toast.LENGTH_SHORT).show()
                                // Pindah ke MainActivity
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            .addOnFailureListener { exception ->
                                // Menampilkan error jika gagal menyimpan data ke Firestore
                                Toast.makeText(this, "Error saving user data: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    // Menampilkan error jika pendaftaran gagal
                    Toast.makeText(this, "Register failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // Mengatur padding untuk sistem bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inisialisasi FirebaseAuth dan Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.tvToLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)  // Perbaiki intent ke LoginActivity
            startActivity(intent)
        }

        binding.signUpButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val phone = binding.phoneNumberEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            // Validasi inputan
            if (email.isEmpty()) {
                binding.emailEditText.error = "Email harus diisi"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.emailEditText.error = "Email tidak valid"
                binding.emailEditText.requestFocus()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                binding.phoneNumberEditText.error = "Nomor Handphone harus diisi"
                binding.phoneNumberEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                binding.passwordEditText.error = "Password harus diisi"
                binding.passwordEditText.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6) {
                binding.passwordEditText.error = "Password minimal 6 karakter"
                binding.passwordEditText.requestFocus()
                return@setOnClickListener
            }

            // Format nomor telepon (contoh: +62 untuk Indonesia)
            val formattedPhone = "+62${phone.substring(1)}"

            // Lakukan registrasi ke Firebase
            registerToFirebase(email, password, formattedPhone)
        }
    }
}
