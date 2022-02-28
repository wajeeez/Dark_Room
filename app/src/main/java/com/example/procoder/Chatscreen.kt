package com.example.procoder

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ContextMenu
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.widget.ProgressBar
import android.widget.Toast
import android.widget.Toolbar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class Chatscreen : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var RV:RecyclerView
    private lateinit var userList:ArrayList<Users>
    private lateinit var adapter: UserAdapter
    private lateinit var mAuth:FirebaseAuth
    private lateinit var mDbref:DatabaseReference
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var toolbar:androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatscreen)



        progressBar=findViewById(R.id.progressbar)
        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        swipeRefreshLayout=findViewById(R.id.swipe)
        mAuth= FirebaseAuth.getInstance()

        mDbref=FirebaseDatabase.getInstance().getReference()


        userList= ArrayList()
        adapter=UserAdapter(this,userList)

        RV = findViewById<RecyclerView>(R.id.rv)
        RV.layoutManager=LinearLayoutManager(this)
        RV.adapter=adapter;





        swipeRefreshLayout.setOnRefreshListener {
            fetchdata()
        }
        fetchdata()







    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.logout){
            Toast.makeText(this,"SIGNING OUT",Toast.LENGTH_SHORT).show()

            var sharedPreferences = getSharedPreferences("logindata", MODE_PRIVATE)
            sharedPreferences.edit().clear().commit()

            val counter = sharedPreferences.getBoolean("logincounter",(MODE_PRIVATE.toString().toBoolean()))
            if(!counter){
                Log.d("Counter value" ,counter.toString())
                mAuth.signOut()
                var sharedPreferencetoken = getSharedPreferences("tokendata", MODE_PRIVATE)
                sharedPreferencetoken .edit().clear().commit()
                var appUtil=AppUtil()
                appUtil.updateOnlineStatus(this,"Offline")

                val intent = Intent(this,LOGIN::class.java)
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }

          //  progressBar.visibility=View.INVISIBLE

            return true
        }

        return true
    }

    private fun fetchdata(){
        swipeRefreshLayout.isRefreshing =true
        mDbref.child("user").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser: Users? = postSnapshot.getValue(Users::class.java)
                    if (mAuth.currentUser?.uid !=currentUser?.uid){
                        userList.add(currentUser!!)
                    }

                }
                swipeRefreshLayout.isRefreshing =false
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    override fun onPause() {
        super.onPause()
        var appUtil = AppUtil()
        appUtil.updateOnlineStatus(this,"Offline")
    }

    override fun onResume() {
        super.onResume()
        var appUtil = AppUtil()
        appUtil.updateOnlineStatus(this,"Online")
    }

    override fun onBackPressed() {


    }




}