package com.example.procoder

import android.net.Uri

class Users {
    var name:String?=null
    var email:String?=null
    var uid:String?=null
    var token:String?=null
    var status:String?=null
    constructor(){}

    constructor(name: String?, email: String?, uid: String?, token: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.token = token
    }

    constructor(status: String?) {
        this.status = status
    }

}