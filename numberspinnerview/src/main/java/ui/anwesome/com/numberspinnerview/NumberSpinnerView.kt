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
        fun drawText(canvas:Canvas,paint:Paint,scale:Float,x:Float,y:Float,w:Float,dir:Int) {
            paint.color = Color.WHITE
            canvas.drawRoundRect(RectF(x,y-dir*w*scale,x+w,y+w-dir*w*scale),w/10,w/10,paint)
            paint.textSize = w/2
            paint.color = Color.BLACK
            canvas.drawText("$number",x+w/2,y+paint.textSize/4,paint)
        }
    }
    data class NumberNodeList(var w:Float,var h:Float,var n:Int,var mode:Int = 0) {
        var root:NumberNode = NumberNode(0)
        var currNode:NumberNode? = root
        var prevNode:NumberNode?=null
        val state = State()
        init {
            var node = root
            for(i in 1..n) {
                val curr = NumberNode(i)
                node.next = curr
                curr.prev = node
                node = curr
            }
            node.next = root
            root.prev = node
        }
        fun draw(canvas:Canvas,paint:Paint) {
            currNode?.drawText(canvas,paint,state.scale,w/2-w/10,h/2-w/10+(w/5)*mode,w/5,mode)
            prevNode?.drawText(canvas,paint,state.scale,w/2-w/10,h/2-w/10,w/5,mode)
        }
        fun startUpdating(dir:Int,startcb:()->Unit) {
            if(mode == 0) {
                mode = dir
                prevNode = currNode
                when(dir) {
                    1 -> currNode = currNode?.next
                    -1 -> currNode = currNode?.prev
                }
                state.startUpdating(startcb)
            }
        }
        fun update(stopcb:(Int)->Unit) {
            state.update {
                stopcb(currNode?.number?:-1)
                mode = 0
                prevNode = null
            }
        }
    }
    data class State(var scale:Float = 0f,var dir:Float = 0f,var prevScale:Float = 0f) {
        fun update(stopcb:()->Unit) {
            scale += 0.1f*dir
            if(Math.abs(scale - prevScale) > 1) {
                this.scale = 0f
                this.dir = 0f
                stopcb()
            }
        }
        fun startUpdating(startcb:()->Unit) {
            if(dir == 0f) {
                dir = 1 - 2 * scale
                startcb()
            }
        }
    }
    data class Animator(var view:NumberSpinnerView,var animated:Boolean = false) {
        fun animate(updatecb:()->Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex:Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class Renderer(var view:NumberSpinnerView,var time:Int = 0) {
        val animator = Animator(view)
        var w:Float = 0f
        var h:Float = 0f
        var numberNodeList:NumberNodeList?=null
        fun render(canvas:Canvas,paint:Paint) {
            if(time == 0) {
                w = canvas.width.toFloat()
                h = canvas.height.toFloat()
                numberNodeList = NumberNodeList(w,h,view.n)
            }
            numberNodeList?.draw(canvas,paint)
            time++
            animator.animate {
                numberNodeList?.update {
                    animator.stop()
                }
            }
        }
        fun handleTap(x:Float,y:Float) {
            var dir = 1
            if(y > h/2+h/10) {
                dir = 1
            }
            else if(y < h/2-h/10) {
                dir = -1
            }
            numberNodeList?.startUpdating(dir,{
                animator.start()
            })
        }
    }
}