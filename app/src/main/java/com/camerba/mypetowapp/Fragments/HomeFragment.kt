package com.camerba.mypetowapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.camerba.mypetowapp.Adapter.PostAdapter
import com.camerba.mypetowapp.Model.Post
import com.camerba.mypetowapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class HomeFragment : Fragment() {


    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var folowingList: MutableList<Post>? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        var recyclerView: RecyclerView? = null
        recyclerView = view.findViewById(R.id.recycler_view_home)

        val linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.reverseLayout =true
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager


        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter

        checkFollowings()

        return view
    }

    private fun checkFollowings() {

        folowingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
                .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(pO: DataSnapshot) {

                if (pO.exists()){

                    (folowingList as ArrayList<String>).clear()

                    for (snapshot in pO.children){
                        snapshot.key?.let { (folowingList as ArrayList<String>).add(it) }
                    }

                    retrivePosts()
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }
        })

    }

    private fun retrivePosts() {
        val postsRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postsRef.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(pO: DataSnapshot) {
               postList?.clear()

                for (snapshot in pO.children){
                    val post = snapshot.getValue(Post::class.java)

                    for (id in folowingList as ArrayList<String> ){

                        if (post!!.getPublisher() == id){
                            postList!!.add(post)
                        }

                        postAdapter!!.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(pO: DatabaseError) {

            }

        })
    }


}