package com.camerba.mypetowapp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.camerba.mypetowapp.Model.User
import com.camerba.mypetowapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter (private var mContext: Context,
                   private var mUser: List<User>,
                   private var isFragment: Boolean = false): RecyclerView.Adapter<UserAdapter.ViewHolder>(){

    private var firebaseUser : FirebaseUser? = FirebaseAuth.getInstance().currentUser
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)
        return UserAdapter.ViewHolder(view)
    }
    override fun getItemCount(): Int {
        return mUser.size
    }


    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {

       val user = mUser[position]

        holder.userNameTextView.text = user.getUsername()
        holder.userFullNameTextView.text = user.getFullname()
        Picasso.get().load(user.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage)

        checkFollowingStatus(user.getUID(), holder.followButton)


        holder.followButton.setOnClickListener {
            if (holder.userFullNameTextView.text.toString() == "Takip et") {

                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Takip et").child(it1.toString())
                        .child("Takip ediliyor").child(user.getUID())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Takip et").child(user.getUID())
                                        .child("Takipçiler").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else{
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Takip et").child(it1.toString())
                        .child("Takip ediliyor").child(user.getUID())
                        .removeValue().addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Takip et").child(user.getUID())
                                        .child("Takipçiler").child(it1.toString())
                                        .removeValue().addOnCompleteListener { task ->
                                            if (task.isSuccessful) {

                                            }
                                        }
                                }
                            }
                        }
                }
            }
        }
    }

    class ViewHolder (@NonNull itemView: View) : RecyclerView.ViewHolder(itemView){

        var userNameTextView : TextView =itemView.findViewById(R.id.user_name_search)
        var userFullNameTextView : TextView =itemView.findViewById(R.id.user_full_name_search)
        var userProfileImage : CircleImageView =itemView.findViewById(R.id.user_profile_image_search)
        var followButton : Button=itemView.findViewById(R.id.follow_btn_search)
    }
    private fun checkFollowingStatus(uid: String, followButton: Button) {

       val followingRef = firebaseUser?.uid.let { it1 ->
           FirebaseDatabase.getInstance().reference
               .child("Takip et").child(it1.toString())
               .child("Takip ediliyor")
       }

        followingRef.addValueEventListener(object  : ValueEventListener{

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child(uid).exists()){
                    followButton.text = " Takip Ediliyor"
                }
                else{
                    followButton.text = " Takip Et"
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}