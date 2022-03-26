package com.example.procoder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class Profile : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var profile: CircleImageView
    private lateinit var back: ImageView
    private lateinit var pname:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        profile=findViewById(R.id.profile)
        back=findViewById(R.id.back)
        pname=findViewById(R.id.pname)

        mAuth= FirebaseAuth.getInstance()


        supportActionBar?.hide()

        back.setOnClickListener { View->
            finish()
        }

        var dbref = FirebaseDatabase.getInstance().getReference()
        dbref.child("user").child(mAuth.currentUser?.uid!!).child("name").get().addOnSuccessListener {
            pname.text =it.value.toString()
        }





        var storageReference: StorageReference = FirebaseStorage.getInstance().getReference("User/" + mAuth.currentUser?.uid + ".jpg")
        val file = File.createTempFile("tempfile", "jpg")
        storageReference.getFile(file).addOnSuccessListener {
            Glide.with(applicationContext).load(file).placeholder(R.drawable.progress)
                .into(profile)
        }





    }
}