package com.bugaga.myroomlight

import android.app.IntentService
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.lang.Exception

class myMqttService: IntentService(myMqttService::class.java.toString()) {



    override fun onHandleIntent(intent: Intent?) {
        output("mqtt service got intent and ready to start")
        if (intent != null){
            val myTopic = intent.getStringExtra("Topic")
            val myData = intent.getStringExtra("Data")
            output("Sending Topic: $myTopic; Msg: $myData")
            try {
                myMqttClient(applicationContext,myTopic,myData)
                //myRequest.execute()
            }catch (e: Exception){
                output("Some error appears while creating client")
            }
        }
    }

    private fun output(data:String){
        Log.w("myLog",data)
    }
}