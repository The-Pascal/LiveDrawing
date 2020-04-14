package com.example.livedrawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.SurfaceView


class LiveDrawingView(
    context: Context,
    screenX : Int)
    :SurfaceView(context),Runnable
 {
     private val debugging = true

     private lateinit var canvas: Canvas
     private var paint: Paint = Paint()

     private var fps : Long = 0
     private val millisInSecond : Long = 1000

     private val fontSize : Int = screenX / 20
     private val fontMargin : Int = screenX/75

     private lateinit var thread: Thread

     @Volatile
     private var drawing: Boolean = false
     private var paused = true




     private fun draw()
     {
         if(holder.surface.isValid){

             canvas = holder.lockCanvas()
             canvas.drawColor(Color.argb(255,0,0,0))
             paint.color=Color.argb(255,255,255,255)
             paint.textSize=fontSize.toFloat()

             if(debugging){
                 printDebuggingText()
             }
             holder.unlockCanvasAndPost(canvas)
         }
     }

     private fun printDebuggingText(){

         val debugSize = fontSize/2
         val debugStart = 150
         paint.textSize = debugSize.toFloat()
         canvas.drawText("fps: $fps",
             10f, (debugStart + debugSize).toFloat(),paint)
     }

     override fun run() {

         while(drawing){

             val frameStartTime = System.currentTimeMillis()
             if(!paused){
                 update()
             }
             draw()
             val timeThisFrame = System.currentTimeMillis() - frameStartTime
             if(timeThisFrame > 0){
                 fps = millisInSecond / timeThisFrame
             }
         }

     }

     fun pause()
     {
         drawing = false
         try{
             thread.join()
         } catch (e : InterruptedException){
             Log.e("Error", "joining thread")
         }
     }
      fun resume(){
          drawing = true
          thread = Thread(this)
          thread.start()
      }

     private fun update(){

     }

 }