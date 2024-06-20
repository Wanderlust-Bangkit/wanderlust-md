package com.dicoding.wanderlust.ui.boarding
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.dicoding.wanderlust.R
import com.dicoding.wanderlust.ui.main.MainActivity
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
            BoardingItem(R.drawable.beach, "Temukan petualangan baru", "Lepaskan keinginan berpetualang Anda! Temukan itinerary yang dipersonalisasi hanya untuk Anda, dibuat oleh AI sesuai dengan preferensi dan minat perjalanan Anda."),
            BoardingItem(R.drawable.city, "Rencanakan dengan mudah", "Ucapkan selamat tinggal pada kekacauan perencanaan perjalanan, dari atraksi yang wajib dikunjungi hingga tempat tersembunyi, semua dalam genggaman Anda"),
            BoardingItem(R.drawable.forest, "Perjalanan Anda, cara Anda", "Jelajahi seperti penduduk lokal, dan ciptakan kenangan tak terlupakan dengan itinerary yang dirancang khusus untuk Anda.")
        )

        val adapter = BoardingAdapter(onboardingItems)
        onboardingViewPager.adapter = adapter

        setupIndicators(onboardingItems.size)
        setCurrentIndicator(0)

        onboardingViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)

                // Show or hide button based on page position
                if (position == 2) {
                    buttonGetStarted.visibility = View.VISIBLE
                    indicatorsContainer.visibility = View.INVISIBLE
                } else {
                    buttonGetStarted.visibility = View.INVISIBLE
                    indicatorsContainer.visibility = View.VISIBLE
                }
            }
        })

        textSkip.setOnClickListener {
            navigateToMain()
        }

        buttonGetStarted.setOnClickListener {
            navigateToMain()
        }
    }

    private fun setupIndicators(count: Int) {
        val indicators = arrayOfNulls<ImageView>(count)
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(8, 42, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(this)
            indicators[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.indicator_inactive // Use round drawable here
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
                    ContextCompat.getDrawable(this, R.drawable.indicator_active) // Use round drawable here
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.indicator_inactive) // Use round drawable here
                )
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
