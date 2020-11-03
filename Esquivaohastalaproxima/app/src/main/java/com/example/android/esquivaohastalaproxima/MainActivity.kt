package com.example.android.esquivaohastalaproxima


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Interpolator
import android.graphics.Rect
import android.graphics.RectF
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.graphics.contains
import androidx.core.os.postDelayed
import androidx.core.view.isGone
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : AppCompatActivity() {


    var time: Long = 1600
    lateinit var rock: ImageView
    lateinit var character: ImageView
    lateinit var context: Context
    var duration = 0
    var motionEventX: Float = 50f
    var motionEventY: Float = 50f
    var gameEnd: Boolean = false
    lateinit var mp: MediaPlayer
    var cont = 0

    val setList = MutableList(0) {AnimatorSet()}
    lateinit var newRockList : MutableList<AppCompatImageView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        setContentView(R.layout.activity_main)
        rock = findViewById(R.id.rock)
        character = findViewById(R.id.character)
        newRockList = MutableList(0) {AppCompatImageView(this)}
        mp = MediaPlayer.create(this,R.raw.hlp)
        startGame()
    }



    private fun startGame(){


        character.translationX = 450f
        character.translationY = 1000f
        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {

                if(gameEnd) lose()

                if(duration > 0) rockSpawn()
                duration ++

                var spawn = duration

                while(spawn > 10){
                    rockSpawn()
                    spawn -=18
                }


                if(duration == 15 || duration == 30 || duration == 45) time -= 100

                if(duration == 60){
                    win()
                }
                else if(!gameEnd) {
                    mainHandler.postDelayed(this, time)
                }
            }
        })
    }

    private fun collision(set : AnimatorSet, newRock : AppCompatImageView, cont : Int){

        val mainHandler = Handler(Looper.getMainLooper())

        mainHandler.post(object : Runnable {
            override fun run() {

                var pos = IntArray(2)
                newRockList.get(cont).getLocationOnScreen(pos)

                var rect = RectF()

                rect.left = motionEventX + character.width/2
                rect.top = motionEventY
                rect.bottom = motionEventY + character.height
                rect.right = motionEventX + character.width


                if(rect.contains(pos[0]+80f, pos[1]+0f)) lose()
                else if(!setList.get(cont).isRunning)
                else mainHandler.postDelayed(this, 0)

            }
        })
    }



    private fun win(){
        val builder = AlertDialog.Builder(context)
        builder.setIcon(R.drawable.rock)
            .setTitle("Enhorabuena")
            .setMessage("Has ganado")
            .setPositiveButtonIcon(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play))
        val dialog = builder.create()
        dialog.show()

    }

    private fun lose(){
        gameEnd = true
        mp.start()
        val builder = AlertDialog.Builder(context)
        builder.setIcon(R.drawable.rock)
            .setTitle("Vaya")
            .setMessage("Hasta la próxima")
            .setPositiveButtonIcon(ContextCompat.getDrawable(context, android.R.drawable.ic_media_play))
        val dialog = builder.create()
        dialog.show()

    }

    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        motionEventX = motionEvent.x - character.width
        motionEventY = motionEvent.y - character.height*2

        character.translationX = motionEventX
        character.translationY = motionEventY

        return true
    }

    private fun rockSpawn() {
        val container = rock.parent as ViewGroup
        val containerW = container.width
        val containerH = container.height

        val newRock = AppCompatImageView(this)
        newRock.setImageResource(R.drawable.rock)
        newRock.layoutParams = FrameLayout.LayoutParams(
            200,
            200)
        var rockW: Float = newRock.width.toFloat()
        var rockH: Float = newRock.height.toFloat()
        container.addView(newRock)
       // newRock.scaleX = Math.random().toFloat() * 0.8f + 0.4f
      //  newRock.scaleY = newRock.scaleX
     //   rockW *= newRock.scaleX
      //  rockH *= newRock.scaleY
        newRock.translationX = Math.random().toFloat() *
                containerW - rockW / 2

        val mover = ObjectAnimator.ofFloat(newRock, View.TRANSLATION_Y,
                -600f, containerH + rockH)
        mover.interpolator = AccelerateInterpolator(1f)
        val rotator = ObjectAnimator.ofFloat(newRock, View.ROTATION,
            (Math.random() * 720).toFloat())
        rotator.interpolator = LinearInterpolator()
        val set = AnimatorSet()
        set.playTogether(mover, rotator)
        if(duration < 4) set.duration = (Math.random() * 3000 + 9000-duration*5).toLong()
        else set.duration = (Math.random() * 2800 + 2400-duration*6).toLong()

        set.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationStart(animation: Animator?) {
                super.onAnimationStart(animation)
                setList.add(set)
                newRockList.add(newRock)
                collision(set, newRock, cont)
                cont++
            }

            override fun onAnimationEnd(animation: Animator?) {
                super.onAnimationEnd(animation)
                container.removeView(newRock)
            }
        })



        set.start()

    }


}