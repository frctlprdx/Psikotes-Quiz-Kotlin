package com.example.psikotes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.psikotes.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private var verificationId: String? = null

    // Fungsi untuk mencari userId berdasarkan nomor telepon
    private fun searchUserByPhone(phone: String) {
        val formattedPhone = "+62${phone.substring(1)}" // Format nomor telepon agar menjadi +62xxxxxxxxxx
        firestore.collection("users")
            .whereEqualTo("phone", formattedPhone)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // Mendapatkan userId dari dokumen yang ditemukan
                    val userDocument = documents.documents[0]
                    val userId = userDocument.id // userId dari dokumen Firestore
                    sendOtp(phone, userId)
                } else {
                    Toast.makeText(this, "Nomor telepon belum terdaftar", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Gagal memeriksa nomor telepon: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Fungsi untuk mengirim OTP ke nomor telepon
    private fun sendOtp(phone: String, userId: String) {
        val formattedPhone = "+62${phone.substring(1)}" // Format nomor telepon
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhone) // Menambahkan kode negara +62
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout pengiriman OTP
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // Jika verifikasi otomatis berhasil (misalnya, jika perangkat sudah terverifikasi)
                    // signInWithPhoneAuthCredential(credential) // Tidak digunakan di sini, karena akan dipindah ke VerifyOTPActivity
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@LoginActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) {
                    // OTP telah dikirim, simpan verificationId untuk digunakan saat verifikasi kode
                    this@LoginActivity.verificationId = verificationId
                    Toast.makeText(this@LoginActivity, "OTP telah dikirim", Toast.LENGTH_SHORT).show()
                    // Pindahkan ke VerifyOTPActivity untuk verifikasi OTP
                    val intent = Intent(this@LoginActivity, VerifyOTPActivity::class.java)
                    intent.putExtra("verificationId", verificationId) // Mengirimkan verificationId
                    startActivity(intent)
                }
            })
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Fungsi login menggunakan email dan password
    private fun loginToFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "${it.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.tvToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java) // Ganti ke RegisterActivity
            startActivity(intent)
        }

        binding.loginButton.setOnClickListener {
            val email = binding.emailLogin.text.toString()
            val phone = binding.phoneLogin.text.toString()
            val password = binding.passwordLogin.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && phone.isEmpty()) {
                loginToFirebase(email, password)
            } else if (phone.isNotEmpty() && email.isEmpty() && password.isEmpty()) {
                // Cari nomor telepon di Firestore dan kirimkan OTP
                searchUserByPhone(phone)
            } else {
                Toast.makeText(this, "Isi email & password atau phone", Toast.LENGTH_LONG).show()
            }
        }
    }
}
