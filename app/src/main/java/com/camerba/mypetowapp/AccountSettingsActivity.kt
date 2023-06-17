package com.camerba.mypetowapp

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.camerba.mypetowapp.Model.User
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import de.hdodenhof.circleimageview.CircleImageView


class AccountSettingsActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private var checker = ""
    private var myUrl= ""
    private var imageUri : Uri? = null
    private var storageProfilePicRef: StorageReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_settings)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
          storageProfilePicRef =FirebaseStorage.getInstance().reference.child("Profile Pictures")

        findViewById<Button>(R.id.logout_btn).setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(this@AccountSettingsActivity, SignInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        findViewById<TextView>(R.id.change_image_text_btn).setOnClickListener {
            checker = "clicked"
            CropImage.activity()
                .setAspectRatio(1,1)
                .start(this@AccountSettingsActivity)
        }

        findViewById<ImageView>(R.id.save_info_profile_btn).setOnClickListener {
            if (checker == "clicked") {

                uploadImageAndUpdateInfo()


            }
            else {
                updateUserInfoOnly()
            }
        }

        userInfo()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null){
            val result = CropImage.getActivityResult(data)
            imageUri = result.uri
            findViewById<CircleImageView>(R.id.profile_image_view_profile_frag).setImageURI(imageUri)
        }


    }

    private fun updateUserInfoOnly() {


        when {TextUtils.isEmpty(findViewById<TextView>(R.id.full_name_profile_frag).text.toString()) ->
                Toast.makeText(this, "Lütfen önce tam adınızı yazın.", Toast.LENGTH_LONG).show()

            (findViewById<TextView>(R.id.usermane_profile_frag).text.toString()) == "" ->
                Toast.makeText(this, "Lütfen önce kullanıcı adınızı yazın.", Toast.LENGTH_LONG).show()

            (findViewById<TextView>(R.id.bio_profile_frag).text.toString()) == " " ->
                Toast.makeText(this, "Lütfen önce bionuzu yazın.", Toast.LENGTH_LONG).show()

            else -> {
                val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

                val userMap = HashMap<String, Any>()
                userMap["fullname"] = findViewById<TextView>(R.id.full_name_profile_frag).text.toString().toLowerCase()
                userMap["username"] =findViewById<TextView>(R.id.usermane_profile_frag).text.toString().toLowerCase()
                userMap["bio"] = findViewById<TextView>(R.id.bio_profile_frag).text.toString().toLowerCase()

                usersRef.child(firebaseUser.uid).updateChildren(userMap)

                Toast.makeText(this, "Hesap bilgileri başarıyla güncellendi.", Toast.LENGTH_LONG).show()

                val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()

            }
        }
    }

    private fun userInfo() {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)

        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(pO: DataSnapshot) {
                if (pO.exists()) {
                    val user = pO.getValue(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(findViewById<CircleImageView>(R.id.profile_image_view_profile_frag))
                    findViewById<TextView>(R.id.usermane_profile_frag).text = user?.getUsername()
                    findViewById<TextView>(R.id.full_name_profile_frag).text = user?.getFullname()
                    findViewById<TextView>(R.id.bio_profile_frag).text = user?.getBio()
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })
    }
    private fun uploadImageAndUpdateInfo() {



        when{
            imageUri == null -> Toast.makeText(this, "Lütfen önce fotğrafınızı seçin.", Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(findViewById<TextView>(R.id.full_name_profile_frag).text.toString()) ->
                Toast.makeText(this, "Lütfen önce tam adınızı yazın.", Toast.LENGTH_LONG).show()
            (findViewById<TextView>(R.id.usermane_profile_frag).text.toString()) == "" ->
                Toast.makeText(this, "Lütfen önce kullanıcı adınızı yazın.", Toast.LENGTH_LONG).show()
            (findViewById<TextView>(R.id.bio_profile_frag).text.toString()) == " " ->
                Toast.makeText(this, "Lütfen önce bionuzu yazın.", Toast.LENGTH_LONG).show()

            else ->{
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Hesap ayarları")
                progressDialog.setMessage("Lütfen bekleyin, profilinizi güncelliyoruz...")
                progressDialog.show()

                val fileRef = storageProfilePicRef!!.child(firebaseUser!!.uid + "jpg")

                var uploadTask:StorageTask<*>
                uploadTask = fileRef.putFile(imageUri!!)
                uploadTask.continueWithTask (Continuation  <UploadTask.TaskSnapshot,Task<Uri>>{ task ->

                    if (!task.isSuccessful){
                        task.exception?.let {
                            throw it
                            progressDialog.dismiss()
                        }
                    }
                    return@Continuation fileRef.downloadUrl
                }).addOnCompleteListener { OnCompleteListener<Uri> { task ->

                    if (task.isSuccessful){
                        val downloadUrl = task.result
                        myUrl = downloadUrl.toString()

                        val ref = FirebaseDatabase.getInstance().reference.child("Users")

                        val userMap = HashMap<String, Any>()
                        userMap["fullname"] = findViewById<EditText>(R.id.full_name_profile_frag).text.toString().toLowerCase()
                        userMap["username"] =findViewById<EditText>(R.id.usermane_profile_frag).text.toString().toLowerCase()
                        userMap["bio"] = findViewById<EditText>(R.id.bio_profile_frag).text.toString().toLowerCase()
                        userMap["image"] = myUrl

                        ref.child(firebaseUser.uid).updateChildren(userMap)

                        Toast.makeText(this, "Hesap bilgileri başarıyla güncellendi.", Toast.LENGTH_LONG).show()

                        val intent = Intent(this@AccountSettingsActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                        progressDialog.dismiss()
                    }
                    else{
                        progressDialog.dismiss()
                    }


                }  }


            }
        }

    }

}
