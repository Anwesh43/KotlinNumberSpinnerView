package ui.anwesome.com.numberspinnerview

/**
 * Created by anweshmishra on 21/01/18.
 */
import android.view.*
import android.graphics.*
import android.content.*
class NumberSpinnerView(ctx:Context,var n:Int = 12):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class NumberNode(var number:Int) {
        var next:NumberNode?=null
        var prev:NumberNode?=null
        fun drawPosText(canvas:Canvas,paint:Paint,scale:Float,x:Float,y:Float,w:Float) {
            paint.color = Color.WHITE
            canvas.drawRoundRect(RectF(x,y-w*scale,x+w,y+w-w*scale),w/10,w/10,paint)
            paint.textSize = w/2
            paint.color = Color.BLACK
            canvas.drawText("$number",x+w/2,y+paint.textSize/4,paint)
        }
    }
}