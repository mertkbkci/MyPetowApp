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
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        findViewById<Button>(R.id.signin_link_btn).setOnClickListener {
            startActivity(Intent(this,SignInActivity::class.java))
        }

        findViewById<Button>(R.id.signup_btn).setOnClickListener {
            CreateAccount()
        }
    }

    private fun CreateAccount() {

        val fullName = findViewById<EditText>(R.id.fullname_signup).text.toString()
        val userName = findViewById<EditText>(R.id.username_signup).text.toString()
        val email = findViewById<EditText>(R.id.email_signup).text.toString()
        val password = findViewById<EditText>(R.id.password_signup).text.toString()

        when{

            TextUtils.isEmpty(fullName) -> Toast.makeText(this,"Ad Soyad gereklidir.",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(userName) -> Toast.makeText(this,"Kullanıcı adı gereklidir.",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email) -> Toast.makeText(this,"Email gereklidir.",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password) -> Toast.makeText(this,"Şifre gereklidir.",Toast.LENGTH_LONG).show()

            else -> {
                val progressDialog = ProgressDialog(this@SignUpActivity)
                progressDialog.setTitle("Kayıl Ol")
                progressDialog.setMessage("Lütfen bekleyin,bu biraz zaman alabilir...")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()

                val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful){

                            saveUserInfo(fullName, userName, email, progressDialog )
                        }
                        else{
                            val message = task.exception!!.toString()
                            Toast.makeText(this,"Hata: $message",Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }
            }
        }

    }

    private fun saveUserInfo(fullName: String, userName: String, email: String, progressDialog: ProgressDialog) {

        val currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val usersRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")

        val database = Firebase.firestore

        val userMap = HashMap<String,Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName
        userMap["username"] = userName
        userMap["email"] = email
        userMap["bio"] = "ben bir hayvan severim. "
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/mypetowapp.appspot.com/o/Default%20Images%2Fprofile.png?alt=media&token=2ac8d5e1-4619-4b49-89f8-9ec4f7e2471d"

        usersRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
            if (task.isSuccessful) {

                progressDialog.dismiss()
                Toast.makeText(this,"Hesap başarıyla oluşturuldu.",Toast.LENGTH_LONG).show()


                val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
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