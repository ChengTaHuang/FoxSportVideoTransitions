package zeno.com.foxsportvideotransitions

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.view.View
import java.lang.ref.WeakReference


/**
 * Created by zeno on 2018/3/16.
 */

class FoxSportView : View {
    private var start = false

    private var innerCircleRectF = RectF(0f, 0f, 0f, 0f)

    private val arcStrokeWidth = 15f

    private var arcAcc = 0f

    private lateinit var upperArc: Arc
    private lateinit var downerArc: Arc

    private val lineStrokeWidth = 20f

    private lateinit var line1: Line
    private  lateinit var line2: Line
    private lateinit var line3: Line

    private lateinit var line4: Line
    private lateinit var line5: Line
    private lateinit var line6: Line

    private lateinit var outerCircle : Circle

    private val outerCircleStrokeWidth = 15f
    private var outerCircleRadiusStep = 8f

    private lateinit var bottomCircle : Circle
    private lateinit var middleCircle : Circle
    private lateinit var topCircle : Circle

    private var bottomCircleAcc = 2f
    private var middleCircleAcc = 2f
    private var topCircleAcc = 2f

    private var centerX = 0f
    private var centerY = 0f

    private val circleLineList: ArrayList<Line> = ArrayList()
    private lateinit var circleLinePaint : Paint
    private val circleLineStrokeWidth = 5f

    private var delayMillis = 10L

    private var innerRadius = 0f
    private var outerRadius = 0f

    private lateinit var animationFlow : AnimationFlow

    private val startStep1Animation = 1
    private val startStep2Animation = 2
    private val startStep3Animation = 3
    private val startStep4Animation = 4
    private val resetScreen = 5

    constructor(context: Context) : super(context) {
        animationFlow = AnimationFlow(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        animationFlow = AnimationFlow(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        animationFlow = AnimationFlow(context)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        centerX = w / 2.0f
        centerY = h / 2.0f
        val oneThirdWidth = width / 3.0f

        //is a square
        innerCircleRectF = RectF(oneThirdWidth, centerY - oneThirdWidth / 2, 2 * oneThirdWidth, centerY + oneThirdWidth / 2)

        initLines()

        initArc()

        initOuterCircle()

        innerRadius = outerCircle.maxRadius + outerCircleStrokeWidth/2
        outerRadius = outerCircle.maxRadius + outerCircleStrokeWidth/2

        initLayerCircle()

        circleLinePaint = createPaint(Color.WHITE,circleLineStrokeWidth)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animationFlow.stopAll()
    }

    private fun initLines() {

        val oneThirdWidth = width / 3.0f
        val baseLineWidth = width / 5
        val baseRightLineStartX = centerX + oneThirdWidth / 5
        val baseRightLineY = centerY - oneThirdWidth / 5
        val margin = 20f

        //define right side line property
        line1 = Line(baseRightLineStartX, baseRightLineY, baseRightLineStartX + baseLineWidth, baseRightLineY, 8f)
        line2 = Line(baseRightLineStartX + margin, baseRightLineY + lineStrokeWidth, baseRightLineStartX + baseLineWidth + margin, baseRightLineY + lineStrokeWidth, 10f)
        line3 = Line(baseRightLineStartX + margin * 2, baseRightLineY + lineStrokeWidth * 2, baseRightLineStartX + baseLineWidth + margin * 2, baseRightLineY + lineStrokeWidth * 2, 12f)

        line1.paint = createPaint(ContextCompat.getColor(context, R.color.orange), lineStrokeWidth)
        line2.paint = createPaint(ContextCompat.getColor(context, R.color.cyan), lineStrokeWidth)
        line3.paint = createPaint(Color.RED, lineStrokeWidth)

        val baseLeftLineStartX = centerX - oneThirdWidth / 5
        val baseLeftLineY = centerY + oneThirdWidth / 5

        //define left side line property
        line4 = Line(baseLeftLineStartX, baseLeftLineY, baseLeftLineStartX - baseLineWidth, baseLeftLineY, 8f)
        line5 = Line(baseLeftLineStartX - margin, baseLeftLineY - lineStrokeWidth, baseLeftLineStartX - baseLineWidth - margin, baseLeftLineY - lineStrokeWidth, 10f)
        line6 = Line(baseLeftLineStartX - margin * 2, baseLeftLineY - lineStrokeWidth * 2, baseLeftLineStartX - baseLineWidth - margin * 2, baseLeftLineY - lineStrokeWidth * 2, 12f)

        line4.paint = createPaint(ContextCompat.getColor(context, R.color.orange), lineStrokeWidth)
        line5.paint = createPaint(ContextCompat.getColor(context, R.color.cyan), lineStrokeWidth)
        line6.paint = createPaint(Color.RED, lineStrokeWidth)

    }

    private fun initArc(){
        upperArc = Arc(-180f, 180f)
        downerArc = Arc(0f, 180f)

        upperArc.paint = createPaint(Color.WHITE, arcStrokeWidth)
        downerArc.paint = createPaint(Color.WHITE, arcStrokeWidth)
    }

    private fun initOuterCircle(){
        outerCircle = Circle(centerX , centerY , width/4.0f , 0f)
        outerCircle.paint = createPaint(ContextCompat.getColor(context, R.color.transparent_orange), outerCircleStrokeWidth, Paint.Style.FILL_AND_STROKE)
    }

    private fun initLayerCircle(){

        bottomCircle = Circle(centerX,centerY,width / 10f,0f)
        middleCircle = Circle(centerX,centerY,width / 10f,0f)
        topCircle = Circle(centerX,centerY,width / 10f, 0f)

        bottomCircle.paint = createPaint(ContextCompat.getColor(context, R.color.cyan), 15f, Paint.Style.FILL_AND_STROKE)
        middleCircle.paint = createPaint(ContextCompat.getColor(context, R.color.red), 15f, Paint.Style.FILL_AND_STROKE)
        topCircle.paint = createPaint(ContextCompat.getColor(context, R.color.orange), 15f, Paint.Style.FILL_AND_STROKE)
    }

    private fun createPaint(color: Int, stroke: Float, style: Paint.Style = Paint.Style.STROKE): Paint {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = style
        paint.color = color
        paint.strokeWidth = stroke
        return paint
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if(start) {
            canvas.drawArc(innerCircleRectF, upperArc.startAngle, upperArc.curAngle, false, upperArc.paint)
            canvas.drawArc(innerCircleRectF, downerArc.startAngle, downerArc.curAngle, false, downerArc.paint)

            canvas.drawLine(line1.startX, line1.startY, line1.endX, line1.endY, line1.paint)
            canvas.drawLine(line2.startX, line2.startY, line2.endX, line2.endY, line2.paint)
            canvas.drawLine(line3.startX, line3.startY, line3.endX, line3.endY, line3.paint)

            canvas.drawLine(line4.startX, line4.startY, line4.endX, line4.endY, line4.paint)
            canvas.drawLine(line5.startX, line5.startY, line5.endX, line5.endY, line5.paint)
            canvas.drawLine(line6.startX, line6.startY, line6.endX, line6.endY, line6.paint)

            if(outerCircle.radius > outerCircle.initRadius)
                canvas.drawCircle(centerX, centerY, outerCircle.radius, outerCircle.paint)

            for (line in circleLineList) {
                canvas.drawLine(line.startX, line.startY, line.endX, line.endY, circleLinePaint)
            }

            if(bottomCircle.radius > bottomCircle.initRadius)
                canvas.drawCircle(bottomCircle.centerX, bottomCircle.centerY, bottomCircle.radius, bottomCircle.paint)
            if(middleCircle.radius > middleCircle.initRadius)
                canvas.drawCircle(middleCircle.centerX, middleCircle.centerY, middleCircle.radius, middleCircle.paint)
            if(topCircle.radius > topCircle.initRadius)
                canvas.drawCircle(topCircle.centerX, topCircle.centerY, topCircle.radius, topCircle.paint)
        }
    }

    /*
        if start is true reset canvas to empty
        else start drawing
     */
    fun start() {
        start = if (start) {
            reset()
            false
        } else {
            animationFlow.sendEmptyMessage(startStep1Animation)
            true
        }
    }

    @SuppressLint("HandlerLeak")
    inner class AnimationFlow(context : Context) : Handler() {
        private val weakContext: WeakReference<Context> = WeakReference(context)

        override fun handleMessage(msg: Message) {
            val context = weakContext.get()
            if (context != null) {
                when (msg.what) {
                    startStep1Animation -> {
                        step1Runnable.run()
                    }
                    startStep2Animation -> {
                        step2Runnable.run()
                    }
                    startStep3Animation -> {
                        step3Runnable.run()
                    }
                    startStep4Animation -> {
                        step4Runnable.run()
                    }
                    resetScreen -> {
                        reset()
                    }
                }
            }
        }

        fun stopStep1(){
            removeCallbacks(step1Runnable)
        }

        fun stopStep2(){
            removeCallbacks(step2Runnable)
        }

        fun stopStep3(){
            removeCallbacks(step3Runnable)
        }

        fun stopStep4(){
            removeCallbacks(step4Runnable)
        }

        fun stopAll(){
            stopStep1()
            stopStep2()
            stopStep3()
            stopStep4()
        }
    }

    private var step1Runnable: Runnable = object : Runnable {
        override fun run() {
            invalidate()

            if (upperArc.curAngle < upperArc.sweepAngle) {
                upperArc.curAngle += upperArc.stepAngle
                upperArc.curAngle += arcAcc
            } else {
                upperArc.curAngle = upperArc.sweepAngle
            }

            if (downerArc.curAngle < downerArc.sweepAngle) {
                downerArc.curAngle += downerArc.stepAngle
                downerArc.curAngle += arcAcc
            } else {
                downerArc.curAngle = downerArc.sweepAngle
            }

            arcAcc += 0.5f


            if (line1.startX < line1.endX) {
                line1.startX += line1.stepX
            } else {
                line1.startX = line1.endX
            }

            if (line2.startX < line2.endX) {
                line2.startX += line2.stepX
            } else {
                line2.startX = line2.endX
            }

            if (line3.startX < line3.endX) {
                line3.startX += line3.stepX
            } else {
                line3.startX = line3.endX
            }

            if (line4.startX > line4.endX) {
                line4.startX -= line4.stepX
            } else {
                line4.startX = line4.endX
            }

            if (line5.startX > line5.endX) {
                line5.startX -= line5.stepX
            } else {
                line5.startX = line5.endX
            }

            if (line6.startX > line6.endX) {
                line6.startX -= line6.stepX
            } else {
                line6.startX = line6.endX
            }


            if (upperArc.curAngle == upperArc.sweepAngle && downerArc.curAngle == downerArc.sweepAngle
                    && line1.startX == line1.endX && line2.startX == line2.endX && line3.startX == line3.endX
                    && line4.startX == line4.endX && line5.startX == line5.endX && line6.startX == line6.endX)
                animationFlow.sendEmptyMessage(startStep2Animation)
            else
                animationFlow.postDelayed(this, delayMillis)
        }
    }

    private var step2Runnable: Runnable = object : Runnable {
        override fun run() {
            invalidate()
            if (outerCircle.radius < outerCircle.maxRadius)
                outerCircle.radius += outerCircleRadiusStep
            else
                outerCircle.radius = outerCircle.maxRadius

            if (outerCircle.radius < outerCircle.maxRadius)
                animationFlow.postDelayed(this, delayMillis)
            else
                animationFlow.sendEmptyMessage(startStep3Animation)
        }

    }

    private var step3Runnable: Runnable = object : Runnable {
        override fun run() {
            invalidate()

            circleLineList.clear()
            var i = 0
            var j = 0

            while (j <= 10) {
                val smallX = centerX + (innerRadius) * Math.cos(i.toDouble())
                val smallY = centerY + (innerRadius) * Math.sin(i.toDouble())

                val bigX = centerX + (outerRadius) * Math.cos(i.toDouble())
                val bigY = centerY + (outerRadius) * Math.sin(i.toDouble())

                circleLineList.add(Line(smallX.toFloat(), smallY.toFloat(), bigX.toFloat(), bigY.toFloat() , 0f))

                i += 36
                j++
            }


            val lineSmallerRadius = innerCircleRectF.height() / 2 + arcStrokeWidth / 2
            val lineBiggerRadius = outerCircle.maxRadius + outerCircleStrokeWidth / 2

            if(innerRadius > lineSmallerRadius && outerRadius == lineBiggerRadius){
                innerRadius -= 4f
                animationFlow.postDelayed(this, delayMillis)
            }else if(innerRadius < lineSmallerRadius && outerRadius == lineBiggerRadius){
                innerRadius = lineSmallerRadius
                animationFlow.postDelayed(this, delayMillis * 5)
            } else if(innerRadius == lineSmallerRadius && outerRadius > lineSmallerRadius){
                outerRadius -= 4f
                animationFlow.postDelayed(this, delayMillis)
            } else{
                outerRadius = innerCircleRectF.height() / 2 + arcStrokeWidth / 2
                animationFlow.sendEmptyMessage(4)
            }

        }
    }

    private var step4Runnable: Runnable = object : Runnable {
        override fun run() {
            invalidate()

            bottomCircle.radius += bottomCircle.stepRadius
            bottomCircle.radius += bottomCircleAcc
            bottomCircleAcc += 2f


            if(bottomCircle.radius > bottomCircle.maxRadius){
                middleCircle.radius += middleCircle.stepRadius
                middleCircle.radius += middleCircleAcc
                middleCircleAcc += 2f
            }

            if(middleCircle.radius > middleCircle.maxRadius){
                topCircle.radius += topCircle.stepRadius
                topCircle.radius += topCircleAcc
                topCircleAcc += 2f
            }

            if(topCircle.radius < width){
                animationFlow.postDelayed(this, delayMillis)
            }else{
                animationFlow.sendEmptyMessage(5)
            }
        }
    }

    open class BaseGraph {
        var paint : Paint
        private var color = Color.WHITE
        private var strokeWidth = 15f
        private var style = Paint.Style.STROKE

        init {
            paint = createPaint(color,strokeWidth,style)
        }

        private fun createPaint(color: Int, stroke: Float, style: Paint.Style): Paint {
            val paint = Paint()
            paint.isAntiAlias = true
            paint.isDither = true
            paint.style = style
            paint.color = color
            paint.strokeWidth = stroke
            return paint
        }
    }

    class Line(var startX: Float, val startY: Float, val endX: Float, val endY: Float, val stepX: Float) : BaseGraph()

    class Arc(val startAngle: Float, val sweepAngle: Float, val stepAngle: Float = 5f, var curAngle: Float = 0f)  : BaseGraph()

    class Circle(val centerX : Float , val centerY : Float ,
                 val maxRadius : Float , var radius : Float ,
                 val stepRadius : Float = 4f , var initRadius: Float = radius)  : BaseGraph()

    fun reset() {

        initLines()

        initArc()

        initOuterCircle()

        innerRadius = outerCircle.maxRadius + outerCircleStrokeWidth/2
        outerRadius = outerCircle.maxRadius + outerCircleStrokeWidth/2

        initLayerCircle()

        circleLinePaint = createPaint(Color.WHITE,circleLineStrokeWidth)

        circleLineList.clear()

        bottomCircleAcc = 2f
        middleCircleAcc = 2f
        topCircleAcc = 2f

        animationFlow.stopAll()

        start = false

        invalidate()
    }
}
