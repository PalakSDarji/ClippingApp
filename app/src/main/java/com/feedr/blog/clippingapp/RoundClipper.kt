package com.feedr.blog.clippingapp

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.RectF
import android.util.TypedValue


class RoundClipper : View {

    private var animating: Boolean = false
    private var isOpen: Boolean = false
    private lateinit var bgRect: Rect
    private lateinit var bgRectF: RectF
    private lateinit var clipRectF: RectF
    private lateinit var bgPaint: Paint
    private lateinit var clipPath: Path

    private var cornerRadiusX : Float = 50F
    private var cornerRadiusY : Float = 50F

    private var clipRight : Float = 160f
    private var clipBottom : Float = 160F

    private lateinit var drawable: Drawable

    private lateinit var animatorSet : AnimatorSet

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
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createBg()
    }


    private fun createBg() {

        bgRectF = RectF(100f, 100f, 400F, 400f)
        bgRect = Rect(100, 100, 400, 400)

        clipRectF = RectF(100f, 100f, clipRight, clipBottom)

        bgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bgPaint!!.color = Color.BLUE

        animatorSet = AnimatorSet()

       // val bitmap = BitmapFactory.decodeResource(resources, R.drawable.butterfly)
        drawable = resources.getDrawable(R.drawable.butterfly, null);
        drawable.setBounds(bgRect);

        clipPath = Path()
    }

    override fun onDraw(canvas: Canvas) {

        clipPath.rewind()
        clipRectF.right = clipRight
        clipRectF.bottom = clipBottom
        println("ClipppRectF : ${clipRectF.right} ${clipRectF.bottom} ")
        clipPath.addRoundRect(clipRectF, cornerRadiusX, cornerRadiusY, Path.Direction.CCW)

        canvas.clipPath(clipPath)
        //canvas.clipRect(clipRectF)

        //canvas.drawRoundRect(bgRectF,cornerRadiusX,cornerRadiusY,bgPaint)

        drawable.draw(canvas)

        //super.onDraw(canvas)
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

        val objectAnimatorX = ObjectAnimator.ofFloat(this, "clipRight", 160f,400f)
        objectAnimatorX.duration = 500
        objectAnimatorX.interpolator = AccelerateDecelerateInterpolator()

        val objectAnimatorY = ObjectAnimator.ofFloat(this, "clipBottom", 160f,400f)
        objectAnimatorY.duration = 500
        objectAnimatorY.interpolator = AccelerateDecelerateInterpolator()

        val objectAnimatorCornerRadiusX = ObjectAnimator.ofFloat(this, "cornerRadiusX", 50f,5f)
        objectAnimatorCornerRadiusX.duration = 500
        objectAnimatorCornerRadiusX.interpolator = AccelerateDecelerateInterpolator()

        val objectAnimatorCornerRadiusY = ObjectAnimator.ofFloat(this, "cornerRadiusY", 50f,5f)
        objectAnimatorCornerRadiusY.duration = 500
        objectAnimatorCornerRadiusY.interpolator = AccelerateDecelerateInterpolator()

        animatorSet!!.playTogether(objectAnimatorX,objectAnimatorY,objectAnimatorCornerRadiusX,objectAnimatorCornerRadiusY)
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

        animatorSet = AnimatorSet()
        val objectAnimatorX = ObjectAnimator.ofFloat(this, "clipRight", 400f,160f)
        objectAnimatorX.duration = 500
        objectAnimatorX.interpolator = AccelerateDecelerateInterpolator()

        val objectAnimatorY = ObjectAnimator.ofFloat(this, "clipBottom", 400f,160f)
        objectAnimatorY.duration = 500
        objectAnimatorY.interpolator = AccelerateDecelerateInterpolator()

        val objectAnimatorCornerRadiusX = ObjectAnimator.ofFloat(this, "cornerRadiusX", 5f,50f)
        objectAnimatorCornerRadiusX.duration = 500
        objectAnimatorCornerRadiusX.interpolator = AccelerateDecelerateInterpolator()

        val objectAnimatorCornerRadiusY = ObjectAnimator.ofFloat(this, "cornerRadiusY", 5f,50f)
        objectAnimatorCornerRadiusY.duration = 500
        objectAnimatorCornerRadiusY.interpolator = AccelerateDecelerateInterpolator()

        animatorSet!!.playTogether(objectAnimatorX,objectAnimatorY,objectAnimatorCornerRadiusX,objectAnimatorCornerRadiusY)
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

    fun setClipRight(clipRight: Float) {
        this.clipRight = clipRight
        println("clipRight  $clipRight")
    }

    fun setCornerRadiusX(cornerRadiusX: Float) {
        this.cornerRadiusX = cornerRadiusX
        println("cornerRadiusX  $cornerRadiusX")
    }

    fun setCornerRadiusY(cornerRadiusX: Float) {
        this.cornerRadiusY = cornerRadiusX
        println("cornerRadiusY  $cornerRadiusY")
    }

    fun setClipBottom(clipBottom: Float) {
        this.clipBottom = clipBottom
        println("clipBottom  $clipBottom")
        invalidate()
    }

    fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), this.context.resources.displayMetrics).toInt()
    }

}