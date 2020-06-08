package com.bugaga.myroomlight

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [MyRoomLightWidgetConfigureActivity]
 */
const val WIDGET_SUNC = "WIDGET_SUNC"

class MyRoomLightWidget : AppWidgetProvider() {


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        var myAction = "null"
        if (intent != null) myAction = intent.action!!
        if (myAction.contains("goCrazy")){
            //output("Get Intent, ${intent.action}")
            val myIntent = Intent(context,myMqttService::class.java)

            if (myAction.contains("ON")) myIntent.putExtra("Data","ON")
            else myIntent.putExtra("Data","OFF")

            if (myAction.contains("1")) myIntent.putExtra("Topic","BuGaGa/feeds/Light1")
            else myIntent.putExtra("Topic","BuGaGa/feeds/Light2")

            context?.startService(myIntent)
        }
        super.onReceive(context, intent)
    }

    private fun sendRequest(context: Context?, data: String?){
        if (context != null && data != null) {
            val client = myMqttClient(context,"BuGaGa/feeds/SomeTest", "everything is working!")
            client.connect("BuGaGa/feeds/light1",data)
            output("Request sent")
        }
        else{
            output("Some fail")
        }
    }
    private fun sendRequest(context: Context?, data: Int){
        if (context != null) {
            val client = myMqttClient(context,"BuGaGa/feeds/SomeTest", "everything is working!")
            if (data == 1) client.connect("BuGaGa/feeds/light1","ON")
            else client.connect("BuGaGa/feeds/light1","OFF")
            output("Request sent")
        }
        else{
            output("Some fail")
        }
    }
}

private fun getIntent(context: Context, data: String): PendingIntent{

    val intent = Intent(context, MyRoomLightWidget::class.java)
    intent.action = "goCrazy"+data
    //intent.putExtra("Data",data)
    return PendingIntent.getBroadcast(context,0,intent,0)
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    //val views = RemoteViews(context.packageName, R.layout.my_room_light_widget)
    val views = RemoteViews(context.packageName,R.layout.my_room_light_widget)
    //views.setTextViewText(R.id.appwidget_text, widgetText)
    views.setOnClickPendingIntent(R.id.light1ON, getIntent(context,"ON1")) //"BuGaGa/feeds/light1"
    views.setOnClickPendingIntent(R.id.light1OFF, getIntent(context,"OFF1"))
    views.setOnClickPendingIntent(R.id.light2ON, getIntent(context,"ON2"))
    views.setOnClickPendingIntent(R.id.light2OFF, getIntent(context,"OFF2"))
    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}

private fun output(data:String){
    Log.w("myLog",data)
}