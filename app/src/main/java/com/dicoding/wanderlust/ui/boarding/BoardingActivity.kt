package com.dicoding.wanderlust.ui.boarding

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.wanderlust.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class BoardingActivity : AppCompatActivity() {

    private lateinit var onboardingViewPager: ViewPager2
    private lateinit var indicatorsContainer: LinearLayout
    private lateinit var textSkip: MaterialTextView
    private lateinit var buttonGetStarted: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_boarding)

        onboardingViewPager = findViewById(R.id.onboardingViewPager)
        indicatorsContainer = findViewById(R.id.indicatorsContainer)
        textSkip = findViewById(R.id.textSkip)
        buttonGetStarted = findViewById(R.id.buttonGetStarted)

        val onboardingItems = listOf(
            BoardingItem(R.drawable.beach, "Welcome", "This is the first slide description."),
            BoardingItem(R.drawable.city, "Discover", "This is the second slide description."),
            BoardingItem(R.drawable.flowers, "Get Started", "This is the third slide description.")
        )

        val adapter = BoardingAdapter(onboardingItems)
        onboardingViewPager.adapter = adapter

        setupIndicators(onboardingItems.size)
        setCurrentIndicator(0)

        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        textSkip.setOnClickListener {
            finish()
        }

        buttonGetStarted.setOnClickListener {
            finish()
        }
    }

    private fun setupIndicators(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.indicator_inactive
                )
            )
            indicators[i]?.layoutParams = layoutParams
            indicatorsContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicatorsContainer.childCount
        for (i in 0 until childCount) {
            val imageView = indicatorsContainer.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_active)
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(applicationContext, R.drawable.indicator_inactive)
                )
            }
        }
    }
}