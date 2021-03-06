package com.koleychik.feature_loading_impl.framework

import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.constraintlayout.motion.widget.TransitionAdapter
import androidx.core.view.isVisible
import com.koleychik.feature_loading_api.LoadingApi
import com.koleychik.feature_loading_impl.R
import javax.inject.Inject

internal class LoadingImpl @Inject constructor() : LoadingApi {

    private lateinit var rootView: View

    private val motionLayout by lazy {
        rootView.findViewById<MotionLayout>(R.id.loadingMotionLayout)
    }

    override fun toStart() {
        motionLayout.transitionToState(R.id.jumpToStart)
    }

    override fun toEnd() {
        motionLayout.transitionToState(R.id.jumpToEnd)
    }

    override fun setRootView(view: View) {
        rootView = view
    }

    override fun startAnimation() {
        with(motionLayout) {
            transitionToState(R.id.end)
            setTransitionListener(object : TransitionAdapter() {
                override fun onTransitionCompleted(
                    motionLayout: MotionLayout?,
                    currentId: Int
                ) {
                    motionLayout?.clearAnimation()
                    motionLayout?.transitionToState(R.id.end)
                }
            })
        }
    }

    override fun endAnimation() {
        motionLayout.clearAnimation()
    }

    override fun setVisible(isVisible: Boolean) {
        motionLayout.isVisible = isVisible
    }

    override fun getLayoutRes(): Int = R.layout.layout_loading
}