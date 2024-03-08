package com.example.tplogin
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.tplogin.MainActivity
import com.example.tplogin.R
import com.example.tplogin.Register
import com.example.tplogin.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
class Login : AppCompatActivity() {
    lateinit var auth : FirebaseAuth
    public override fun onStart() {
        super.onStart()
// Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)
        auth = FirebaseAuth.getInstance()
        binding.registerNow.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            binding.progress.visibility = View.VISIBLE
            if (email.isEmpty()) {
                Toast.makeText(this, "Email is empty", Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                Toast.makeText(this, "Password is empty",
                    Toast.LENGTH_SHORT).show()
                binding.progress.visibility = View.GONE
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
// Sign in success, update UI with the signed-in user's information
                        Log.d("isSuccessfulSignin", "signInWithEmail:success")
                        Toast.makeText(this, "login ok ",
                            Toast.LENGTH_SHORT).show()
                        binding.progress.visibility = View.GONE
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)

                        finish()
                    } else {
                        binding.progress.visibility = View.GONE
// If sign in fails, display a message to the user.

                        Log.w("isSuccessfulNOOOSignin",

                            "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,"Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }
}