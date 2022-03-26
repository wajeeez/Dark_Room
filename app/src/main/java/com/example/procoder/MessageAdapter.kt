package com.example.procoder

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context:Context , val messageList:ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    val Item_Recieve=1
    val Item_Sent = 2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType==1){
            val view = LayoutInflater.from(context).inflate(R.layout.recieve,parent,false)
            return RecieveViewHolder(view)
        }else{
            val view = LayoutInflater.from(context).inflate(R.layout.send,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        if(holder.javaClass ==SentViewHolder::class.java){

            val viewHolder=holder as SentViewHolder
            holder.sent.text=currentMessage.message


        }else{
            val viewHolder = holder as RecieveViewHolder
            holder.recieve.text=currentMessage.message
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    override fun getItemViewType(position: Int): Int {
       val currentMessage=messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid .equals(currentMessage?.senderId)){

            return Item_Sent

        }else{
            return Item_Recieve
        }
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sent= itemView.findViewById<TextView>(R.id.txtsend)
    }

    class RecieveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val recieve= itemView.findViewById<TextView>(R.id.txtrecieve)
    }
}