package com.bugaga.myroomlight

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.util.Log
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class myMqttClient(private val context: Context,val Topic:String, val Message:String): AsyncTask<Void, Void, Void>() {

    private var _connectionStatus = 0
    val client by lazy {
        val clientId = MqttClient.generateClientId()
        //MqttAndroidClient(context, "tcp://192.168.1.129:1883", clientId)
        MqttAndroidClient(context, "tcp://io.adafruit.com", clientId)
    }
    init {
        execute()
    }

    override fun doInBackground(vararg params: Void?): Void? {
        connect(Topic,Message)

        return null
    }

    fun connect(topic: String,data: String) {
        try {
            val options = MqttConnectOptions()
            options.userName = "BuGaGa"
            options.password = "aio_xwYt514kazXaPXHRvoq867iFvgzl".toCharArray()
            client.connect(options)
            client.setCallback(object : MqttCallbackExtended {
                override fun connectComplete(reconnect: Boolean, serverURI: String) {
                    output( "Connected to: $serverURI")
                    _connectionStatus = 1
                    publishMessage(topic,data)
                    val offIntent = Intent(context, MyRoomLightWidget::class.java)
                    offIntent.action = "offIntent"
                    context.sendBroadcast(offIntent)
                }

                override fun connectionLost(cause: Throwable) {
                    output( "The Connection was lost.")
                    _connectionStatus = -1
                }

                @Throws(Exception::class)
                override fun messageArrived(topic: String, message: MqttMessage) {
                    output("Incoming message from $topic: " + message.toString())
                    //messageCallBack?.invoke(topic, message)
                }

                override fun deliveryComplete(token: IMqttDeliveryToken) {
                    output("Delivery Complete, starting intent")
                    val offIntent = Intent(context, MyRoomLightWidget::class.java)
                    offIntent.action = "offIntent"
                    context.sendBroadcast(offIntent)
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    fun subscribeTopic(topic: String, qos: Int = 0) {
        client.subscribe(topic, qos).actionCallback = object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                output( "Subscribed to $topic")
            }

            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                output( "Failed to subscribe to $topic")
                exception.printStackTrace()
            }
        }
    }

    fun publishMessage(topic: String, msg: String) {

        try {
            val message = MqttMessage()
            message.payload = msg.toByteArray()
            client.publish(topic, message.payload, 0, true)
            output( "$msg published to $topic")
            close()
        } catch (e: MqttException) {
            output( "Error Publishing to $topic: " + e.message)
            e.printStackTrace()
        }
    }

    fun isConnected():Int{
        if(client.isConnected) _connectionStatus = 1
        return _connectionStatus
    }
    fun close() {
        client.apply {
            unregisterResources()
            close()
        }
    }

    private fun output(data:String){
        Log.w("myLog",data)
    }



}