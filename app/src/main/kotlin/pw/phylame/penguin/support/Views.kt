package pw.phylame.penguin.support

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View


@Suppress("UNCHECKED_CAST")
operator fun <T : View> View.get(@IdRes id: Int): T = findViewById(id) as T

fun <T : View> View.lazyView(@IdRes id: Int): Lazy<T> = lazy {
    @Suppress("UNCHECKED_CAST")
    findViewById(id) as T
}

@Suppress("UNCHECKED_CAST")
operator fun <T : View> Fragment.get(@IdRes id: Int): T = view!!.findViewById(id) as T

fun <T : View> Fragment.lazyView(@IdRes id: Int): Lazy<T> = lazy {
    @Suppress("UNCHECKED_CAST")
    view!!.findViewById(id) as T
}

fun View.showAnimated(shown: Boolean) {
    val animTime = context.resources.getInteger(android.R.integer.config_shortAnimTime)
    visibility = if (shown) View.VISIBLE else View.GONE
    animate()
            .setDuration(animTime.toLong())
            .alpha(if (shown) 1F else 0F)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    visibility = if (shown) View.VISIBLE else View.GONE
                }
            })
}


class DividerItemDecoration(context: Context, orientation: Int) : RecyclerView.ItemDecoration() {
    private val mDivider: Drawable

    var orientation: Int = VERTICAL
        set(value) {
            require(value == HORIZONTAL || value == VERTICAL) { "Invalid orientation" }
            field = value
        }

    var isLastDividerShown = false

    init {
        val attrs = context.obtainStyledAttributes(ATTRS)
        mDivider = attrs.getDrawable(0)
        attrs.recycle()
        this.orientation = orientation
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State?) {
        if (orientation == VERTICAL) {
            drawVertical(c, parent)
        } else {
            drawHorizontal(c, parent)
        }
    }

    fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val end = if (isLastDividerShown) parent.childCount - 1 else parent.childCount - 2
        for (i in 0..end) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + mDivider.intrinsicHeight
            mDivider.setBounds(left + child.paddingLeft, top, right - child.paddingRight, bottom)
            mDivider.draw(c)
        }
    }

    fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop
        val bottom = parent.height - parent.paddingBottom

        val end = if (isLastDividerShown) parent.childCount - 1 else parent.childCount - 2
        for (i in 0..end) {
            val child = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.right + params.rightMargin
            val right = left + mDivider.intrinsicHeight
            mDivider.setBounds(left + child.paddingLeft, top, right - child.paddingRight, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView?, state: RecyclerView.State?) {
        if (orientation == VERTICAL) {
            outRect.set(0, 0, 0, mDivider.intrinsicHeight)
        } else {
            outRect.set(0, 0, mDivider.intrinsicWidth, 0)
        }
    }

    companion object {

        private val ATTRS = intArrayOf(android.R.attr.listDivider)

        val VERTICAL = LinearLayoutManager.VERTICAL

        val HORIZONTAL = LinearLayoutManager.HORIZONTAL
    }
}
