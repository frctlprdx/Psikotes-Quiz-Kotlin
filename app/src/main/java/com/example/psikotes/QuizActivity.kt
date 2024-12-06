package com.example.psikotes

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.psikotes.databinding.ActivityQuizBinding

data class Question(
    val id: Int,
    val question: String,
    val optionone: String,
    val optiontwo: String,
    val optionthree: String?,
    val optionfour: String?,
    val optionfive: String?,
    val choosedOption: String
)

val listOfQuestion = listOf(
    Question(1, "Seberapa tertarik Anda dengan ide untuk mengembangkan dan meningkatkan penjualan produk?",
        "Tidak tertarik sama sekali", "Kurang tertarik", "Netral", "Tertarik", "Sangat tertarik", ""),
    Question(2, "Apakah Anda suka bekerja dengan orang lain dalam konteks bisnis?",
        "Ya", "Tidak", null, null, null, ""),
    Question(3, "Seberapa sering Anda berbagi ide atau strategi dengan orang lain?",
        "Tidak Pernah", "Jarang", "Kadang-kadang", "Sering", "Selalu", ""),
    Question(4, "Apakah Anda lebih suka bekerja secara mandiri?",
        "Ya", "Tidak", null, null, null, ""),
    Question(5, "Seberapa Anda menikmati berkolaborasi dalam proyek?",
        "Tidak suka", "Kurang suka", "Biasa saja", "Suka", "Sangat suka", ""),
    Question(6, "Apakah Anda tertarik untuk memahami pasar teknologi bisnis?",
        "Ya", "Tidak", null, null, null, ""),
    Question(7, "Seberapa sering Anda membaca berita atau artikel tentang teknologi?",
        "Tidak pernah", "Jarang", "Kadang-kadang", "Sering", "Selalu", ""),
    Question(8, "Apakah Anda tertarik untuk belajar tentang bisnis dan teknologi?",
        "Ya", "Tidak", null, null, null, ""),
    Question(9, "Seberapa Anda peduli tentang tren di pasar keuangan?",
        "Tidak peduli", "Kurang peduli", "Biasa saja", "Peduli", "Sangat peduli", ""),
    Question(10, "Apakah Anda suka mengikuti perkembangan ekonomi?",
        "Ya", "Tidak", null, null, null, ""),
    Question(11, "Seberapa sering Anda mendiskusikan isu-isu keuangan dengan teman atau keluarga?",
        "Tidak pernah", "Jarang", "Kadang-kadang", "Sering", "Selalu", ""),
    Question(12, "Apakah Anda tertarik untuk memahami proses manufaktur?",
        "Ya", "Tidak", null, null, null, ""),
    Question(13, "Seberapa Anda suka bekerja dengan alat dan mesin?",
        "Tidak suka", "Kurang suka", "Biasa saja", "Suka", "Sangat suka", ""),
    Question(14, "Apakah Anda suka merancang aplikasi atau alat teknologi?",
        "Ya", "Tidak", null, null, null, ""),
    Question(15, "Seberapa Anda menikmati mengembangkan aplikasi mobile?",
        "Tidak suka", "Kurang suka", "Biasa saja", "Suka", "Sangat suka", ""),
    Question(16, "Apakah Anda suka berkomunikasi tentang proyek atau konsultasi?",
        "Ya", "Tidak", null, null, null, ""),
    Question(17, "Seberapa sering Anda terlibat dalam diskusi proyek dengan tim?",
        "Tidak pernah", "Jarang", "Kadang-kadang", "Sering", "Selalu", ""),
    Question(18, "Seberapa Anda menikmati mengumpulkan dan menganalisis informasi?",
        "Tidak suka", "Kurang suka", "Biasa saja", "Suka", "Sangat suka", ""),
    Question(19, "Apakah Anda tertarik dengan proses dalam manufaktur?",
        "Ya", "Tidak", null, null, null, ""),
    Question(20, "Apakah Anda suka bekerja dengan pemasok atau vendor?",
        "Ya", "Tidak", null, null, null, ""),
    Question(21, "Seberapa pentingkah Anda rasa dokumentasi teknis dalam pekerjaan?",
        "Tidak penting", "Kurang penting", "Biasa saja", "Penting", "Sangat penting", ""),
    Question(22, "Apakah Anda suka mendesain antarmuka pengguna untuk aplikasi atau website?",
        "Ya", "Tidak", null, null, null, ""),
    Question(23, "Seberapa Anda menikmati menulis atau mengedit dokumen?",
        "Tidak suka", "Kurang suka", "Biasa saja", "Suka", "Sangat suka", "")
)


class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private var currentQuestionIndex = 0
    private var selectedOptionIndex = -1
    private var selectedButton: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()
        setupWindowInsets()

        // Memuat pertanyaan pertama saat aktivitas dimulai
        loadQuestion()

        // Mengatur klik untuk tombol Next
        binding.nextButton.setOnClickListener {
            handleNextButtonClick()
        }
    }

    private fun loadQuestion() {
        // Mengambil pertanyaan saat ini
        val question = listOfQuestion[currentQuestionIndex]

        // Reset pilihan sebelumnya
        selectedOptionIndex = -1
        selectedButton = null

        // Mengatur teks pertanyaan
        binding.questionText.text = question.question

        // Membuat daftar opsi jawaban tanpa null
        val options = listOfNotNull(
            question.optionone,
            question.optiontwo,
            question.optionthree,
            question.optionfour,
            question.optionfive
        )

        // Membersihkan opsi sebelumnya
        binding.optionsContainer.removeAllViews()

        // Menambahkan opsi jawaban dinamis
        for (i in options.indices) {
            val button = Button(this).apply {
                text = options[i]
                isAllCaps = false // Menghilangkan huruf kapital pada teks (opsional)
                setOnClickListener {
                    handleOptionClick(this, i)
                }
            }
            binding.optionsContainer.addView(button)
        }

        // Perbarui status tombol Next
        updateNextButtonState()
    }

    private fun handleOptionClick(button: Button, optionIndex: Int) {
        // Mengatur pilihan yang dipilih
        selectedButton?.isSelected = false
        button.isSelected = true
        selectedButton = button
        selectedOptionIndex = optionIndex

        // Perbarui status tombol Next
        updateNextButtonState()
    }

    private fun handleNextButtonClick() {
        if (currentQuestionIndex < listOfQuestion.size - 1) {
            currentQuestionIndex++
            loadQuestion()
        } else {
            Toast.makeText(this, "Quiz selesai!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateNextButtonState() {
        // Tombol Next hanya aktif jika ada opsi yang dipilih
        binding.nextButton.isEnabled = selectedOptionIndex != -1
        binding.nextButton.text = if (currentQuestionIndex < listOfQuestion.size - 1) "Next" else "Finish"
    }

    private fun enableEdgeToEdge() {
        // Mengatur mode full-screen dengan menghilangkan padding sistem
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun setupWindowInsets() {
        // Menyesuaikan padding dengan insets sistem
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

