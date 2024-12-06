package com.example.psikotes

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.psikotes.databinding.ActivityVerifyOtpactivityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class VerifyOTPActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerifyOtpactivityBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpactivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Dapatkan verificationId dari intent
        verificationId = intent.getStringExtra("verificationId")

        binding.verifyButton.setOnClickListener {
            val otp = binding.otpEditText.text.toString()
            if (otp.isNotEmpty() && verificationId != null) {
                verifyOtp(otp)
            } else {
                Toast.makeText(this, "Masukkan OTP", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk verifikasi OTP
    private fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Login Berhasil", Toast.LENGTH_SHORT).show()
                    // Pindah ke MainActivity setelah berhasil login
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Login Gagal: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
