package com.example.livedrawing

import android.graphics.PointF
import java.util.*
import kotlin.collections.ArrayList

class ParticleSystem {

    private var duration : Float = 0f
    private var particles : ArrayList<Particle> = ArrayList()

    private val random = Random()
    var isRunning = false

    fun initParticles(numParticles : Int){

        for ( i in 0 until numParticles){

            var angle : Double = random.nextInt(360).toDouble()
            angle *= (3.14 / 180)

            //val speed = random.nextFloat() / 3

            val speed = (random.nextInt(10) + 1);

            val direction : PointF
            direction = PointF(Math.cos(
                angle).toFloat() * speed ,
                Math.sin(angle).toFloat() * speed
            )

            particles. add ( Particle(direction))

        }
    }
}