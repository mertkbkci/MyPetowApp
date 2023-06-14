package com.camerba.mypetowapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.camerba.mypetowapp.Model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        findViewById<Button>(R.id.logout_btn).setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.save_info_profile_btn).setOnClickListener {

            if (checker =="clicked"){

            }
            else{
                updateUserInfoOnly()
            }
        }


        userInfo()
    }

    private fun updateUserInfoOnly() {

       when {
           TextUtils.isEmpty(findViewById<EditText>(R.id.full_name_profile_frag).text.toString())-> Toast.makeText(this,"Lütfen önce full isminizi girin.",Toast.LENGTH_LONG).show()
           findViewById<EditText>(R.id.usermane_profile_frag).text.toString() ==""-> Toast.makeText(this,"Lütfen önce kullanıcı adınızı girin." , Toast.LENGTH_LONG).show()
           findViewById<EditText>(R.id.bio_profile_frag).text.toString() ==""-> Toast.makeText(this,"Lütfen önce bionuzu girin." , Toast.LENGTH_LONG).show()

           else -> {
               val usersRef = FirebaseDatabase.getInstance().reference.child("Users")


               val userMap = HashMap<String, Any>()
               userMap["fullname"] =
                   findViewById<EditText>(R.id.full_name_profile_frag).text.toString().toLowerCase()
               userMap["username"] =
                   findViewById<EditText>(R.id.usermane_profile_frag).text.toString().toLowerCase()
               userMap["bio"] =
                   findViewById<EditText>(R.id.bio_profile_frag).text.toString().toLowerCase()

               usersRef.child(firebaseUser.uid).updateChildren(userMap)

               Toast.makeText(this, "Hesap bilgileri başarıyla güncellendi.", Toast.LENGTH_LONG)
                   .show()


               val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
               startActivity(intent)
               finish()

           }
       }
    }



    private fun userInfo(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {


                if (dataSnapshot.exists()){
                    val user = dataSnapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(findViewById<CircleImageView>(R.id.profile_image_view_profile_frag))
                    findViewById<TextView>(R.id.usermane_profile_frag).text= user!!.getUsername()
                    findViewById<TextView>(R.id.full_name_profile_frag).text = user!!.getFullname()
                    findViewById<TextView>(R.id.bio_profile_frag).text = user!!.getBio()
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {

            }

        })
    }
}


