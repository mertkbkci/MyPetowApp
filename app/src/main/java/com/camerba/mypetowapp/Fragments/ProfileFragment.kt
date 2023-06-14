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
import com.camerba.mypetowapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


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
            this.profileId = pref.getString("profileId", "none")!!
        }

        if (profileId == firebaseUser.uid){

            view.findViewById<Button>(R.id.edit_account_settings_btn).text = "Edit Profile"
        }
        else  if (profileId == firebaseUser.uid){

            checkFollowAndFollowingButtonStatus()
        }

        view.findViewById<Button>(R.id.edit_account_settings_btn).setOnClickListener {
            startActivity(Intent(context,AccountSettingsActivity::class.java))
        }

        getFollowers()
        getFollowings()
        return view
    }

    private fun checkFollowAndFollowingButtonStatus() {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }

        if (followingRef != null){
            followingRef.addValueEventListener(object : ValueEventListener{

                override fun onDataChange(pO: DataSnapshot) {

                    if (pO.child(profileId).exists()){
                        view?.findViewById<Button>(R.id.edit_account_settings_btn)?.text = "Following"
                    }
                    else{
                        view?.findViewById<Button>(R.id.edit_account_settings_btn)?.text = "Follow"
                    }
                }

                override fun onCancelled(p0: DatabaseError) {

                }


            })
        }
    }


    private fun getFollowers(){
        val followersRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Followers")

        }

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
        val followersRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")

        }

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
}