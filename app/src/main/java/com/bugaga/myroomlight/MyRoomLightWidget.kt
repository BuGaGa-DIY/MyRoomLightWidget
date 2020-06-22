package com.bugaga.myroomlight

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
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


    var myAppWidgetManager: AppWidgetManager? = null
    private fun getIntent(context: Context, data: String): PendingIntent{

        val intent = Intent(context, MyRoomLightWidget::class.java)
        intent.action = "goCrazy"+data
        //intent.putExtra("Data",data)
        return PendingIntent.getBroadcast(context,0,intent,0)
    }
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        myAppWidgetManager = appWidgetManager
        for (appWidgetId in appWidgetIds) {
            //updateAppWidget(context, appWidgetManager, appWidgetId)
            val views = RemoteViews(context.packageName,R.layout.my_room_light_widget)
            //views.setTextViewText(R.id.appwidget_text, widgetText)
            views.setOnClickPendingIntent(R.id.light1ON, getIntent(context,"ON1")) //"BuGaGa/feeds/light1"
            views.setOnClickPendingIntent(R.id.light1OFF, getIntent(context,"OFF1"))
            views.setOnClickPendingIntent(R.id.light2ON, getIntent(context,"ON2"))
            views.setOnClickPendingIntent(R.id.light2OFF, getIntent(context,"OFF2"))
            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
            output("Widget Id: $appWidgetId is Updated")
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
        if (context == null) return
        if (intent != null) myAction = intent.action!!
        else return
        output("Received not null intent: $myAction")
        val myIntent = Intent(context,myMqttService::class.java)
        when(myAction){
            "goCrazyON1"->{
                updateAppWidget(context,R.drawable.img_light_off,R.id.light1ON)
                myIntent.putExtra("Data","ON")
                myIntent.putExtra("Topic","BuGaGa/feeds/Light1")
                context.startService(myIntent)
            }
            "goCrazyON2"-> {
                updateAppWidget(context,R.drawable.img_light_off,R.id.light2ON)
                myIntent.putExtra("Data", "ON")
                myIntent.putExtra("Topic", "BuGaGa/feeds/Light2")
                context.startService(myIntent)
            }
            "goCrazyOFF1"-> {
                updateAppWidget(context,R.drawable.img_light_on,R.id.light1OFF)
                myIntent.putExtra("Data", "OFF")
                myIntent.putExtra("Topic", "BuGaGa/feeds/Light1")
                context.startService(myIntent)
            }
            "goCrazyOFF2"-> {
                updateAppWidget(context,R.drawable.img_light_on,R.id.light2OFF)
                myIntent.putExtra("Data", "OFF")
                myIntent.putExtra("Topic", "BuGaGa/feeds/Light2")
                context.startService(myIntent)
            }


            "1ON"->{
                output("1ON received")
                updateAppWidget(context,R.drawable.img_light_on,R.id.light1ON)
            }
            "1OFF"->{
                output("1OFF received")
                updateAppWidget(context,R.drawable.img_light_off,R.id.light1OFF)
            }
            "2ON"->{
                output("1ON received")
                updateAppWidget(context,R.drawable.img_light_on,R.id.light2ON)
            }
            "2OFF"->{
                output("1OFF received")
                updateAppWidget(context,R.drawable.img_light_off,R.id.light2OFF)
            }
        }

        super.onReceive(context, intent)
    }


}



internal fun updateAppWidget(
    context: Context, condition:Int, id:Int
) {
    //val views = RemoteViews(context.packageName, R.layout.my_room_light_widget)
    val views = RemoteViews(context.packageName,R.layout.my_room_light_widget)

    // Instruct the widget manager to update the widget
    views.setImageViewResource(id,condition)

    val awm = AppWidgetManager.getInstance(context)
    awm.updateAppWidget(awm.getAppWidgetIds(ComponentName(context,MyRoomLightWidget::class.java)), views)
}

private fun output(data:String){
    Log.w("myLog",data)
}