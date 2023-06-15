package com.camerba.mypetowapp.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.camerba.mypetowapp.AccountSettingsActivity
import com.camerba.mypetowapp.Model.User
import com.camerba.mypetowapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {

    private lateinit var profileId:String
    private lateinit var firebaseUser:FirebaseUser




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)

        if (pref != null){
            //sıkıntı olabılır
            this.profileId = pref?.getString("profileId", "none")!!
        }

        if (profileId == firebaseUser.uid){

            view.findViewById<Button>(R.id.edit_account_settings_btn).text = "Edit Profile"
        }
        else  if (profileId != firebaseUser.uid){

            checkFollowAndFollowingButtonStatus()
        }

        view.findViewById<Button>(R.id.edit_account_settings_btn).setOnClickListener {


            if (view.findViewById<Button>(R.id.edit_account_settings_btn).text.toString() == "Edit Profile") startActivity(Intent(context,AccountSettingsActivity::class.java))
            else if (view.findViewById<Button>(R.id.edit_account_settings_btn).text.toString() == "Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(profileId).setValue(true)
                }

                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(profileId)
                        .child("Followers").child(it1.toString())
                        .setValue(true)
                }
            }
            else if (view.findViewById<Button>(R.id.edit_account_settings_btn).text.toString() == "Following") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(profileId).removeValue()
                }

                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(profileId)
                        .child("Followers").child(it1.toString())
                        .removeValue()
                }
            }

        }

        getFollowers()
        getFollowings()
        userInfo()
        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }
// değişti
        followingRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child(profileId).exists()){
                    view?.findViewById<Button>(R.id.edit_account_settings_btn)?.text = "Following"
                }
                else{
                    view?.findViewById<Button>(R.id.edit_account_settings_btn)?.text = "Follow"
                }
            }

            override fun onCancelled(dataSnapshot: DatabaseError) {

            }


        })
    }


    private fun getFollowers(){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Followers")



        followersRef.addValueEventListener(object  : ValueEventListener{

            override fun onDataChange(pO: DataSnapshot) {

                if (pO.exists()){

                    view?.findViewById<TextView>(R.id.total_followers)?.text = pO.childrenCount.toString()
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }

        })
    }

    private fun getFollowings(){
        val followersRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(profileId)
                .child("Following")



        followersRef.addValueEventListener(object  : ValueEventListener{

            override fun onDataChange(pO: DataSnapshot) {

                if (pO.exists()){

                    view?.findViewById<TextView>(R.id.total_following)?.text = pO.childrenCount.toString()
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }

        })
    }
    private fun userInfo(){
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)

        usersRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(pO: DataSnapshot) {


                if (pO.exists()){
                    val user = pO.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(view?.findViewById(R.id.pro_image_profile_frag))
                    view?.findViewById<TextView>(R.id.profile_fragment_username)?.text = user!!.getUsername()
                    view?.findViewById<TextView>(R.id.full_name_profile_frag)?.text = user!!.getFullname()
                    view?.findViewById<TextView>(R.id.bio_profile_frag)?.text = user!!.getBio()
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }

        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId",firebaseUser.uid)
        pref?.apply()
    }
}