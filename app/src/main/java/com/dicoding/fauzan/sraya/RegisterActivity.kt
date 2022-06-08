package com.dicoding.fauzan.sraya

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.dicoding.fauzan.sraya.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener {
            val nik = binding.edtNik.text.toString()
            val nama = binding.edtName.text.toString()
            val tanggal = binding.dateTglahir.text.toString()
            var jenkel = ""
            val nohp = binding.edtNohp.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val kontak = binding.edtKontak.text.toString()

            if (nik.isEmpty()){
                binding.edtNik.error = "NIK Harus Diisi!"
                binding.edtNik.requestFocus()
                return@setOnClickListener
            }

            if (nama.isEmpty()){
                binding.edtName.error = "Nama Harus Diisi!"
                binding.edtName.requestFocus()
                return@setOnClickListener
            }

            if (tanggal.isEmpty()){
                binding.dateTglahir.error = "Tanggal Lahir Harus Diisi!"
                binding.dateTglahir.requestFocus()
                return@setOnClickListener
            }

            if (binding.rbMale.isChecked){
                jenkel = binding.rbMale.text.toString()
            }

            if (binding.rbFemale.isChecked){
                jenkel = binding.rbFemale.text.toString()
            }

            if (jenkel.isEmpty()){
                Toast.makeText(this, "Gender Tidak Diketahui", Toast.LENGTH_SHORT).show()
            }

            if (nohp.isEmpty()){
                binding.edtNohp.error = "No Handphone Harus Diisi!"
                binding.edtNohp.requestFocus()
                return@setOnClickListener
            }

            if (email.isEmpty()){
                binding.edtEmail.error = "Email Harus Diisi!"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.edtEmail.error = "Email Tidak Valid!"
                binding.edtEmail.requestFocus()
                return@setOnClickListener
            }

            if (password.isEmpty()){
                binding.edtPassword.error = "Password Harus Diisi!"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            if (password.length < 6){
                binding.edtPassword.error = "Password Minimal 6 Karakter!"
                binding.edtPassword.requestFocus()
                return@setOnClickListener
            }

            if (kontak.isEmpty()){
                binding.edtKontak.error = "No Kontak terdekat Harus Diisi!"
                binding.edtKontak.requestFocus()
                return@setOnClickListener
            }

            RegisterFirebase(email, password, nohp)
        }

        setupAction()
    }

    private fun RegisterFirebase(email: String, password: String, jenkel: String) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{
                saveUserToFireStore(jenkel)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Terjadi Error!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveUserToFireStore(jenkel: String) {
        val db = FirebaseFirestore.getInstance()

        val hashMap : HashMap<String, Any> = HashMap()
        hashMap["NIK"] = binding.edtNik.text.toString()
        hashMap["Nama"] = binding.edtName.text.toString()
        hashMap["Tanggal"] = binding.dateTglahir.text.toString()
        hashMap["Jenkel"] = jenkel
        hashMap["NoHp"] = binding.edtNohp.text.toString()
        hashMap["Email"] = binding.edtEmail.text.toString()
        hashMap["Password"] = binding.edtPassword.text.toString()
        hashMap["Kontak"] = binding.edtKontak.text.toString()

        db.collection("Users")
            .add(hashMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Register Berhasil!", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Terjadi Kesalahan Saat Mendaftar!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupAction() {
        binding.tvLogin.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}