package com.example.procoder

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class Splashscreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this,LOGIN::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            finish()
            startActivity(intent)

        },1500)









    }

    override fun onStart() {
        super.onStart()

    }


   private fun getToken(){
       FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
           if (task.isSuccessful) {


               var token = task.result
               Log.d("TOKEN CHECK KRO ",token)
               Toast.makeText(this,"TOKEN TOKEN TOKEN "+ getToken(),Toast.LENGTH_SHORT).show()

               Toast.makeText(this,"TOKEN TOKEN TOKEN "+ token,Toast.LENGTH_SHORT).show()
               var sharedPreferences = getSharedPreferences("tokendata", MODE_PRIVATE)
               var editor: SharedPreferences.Editor = sharedPreferences.edit()
               editor.putString("token", token)
               editor.apply()
           }

       } )
   }


}





