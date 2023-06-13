package com.camerba.mypetowapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.camerba.mypetowapp.Adapter.UserAdapter
import com.camerba.mypetowapp.Model.User
import com.camerba.mypetowapp.R

import kotlin.collections.ArrayList


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class SearchFragment : Fragment() {

    private var recyclerView : RecyclerView? = null
    private var userAdapter: UserAdapter? = null
    private var mUser : MutableList<User>? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

       val view = inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.recycler_view_search)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        mUser = ArrayList()
        userAdapter = context?.let { UserAdapter(it,mUser as ArrayList<User>,true) }
        recyclerView?.adapter = userAdapter



        return view
    }



}