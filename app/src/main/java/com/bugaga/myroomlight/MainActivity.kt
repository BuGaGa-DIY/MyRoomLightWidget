package com.bugaga.myroomlight

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var client: myMqttClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val context = this.applicationContext

        SendBT.setOnClickListener {
            //client?.connect("BuGaGa/feeds/SomeTest", "everything is working!")
            //client = myMqttClient(context,"BuGaGa/feeds/SomeTest", "everything is working!")
            val myIntent = Intent(context,myMqttService::class.java)
            myIntent.putExtra("Topic","BuGaGa/feeds/SomeTest")
            myIntent.putExtra("Data","everything is working!")
            context.startService(myIntent)

        }
        finish()
    }
}


