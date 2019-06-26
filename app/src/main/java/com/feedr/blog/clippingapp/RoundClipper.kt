package com.feedr.blog.clippingapp

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.graphics.RectF
import android.util.TypedValue


class RoundClipper : View {

    private var animating: Boolean = false
    private var isOpen: Boolean = false
    private lateinit var bgRect: Rect
    private lateinit var clipRectF: RectF
    private lateinit var bgPaint: Paint
    private lateinit var clipPath: Path

    private var cornerRadius : Float = 0f

    private var clipLeft : Float = 0F
    private var clipTop : Float = 0f
    private var clipRight : Float = 0f
    private var clipBottom : Float = 0f

    private var startClipLeft : Float = 0F
    private var startClipTop : Float = 0f
    private var startClipRight : Float = 0f

    private var circleSize : Int = 0

    private lateinit var drawable: Drawable

    private lateinit var animatorSet : AnimatorSet

    private var objectAnimatorTop = ObjectAnimator()

    private var objectAnimatorLeft = ObjectAnimator()

    private var objectAnimatorRight = ObjectAnimator()

    private var objectAnimatorCornerRadius = ObjectAnimator()

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {

        //here is where we will fetch attributes passed in xml
        if (attrs != null) {
            /*val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundClipper)
            borderPadding = typedArray.getDimensionPixelSize(R.styleable.RoundClipper_switch_inner_padding, 5)*/
        }
    }
/*
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //These are just best looking reference dimens to set the height of the view according to given width.
        //you can also consider this as aspect ratio.
        val desiredWidth = dpToPx(60)
        val desiredHeight = dpToPx(60)

        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)

        val width: Int
        val height: Int

        //Measure Width
        if (widthMode == View.MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize)
        } else {
            //Be whatever you want
            width = desiredWidth
        }

        //Now, time to calculate height according to calculated width in reference to desired width and height!
        height = width * desiredHeight / desiredWidth

        setMeasuredDimension(width, width)
    }*/


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createBg()
    }


    private fun createBg() {

        circleSize = width/3

        bgRect = Rect(0, 0, width, height)

        clipTop = circleSize.toFloat()
        clipLeft = circleSize.toFloat()
        clipRight = circleSize.toFloat()

        clipRectF = RectF(getClipLeft(), getClipTop(), getClipRight(), getClipBottom())

        startClipTop = clipRectF.top
        startClipLeft = clipRectF.left
        startClipRight = clipRectF.right

        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint!!.color = Color.BLUE

        drawable = resources.getDrawable(R.drawable.butterfly, null);
        drawable.bounds = bgRect;

        animatorSet = AnimatorSet()
        clipPath = Path()

        cornerRadius = clipRectF.width()/2

        initAnimators()
    }

    private fun initAnimators() {

        objectAnimatorTop.target = this
        objectAnimatorTop.propertyName = "clipTop"
        objectAnimatorTop.duration = 500
        objectAnimatorTop.interpolator = AccelerateDecelerateInterpolator()

        objectAnimatorLeft.target = this
        objectAnimatorLeft.propertyName = "clipLeft"
        objectAnimatorLeft.duration = 500
        objectAnimatorLeft.interpolator = AccelerateDecelerateInterpolator()

        objectAnimatorRight.target = this
        objectAnimatorRight.propertyName = "clipRight"
        objectAnimatorRight.duration = 500
        objectAnimatorRight.interpolator = AccelerateDecelerateInterpolator()

        objectAnimatorCornerRadius.target = this
        objectAnimatorCornerRadius.propertyName = "cornerRadius"
        objectAnimatorCornerRadius.duration = 500
        objectAnimatorCornerRadius.interpolator = AccelerateDecelerateInterpolator()
    }

    private fun getClipLeft() : Float{
        return (width/2) - (clipLeft/2)
    }

    private fun getClipTop() : Float{
        return (height - clipTop)
    }

    private fun getClipRight() : Float{
        return (height - clipRight)
    }

    private fun getClipBottom() : Float{
        return height.toFloat()
    }

    override fun onDraw(canvas: Canvas) {

        clipPath.rewind()

        if(animating){
            clipRectF.top = clipTop
            clipRectF.left = clipLeft
            clipRectF.right = clipRight
        }

        println("ClipppRectF : ${clipRectF.left} ${clipRectF.top} ${clipRectF.right} ${clipRectF.bottom} ")

        clipPath.addRoundRect(clipRectF, cornerRadius, cornerRadius, Path.Direction.CCW)

        canvas.clipPath(clipPath)
        //canvas.clipRect(clipRectF)

        //canvas.drawRoundRect(bgRectF,cornerRadiusX,cornerRadiusY,bgPaint)

        drawable.draw(canvas)
        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!animating) {
                    if (!isOpen) {
                        animateToOpen()
                    } else {
                        animateToClose()
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    private fun animateToOpen(){

        objectAnimatorTop.setFloatValues(startClipTop,0f)
        objectAnimatorLeft.setFloatValues(startClipLeft,0f)
        objectAnimatorRight.setFloatValues(startClipRight,width.toFloat())
        objectAnimatorCornerRadius.setFloatValues((circleSize/2).toFloat(),5f)

        animatorSet!!.playTogether(objectAnimatorTop,objectAnimatorLeft,objectAnimatorRight,objectAnimatorCornerRadius)

        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                animating = true
            }

            override fun onAnimationEnd(animator: Animator) {
                isOpen = true
                animating = false
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })

        animatorSet!!.start()

    }

    private fun animateToClose(){

        animatorSet.cancel()

        objectAnimatorTop.setFloatValues(0f,startClipTop)
        objectAnimatorLeft.setFloatValues(0f,startClipLeft)
        objectAnimatorRight.setFloatValues(width.toFloat(),startClipRight)
        objectAnimatorCornerRadius.setFloatValues(5f,(circleSize/2).toFloat())

        animatorSet!!.playTogether(objectAnimatorTop,objectAnimatorLeft,objectAnimatorRight,objectAnimatorCornerRadius)

        animatorSet!!.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator) {
                animating = true
            }

            override fun onAnimationEnd(animator: Animator) {
                isOpen = false
                animating = false
            }

            override fun onAnimationCancel(animator: Animator) {}

            override fun onAnimationRepeat(animator: Animator) {}
        })

        animatorSet!!.start()

    }

    fun setClipTop(clipTop: Float) {
        this.clipTop = clipTop
        println("clipTop  $clipTop")
    }

    fun setClipLeft(clipLeft: Float) {
        this.clipLeft = clipLeft
        println("clipLeft  $clipLeft")
    }

    fun setClipRight(clipRight: Float) {
        this.clipRight = clipRight
        println("clipRight  $clipRight")
    }

    fun setCornerRadius(cornerRadius: Float) {
        this.cornerRadius = cornerRadius
        println("cornerRadius  $cornerRadius")
        invalidate()
    }

    fun setClipBottom(clipBottom: Float) {
        this.clipBottom = clipBottom
        println("clipBottom  $clipBottom")
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), this.context.resources.displayMetrics).toInt()
    }

}