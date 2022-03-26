package com.example.procoder

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import kotlin.math.log

class LOGIN : AppCompatActivity() {

    private lateinit var user: EditText
    private lateinit var pass: EditText
    private lateinit var login: Button
    private lateinit var signup:TextView
    private lateinit var signup2:TextView

    private lateinit var progressBar: ProgressBar
    private lateinit var mAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor :SharedPreferences.Editor
    private lateinit var mDbref: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()


        mAuth = FirebaseAuth.getInstance()


        progressBar=findViewById(R.id.progressbar)
        user = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        login = findViewById(R.id.login)
        signup = findViewById(R.id.signup)
        signup2 = findViewById(R.id.signup2)

       /* if(mAuth.currentUser!=null){
            val intent= Intent(this,Chatscreen::class.java)
            finish()
            startActivity(intent)
        }*/

       signup.setOnClickListener {
           val intent = Intent(this,Signup::class.java)
           startActivity(intent)
       }

        login.setOnClickListener {

            val email = user.text.toString()
            val pass = pass.text.toString()

            if(email.isEmpty() || pass.isEmpty()){
                Toast.makeText(this,"FIELDS ARE EMPTY", Toast.LENGTH_SHORT).show()
            }else{
                progressBar.visibility=View.VISIBLE
                login(email,pass);
            }
        }

    }

    private fun login(email: String, pass: String) {




        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    session(email)
                    progressBar.visibility=View.INVISIBLE
                    getToken()

                    val intent= Intent(this,Chatscreen::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    finish()
                    startActivity(intent)


                } else {
                    progressBar.visibility=View.INVISIBLE
                    Toast.makeText(this,"User does not exist or ERROR", Toast.LENGTH_SHORT).show()

                }
            }

    }

    private fun session(email:String){

       sharedPreferences = getSharedPreferences("logindata",MODE_PRIVATE)
        editor= sharedPreferences.edit()
        editor.putBoolean("logincounter",true)
        editor.putString("Email",email)
        editor.apply()

    }

    override fun onStart() {
        super.onStart()
       checksession()
    }
    private fun checksession(){
        sharedPreferences = getSharedPreferences("logindata",MODE_PRIVATE)
        val counter = sharedPreferences.getBoolean("logincounter",(MODE_PRIVATE.toString().toBoolean()))
        if(counter){

            val intent= Intent(this,Chatscreen::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            startActivity(intent)
        }else{

        }


    }




    private fun getToken(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                var token = task.result
                Log.d("TOKEN CHECK KRO ",token)
                updatetoken(token)

            var sharedPreferences = getSharedPreferences("tokendata", MODE_PRIVATE)
                var editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString("token", token)
                editor.apply()
            }
        } )
    }


    private fun updatetoken(token:String){
      /*  var sharedPreferences = getSharedPreferences("tokendata", MODE_PRIVATE)
        var token =  sharedPreferences.getString("token", MODE_PRIVATE.toString() )
        Log.d("TOKEN CHECK AGAIN ",token!!)*/
        mDbref = FirebaseDatabase.getInstance().getReference()
        val taskMap: MutableMap<String, Any> = HashMap()
        taskMap["token"] =token
        mDbref.child("user").child(mAuth.currentUser?.uid!!).updateChildren(taskMap).addOnCompleteListener(
            OnCompleteListener { task ->
                if (task.isSuccessful){
                    Log.d("TOKEN UPDATED ",token)
                }
            })

    }

}