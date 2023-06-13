package com.camerba.mypetowapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        findViewById<Button>(R.id.signup_link_btn).setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }

        findViewById<Button>(R.id.login_btn).setOnClickListener {
            loginUser();
        }
    }

    private fun loginUser() {

        val email = findViewById<EditText>(R.id.email_login).text.toString()
        val password = findViewById<EditText>(R.id.password_login).text.toString()

        when{
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email gereklidir.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Şifre gereklidir.", Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignInActivity)
                progressDialog.setTitle("Giriş Yap")
                progressDialog.setMessage("Lütfen bekleyin,bu biraz zaman alabilir...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {task ->
                    if (task.isSuccessful){
                        progressDialog.dismiss()

                        val intent = Intent(this@SignInActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else {

                        val message = task.exception!!.toString()
                        Toast.makeText(this, "Hata: $message", Toast.LENGTH_LONG).show()
                        FirebaseAuth.getInstance().signOut()
                        progressDialog.dismiss()
                    }
                }

            }
        }

    }

    override fun onStart(){
        super.onStart()

        if (FirebaseAuth.getInstance().currentUser !=null){

            val intent = Intent(this@SignInActivity, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }
}