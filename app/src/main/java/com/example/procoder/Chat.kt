package com.example.procoder

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class Chat : AppCompatActivity() {

    var handler: Handler = Handler()
    var runnable: Runnable? = null
    var delay = 5000
    private lateinit var RV2:RecyclerView
    private lateinit var message:EditText
    private lateinit var sendbutton:ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList:ArrayList<Message>
    private lateinit var toolbar:androidx.appcompat.widget.Toolbar
    private lateinit var textView: TextView
    private lateinit var textView2: TextView
    private lateinit var anim:LottieAnimationView
    private lateinit var circleImageView: CircleImageView
    var senderuid = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var mdbref:DatabaseReference

    var recieverRoom: String?=null
    var senderRoom: String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        mdbref = FirebaseDatabase.getInstance().getReference()

        val name = intent.getStringExtra("name")
        val uid = intent.getStringExtra("uid")
        val token = intent.getStringExtra("token")
       // Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()

        senderRoom = uid+senderuid
        recieverRoom=senderuid+uid


        toolbar=findViewById(R.id.toolbar)
        textView=findViewById(R.id.user)

        textView2=findViewById(R.id.st)
        circleImageView=findViewById(R.id.prof)


        textView.text=name
        setSupportActionBar(toolbar)



        RV2 = findViewById(R.id.chatrv)
        message=findViewById(R.id.messagebox)
        sendbutton=findViewById(R.id.sendbtn)

        messageList= ArrayList()
        messageAdapter= MessageAdapter(this,messageList)



        var storageReference: StorageReference = FirebaseStorage.getInstance().getReference("User/$uid.jpg")

        val file = File.createTempFile("tempfile","jpg")

        storageReference.getFile(file).addOnSuccessListener {

            Glide.with(this).load(file).placeholder(R.drawable.progress)
                .into(circleImageView)

        }

        var dbref = FirebaseDatabase.getInstance().getReference()
        dbref.child("user").child(uid!!).child("status").get().addOnSuccessListener {

            if(it.value=="Offline"){
                textView2.setTextColor(Color.parseColor("#4e6654"))
                textView2.text=it.value.toString()

            }else{
                textView2.setTextColor(Color.parseColor("#4CAF50"))
                textView2.text=it.value.toString()
            }
        }


        /*  handler.postDelayed(Runnable {
              handler.postDelayed(runnable!!, delay.toLong())


                  var dbref = FirebaseDatabase.getInstance().getReference()
                  dbref.child("user").child(uid!!).child("status").get().addOnSuccessListener {

                      if(it.value=="Offline"){
                          textView2.setTextColor(Color.parseColor("#4e6654"))
                          textView2.text=it.value.toString()

                      }else{
                          textView2.setTextColor(Color.parseColor("#4CAF50"))
                          textView2.text=it.value.toString()
                      }
                  }



              Toast.makeText(this, "This method will run every 5 seconds", Toast.LENGTH_SHORT).show()
          }.also { runnable = it }, delay.toLong())



  */



        RV2.layoutManager=LinearLayoutManager(this).apply {
            reverseLayout=false
            stackFromEnd =true
        }

        RV2.adapter = messageAdapter


        mdbref.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()
                    for(postsnapshot in snapshot.children){
                        val message=postsnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })


        sendbutton.setOnClickListener{
            //notification code her


            val text= message.text.toString()
            if(text.isEmpty()){
                Toast.makeText(this,"Please Type a message",Toast.LENGTH_SHORT).show()

            }else{
            val messageObject =Message(text,senderuid)
            mdbref.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mdbref.child("chats").child(recieverRoom!!).child("messages").push()
                        .setValue(messageObject)

                }
            }
            RV2.smoothScrollToPosition(messageList.size)
            message.setText("")
            var sentname:String?=null
            mdbref.child("user").child(senderuid!!).child("name").get().addOnSuccessListener {
                sentname=it.value.toString()

                sendnotification(sentname!!,text,token!!)
            }.addOnFailureListener{

            }


        }


    }





    private fun sendnotification(name:String,message:String,token:String){
        try {
            var requestQueue: RequestQueue = Volley.newRequestQueue(this)
            var url: String = "https://fcm.googleapis.com/fcm/send"
            var data: JSONObject = JSONObject()
            data.put("title", name)
            data.put("body", message)
            var notification:JSONObject = JSONObject()
            notification.put("notification",data)
            notification.put("to",token)


            val request: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                url,
                notification,
                Response.Listener { response: JSONObject ->

                    Log.d("TAG", "onResponse: $response")
                },
                Response.ErrorListener {

                    Log.d("TAG", "onError: $it")
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    var key:String = "AAAAiwbOKCo:APA91bFzoIiti37TQGLtuW1iSCrUGxA7LZy9JOzQthGdlLiRjB9nLP3CgR3Y_F5cEVSkGzAIRHJf6k7YH7J6DdecLbJXQtD1-9Qfy9Lhinzt_Poikpwts-c03b4knW46BVdwBdw8BrU5"


                    val map: MutableMap<String, String> = HashMap()

                    map["Authorization"] = "key=" + key
                    map["Content-type"] = "application/json"
                    return map
                }

            }
            requestQueue.add(request)

        }catch (e:Exception){

        }

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

    private fun typingstatus(typing:String){

        val databaseReference =
            FirebaseDatabase.getInstance().getReference()


        val map = HashMap<String, Any>()
        map["typing"] = typing
        databaseReference.child("user").child(senderuid!!).updateChildren(map)
        Log.d("TYPING ",typing)


    }





}

