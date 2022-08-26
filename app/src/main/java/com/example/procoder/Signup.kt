package com.example.procoder

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.net.URI

class Signup : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var name: EditText
    private lateinit var user: EditText
    private lateinit var pass: EditText
    private lateinit var signup: Button
    private lateinit var imgselect:Button
    private lateinit var circleImageView: CircleImageView
    private var PICK_IMAGE=100
    private var check=0;
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbref: DatabaseReference
    private lateinit var storageReference:StorageReference


    private var uri:Uri?=null
    private var u:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        supportActionBar?.hide()

        mAuth = FirebaseAuth.getInstance()
        name = findViewById(R.id.name)
        user = findViewById(R.id.email)
        pass = findViewById(R.id.pass)
        signup = findViewById(R.id.signup)
        progressBar=findViewById(R.id.progressbar)

        imgselect=findViewById(R.id.imgselect)
        circleImageView=findViewById(R.id.circleImageView)


        imgselect.setOnClickListener {
            val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)

             startActivityForResult(gallery, PICK_IMAGE)

        }

        signup.setOnClickListener {

            val uname = name.text.toString()
            val email = user.text.toString()
            val pass = pass.text.toString()

            if(email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "FIELDS ARE EMPTY", Toast.LENGTH_SHORT).show()
            }else if(pass.toString().length < 8){

                Toast.makeText(this, "PASSWORD SHOULD BE 8 CHAR AT LEAST", Toast.LENGTH_SHORT).show()


            }else{
                progressBar.visibility= View.VISIBLE
                signup(uname,email,pass)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int,data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            uri = data?.data




            circleImageView.setImageURI(uri)


        }
    }







    private fun signup(name:String,email: String, pass: String ) {

        /*progressDialog.setTitle("PLEASE WAIT")
        progressDialog.setMessage("Uploading........")
        progressDialog.show()*/
        if (uri==null){
            progressBar.visibility=View.INVISIBLE
            Toast.makeText(this,"PLEASE SELECT AN IMAGE ",Toast.LENGTH_SHORT).show()
        }else{



            mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                        addusertodatabase(name,email,mAuth.currentUser?.uid!!,uri!!)
                        //uploadpic(uri!!);

                    } else {
                        progressBar.visibility=View.INVISIBLE
                        Toast.makeText(this,"Some error occurred",Toast.LENGTH_SHORT).show()


                    }
                }
        }

    }

    private fun addusertodatabase(name: String, email: String, uid: String ,uri:Uri ) {


        var sharedPreferences = getSharedPreferences("tokendata", MODE_PRIVATE)
        var token =  sharedPreferences.getString("token", MODE_PRIVATE.toString() )

            mDbref = FirebaseDatabase.getInstance().getReference()
            mDbref.child("user").child(uid).setValue(Users(name,email,uid,token)).addOnCompleteListener(
                OnCompleteListener { task ->
                    if (task.isSuccessful){

                        storageReference=FirebaseStorage.getInstance().getReference("User/"+mAuth.currentUser?.uid+".jpg")
                        storageReference.putFile(uri).addOnSuccessListener {

                          //  mDbref = FirebaseDatabase.getInstance().getReference()
                            progressBar.visibility=View.INVISIBLE
                            Toast.makeText(this,"SIGNED UP SUCCESSFUL",Toast.LENGTH_SHORT).show()
                            val intent = Intent(this,LOGIN::class.java)
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            finish()
                            startActivity(intent)
                        }

                            .addOnFailureListener{
                                progressBar.visibility=View.INVISIBLE
                                Toast.makeText(this,"FAILED TO UPLOAD IMG",Toast.LENGTH_SHORT).show()
                                mAuth.currentUser?.delete()
                                Toast.makeText(this,"PLEASE TRY AGAIN",Toast.LENGTH_SHORT).show()



                          }


                    }

                })

          //  Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()





    }

   private fun uploadpic(uri:Uri){


    storageReference=FirebaseStorage.getInstance().getReference("User/"+mAuth.currentUser?.uid+".jpg")
       storageReference.putFile(uri).addOnSuccessListener {

           mDbref = FirebaseDatabase.getInstance().getReference()
           progressBar.visibility=View.INVISIBLE
           Toast.makeText(this,"SIGNED UP SUCCESSFUL",Toast.LENGTH_SHORT).show()
           val intent = Intent(this,LOGIN::class.java)
           intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
           finish()
           startActivity(intent)


       }

           .addOnFailureListener{
               progressBar.visibility=View.INVISIBLE
               Toast.makeText(this,"FAILED TO UPLOAD IMG",Toast.LENGTH_SHORT).show()





           }


   }
}