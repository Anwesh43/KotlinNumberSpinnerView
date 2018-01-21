package ui.anwesome.com.numberspinnerview

/**
 * Created by anweshmishra on 21/01/18.
 */
import android.app.Activity
import android.view.*
import android.graphics.*
import android.content.*
import java.util.concurrent.ConcurrentLinkedQueue

class NumberSpinnerView(ctx:Context,var n:Int = 12):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    override fun onDraw(canvas:Canvas) {
        renderer.render(canvas,paint)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap(event.x,event.y)
            }
        }
        return true
    }
    data class NumberNode(var number:Int) {
        var next:NumberNode?=null
        var prev:NumberNode?=null
        fun drawText(canvas:Canvas,paint:Paint,scale:Float,x:Float,y:Float,w:Float,dir:Int) {
            paint.color = Color.WHITE
            canvas.save()
            canvas.translate(x+w/2,y-dir*w*scale+w/2)
            canvas.drawRect(RectF(-w/2,-w/2,w/2,w/2),paint)
            paint.textSize = w/2
            paint.color = Color.BLACK
            canvas.drawText("$number",-paint.measureText("$number")/2,paint.textSize/4,paint)
            canvas.restore()
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
            canvas.save()
            val path = Path()
            path.addRoundRect(w/2-w/10,h/2-w/10,w/2+w/10,h/2+w/10,w/25,w/25,Path.Direction.CW)
            canvas.clipPath(path)
            currNode?.drawText(canvas,paint,state.scale,w/2-w/10,h/2-w/10+(w/5)*mode,w/5,mode)
            prevNode?.drawText(canvas,paint,state.scale,w/2-w/10,h/2-w/10,w/5,mode)
            canvas.restore()
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
        fun update(updatecb:(Float)->Unit,stopcb:(Int)->Unit) {
            state.update {
                stopcb(currNode?.number?:-1)
                mode = 0
                prevNode = null
            }
            updatecb(state.scale)
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
        var numberSpinner:NumberSpinner?=null
        fun render(canvas:Canvas,paint:Paint) {
            if(time == 0) {
                w = canvas.width.toFloat()
                h = canvas.height.toFloat()
                numberSpinner = NumberSpinner(w,h,view.n)
            }
            canvas.drawColor(Color.parseColor("#212121"))
            numberSpinner?.draw(canvas,paint)
            time++
            animator.animate {
                numberSpinner?.update({
                    animator.stop()
                })
            }
        }
        fun handleTap(x:Float,y:Float) {
            numberSpinner?.handleTap(x,y,{
                animator.start()
            })
        }
    }
    data class ArrowBtn(var x:Float,var y:Float,var size:Float,var dir:Int,var scale:Float = 0f,var selected:Boolean = false) {
        fun draw(canvas:Canvas,paint:Paint) {
            canvas.save()
            canvas.translate(x,y)
            canvas.rotate(90f*(1+dir))
            val path = Path()
            path.moveTo(-size/2,size/2)
            path.lineTo(size/2,size/2)
            path.lineTo(0f,-size/2)
            path.lineTo(-size/2,size/2)
            paint.color = Color.WHITE
            canvas.drawPath(path,paint)
            paint.color = Color.parseColor("#99FAFAFA")
            canvas.save()
            canvas.scale(scale,scale)
            canvas.drawPath(path,paint)
            canvas.restore()
            canvas.restore()
        }
        fun setSelectedBtn(selected: Boolean) {
            this.selected = selected
        }
        fun update(scale:Float) {
            this.scale = scale
        }
        fun handleTap(x:Float,y:Float):Boolean = x>=this.x-size/2 && x<=this.x+size/2 && y>=this.y-size/2 && y<=this.y+size/2
    }
    data class NumberSpinner(var w:Float,var h:Float,var n:Int) {
        var btns:ConcurrentLinkedQueue<ArrowBtn> = ConcurrentLinkedQueue()
        var numberNodeList:NumberNodeList = NumberNodeList(w,h,n)
        init {
            btns.add(ArrowBtn(w/2,h/2-w/5,w/20,-1))
            btns.add(ArrowBtn(w/2,h/2+w/5,w/20,1))
        }
        fun draw(canvas:Canvas,paint:Paint) {
            btns.forEach { btn ->
                btn.draw(canvas,paint)
            }
            numberNodeList.draw(canvas,paint)
        }
        fun update(stopcb: (Int) -> Unit) {
            var selectedBtns = btns.filter {
                it.selected
            }
            if(selectedBtns.size == 1) {
                var selectedBtn = selectedBtns[0]
                numberNodeList.update({
                    selectedBtn.update(it)
                },{
                    selectedBtn.setSelectedBtn(false)
                    selectedBtn.scale = 0f
                    stopcb(it)
                })
            }
        }
        fun handleTap(x:Float,y:Float,startcb: () -> Unit) {
            btns.forEach {
                if(it.handleTap(x,y)) {
                    it.setSelectedBtn(true)
                    numberNodeList.startUpdating(it.dir,startcb)
                    return
                }
            }
        }
    }
    companion object {
        fun create(activity:Activity):NumberSpinnerView {
            val view = NumberSpinnerView(activity)
            activity.setContentView(view)
            return view
        }
    }
}