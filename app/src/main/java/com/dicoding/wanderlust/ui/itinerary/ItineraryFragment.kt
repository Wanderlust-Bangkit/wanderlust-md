package com.dicoding.wanderlust.ui.itinerary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dicoding.wanderlust.R

class ItineraryFragment : Fragment() {

    private lateinit var itineraryViewModel: ItineraryViewModel
    private lateinit var etName: EditText
    private lateinit var lvDestinations: ListView
    private lateinit var btnStartDate: Button
    private lateinit var btnEndDate: Button
    private lateinit var calendarView: CalendarView
    private lateinit var formScrollView: ScrollView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_itinerary, container, false)

        itineraryViewModel = ViewModelProvider(this).get(ItineraryViewModel::class.java)

//        etName = rootView.findViewById(R.id.etName)
//        lvDestinations = rootView.findViewById(R.id.lvDestinations)
//        btnStartDate = rootView.findViewById(R.id.btnStartDate)
//        btnEndDate = rootView.findViewById(R.id.btnEndDate)
//        calendarView = rootView.findViewById(R.id.calendarView)
//        formScrollView = rootView.findViewById(R.id.formScrollView)
//
//        rootView.findViewById<Button>(R.id.btnToggleDestinations).setOnClickListener {
//            toggleListViewVisibility()
//        }

        lvDestinations.setOnItemClickListener { _, _, position, _ ->
            // Handle destination selection
            lvDestinations.visibility = View.GONE
        }

        btnStartDate.setOnClickListener {
            calendarView.visibility = View.VISIBLE
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                btnStartDate.text = "$dayOfMonth/${month + 1}/$year"
                calendarView.visibility = View.GONE
            }
        }

        btnEndDate.setOnClickListener {
            calendarView.visibility = View.VISIBLE
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                btnEndDate.text = "$dayOfMonth/${month + 1}/$year"
                calendarView.visibility = View.GONE
            }
        }

        return rootView
    }

    private fun toggleListViewVisibility() {
        lvDestinations.visibility = if (lvDestinations.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    fun onThemeSelected(view: View) {
        formScrollView.visibility = View.VISIBLE
    }
}
