package com.example.livedrawing

import android.content.Context
import android.graphics.*
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.MotionEvent
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

     private var resetButton: RectF
     private var togglePauseButton: RectF
     private var setting: RectF

     private val particleSystems = ArrayList<ParticleSystem>()

     private var nextSystem = 0
     private val maxSystems = 1000
     private val particlesPerSystem = 100

     init {
         resetButton = RectF(30f , 40f, 200f , 100f)
         togglePauseButton = RectF(30f, 140f , 200f , 200f)
         setting = RectF(700f,100f,100f,500f)


         for(i in 0 until maxSystems){
             particleSystems.add(ParticleSystem())

             particleSystems[i]
                 .initParticles(particlesPerSystem)
         }

     }





     private fun draw()
     {
         if(holder.surface.isValid){

             canvas = holder.lockCanvas()
             canvas.drawColor(Color.argb(255,0,0,0))
             paint.color=Color.argb(255,255,255,255)


             for(i in 0 until nextSystem){
                 particleSystems[i].draw(canvas , paint)
             }

             canvas.drawRect(resetButton, paint)
             canvas.drawRect(togglePauseButton, paint)
             canvas.drawRect(setting,paint)


             paint.color=Color.argb(255,255,0,0)

             paint.textSize = 60f
             canvas.drawText("Reset",
                 44f , 85f
                 ,paint)

             canvas.drawText("Magic",
                 42f , 185f
                 ,paint)

             paint.textSize=fontSize.toFloat()
             paint.color=Color.argb(255,255,255,51)

             if(debugging){
                 printDebuggingText()
             }
             holder.unlockCanvasAndPost(canvas)
         }
     }

     private fun printDebuggingText(){

         val debugSize = fontSize/2
         val debugStart = 250
         paint.textSize = debugSize.toFloat()
         canvas.drawText("fps: $fps",
             50f, (debugStart + debugSize).toFloat(),paint)

         canvas.drawText("Systems: $nextSystem",
             10f , (fontMargin + debugStart + debugSize *2).toFloat()
         ,paint)

         canvas.drawText("Particles : ${nextSystem * particlesPerSystem}",
             10f, (fontMargin + debugStart + debugSize * 3).toFloat(),
             paint)
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

         for( i in 0 until particleSystems.size){
             if(particleSystems[i].isRunning){
                 particleSystems[i].update(fps)
             }
         }
     }

     override fun onTouchEvent(event: MotionEvent): Boolean {

         if(event.action and MotionEvent
                 .ACTION_MASK==
                 MotionEvent.ACTION_MOVE){

             particleSystems[nextSystem].emitParticles(
                 PointF(event.x, event.y)
             )

             nextSystem++

             if(nextSystem == maxSystems){
                 nextSystem = 0
             }
         }

         if(event.action and MotionEvent.ACTION_MASK==
                 MotionEvent.ACTION_DOWN){
             if(resetButton.contains(event.x,event.y)){
                 nextSystem=0
             }

             if(togglePauseButton.contains(event.x, event.y)){
                 paused = !paused
             }

             if(setting.contains(event.x, event.y)){

             }
         }

         return true
     }

 }