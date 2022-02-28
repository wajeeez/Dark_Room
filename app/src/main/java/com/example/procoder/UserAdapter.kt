package com.example.procoder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.internal.Storage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import java.io.File

class UserAdapter(val context:Context, val userList:ArrayList<Users>) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.user_layout,parent,false)
        return UserViewHolder(view)

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

// DATA ATTACHMENT
      val currentUser=userList[position]

       var dbref = FirebaseDatabase.getInstance().getReference()
        dbref.child("user").child(currentUser.uid!!).child("status").get().addOnSuccessListener {

            if(it.value=="Offline"){
                holder.status.setTextColor(Color.parseColor("#4e6654"))
                holder.status.text=it.value.toString()
            }else{
                holder.status.setTextColor(Color.parseColor("#4CAF50"))
                holder.status.text=it.value.toString()
            }


        }

      holder.txtname.text=currentUser.name




        var storageReference:StorageReference = FirebaseStorage.getInstance().getReference("User/"+currentUser.uid+".jpg")

        val file =File.createTempFile("tempfile","jpg")

        storageReference.getFile(file).addOnSuccessListener {

            Glide.with(context).load(file).placeholder(R.drawable.progress)
                .into(holder.profimg)

        }


      holder.itemView.setOnClickListener{

          val intent=Intent(context,Chat::class.java)
          intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
          intent.putExtra("name",currentUser.name)
          intent.putExtra("uid",currentUser.uid)
          intent.putExtra("token",currentUser.token)
          context.startActivity(intent)
      }

    }

    override fun getItemCount(): Int {
       return userList.size
    }

    class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val txtname = itemView.findViewById<TextView>(R.id.user)
        val profimg= itemView.findViewById<CircleImageView>(R.id.profile)
        val status = itemView.findViewById<TextView>(R.id.status)
    }

}

