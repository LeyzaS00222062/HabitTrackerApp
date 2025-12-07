package com.example.habittracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*



class MainActivity : AppCompatActivity() {

    private lateinit var etHabitName: EditText
    private lateinit var btnAddHabit: Button
    private lateinit var btnViewStats: Button
    private lateinit var rvHabits: RecyclerView
    private lateinit var tvNoHabits: TextView
    private lateinit var tvHabitCount: TextView
    private lateinit var tvProgressText: TextView
    private lateinit var tvProgressNumber: TextView

    private lateinit var habitAdapter: HabitAdapter
    private lateinit var  dbHelper: HabitDatabaseHelper
    private val habitsList = mutableListOf<Habit>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = HabitDatabaseHelper(this)

        etHabitName = findViewById(R.id.etHabitName)
        btnAddHabit = findViewById(R.id.btnAddHabit)
        btnViewStats = findViewById(R.id.btnViewStats)
        btnCalendar = findViewById(R.id.btnCalendar)
        rvHabits = findViewById(R.id.rvHabits)
        tvNoHabits = findViewById(R.id.tvNoHabits)
        tvHabitCount = findViewById(R.id.tvHabitCount)
        tvProgressText = findViewById(R.id.tvProgressText)
        tvProgressNumber = findViewById(R.id.tvProgressNumber)

        habitAdapter = HabitAdapter(habitsList){ habit ->
            showHabitHistory(habit)
        }
        rvHabits.layoutManager = LinearLayoutManager(this)
        rvHabits.adapter = habitAdapter

        loadTodayHabits()

        btnAddHabit.setOnClickListener {
            addHabit()
        }

        btnViewStats.setOnClickListener {
            showStatistics()
        }

        btnCalendar.setOnClickListener{
            showCalendarView()
        }
    }

    private fun addHabit(){
        val habitName = etHabitName.text.toString().trim()

        if (habitName.isEmpty()){
            Toast.makeText(this, "Please Enter your Habit: ", Toast.LENGTH_SHORT).show()
            return

        }

        val today = getCurrentDate()
        val habit = Habit(habitName = habitName, date = today)

        val id = dbHelper.insertHabit(habit)
        if (id > 0){
            habit.id = id.toInt()
            habitsList.add(0, habit)
            habitAdapter.notifyItemInserted(0)
            rvHabits.scrollToPosition(0)


            etHabitName.text.clear()
            updateEmptyView()

            Toast.makeText(this, "A Habit has been added!", Toast.LENGTH_SHORT).show()
        } else{
            Toast.makeText(this, "Failed to add your habit!", Toast.LENGTH_SHORT).show()
        }

    }

    private fun showCalendarView(){
        val allHabits = dbHelper.getAllHabits()
        val habitsByDate = allHabits.groupBy { it.date }

        val message = buildString {
            append("Your Habit Calendar:\n\n")
            if (habitsByDate.isEmpty()){
                append("No Habits added yet!")
            }else{
                habitsByDate.entries.sortedByDescending { it.key }.take(10).forEach { (date, habits) ->
                    append("$date\n")
                    habits.forEach { habit ->
                        append(". ${habit.habitName}\n")
                    }
                    append("\n")
                }
            }
        }
        AlertDialog.Builder(this)
            .setTitle("Calendar")
            .setMessage(message)
            .setPositiveButton("Ok", null)
            .show()

    }


    private fun loadTodayHabits(){
        val today = getCurrentDate()
        habitsList.clear()
        habitsList.addAll(dbHelper.getHabitsByDate(today))
        //habitAdapter.notifyDataSetChanged()
        updateEmptyView()
    }

    private fun updateEmptyView(){
        if (habitsList.isEmpty()){
            tvNoHabits.visibility = TextView.VISIBLE
            rvHabits.visibility = RecyclerView.GONE
        } else {
            tvNoHabits.visibility = TextView.GONE
            rvHabits.visibility = RecyclerView.VISIBLE
        }
        updateProgressCard()
    }

    private fun updateProgressCard(){
        val count = habitsList.size

        tvHabitCount.text = "$count Habits"
        tvProgressNumber.text = count.toString()
        tvProgressText.text = if (count == 1) "$count habit completed today" else "$count habits completed today"

    }

    private fun showHabitHistory(habit: Habit){
        val history = dbHelper.getHabitHistory(habit.habitName)

        val message = if (history.isEmpty()){
            "There is no History for this habit yet."
        } else{
            buildString {
                append("History for '${habit.habitName}':\n\n")
                history.forEach{ h ->
                    append(". ${h.date}\n")
                }
                append("\nTotal entries: ${history.size}")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Habit History")
            .setMessage(message)
            .setPositiveButton("Ok", null)
            .show()

    }

    private fun showStatistics(){
        val stats = dbHelper.getHabitStatistics()

        if (stats.isEmpty()){
            AlertDialog.Builder(this)
                .setTitle("The Statistics")
                .setMessage("No Habits have been recorded yet. Start recording now!")
                .setPositiveButton("Ok",null)
                .show()
            return
        }

        val sortedStats = stats.entries.sortedByDescending { it.value }
        val mostFrequent = sortedStats.first()
        val leastFrequent = sortedStats.last()

        val message = buildString {
            append("Your Habit Statistics\n\n")
            append("Most recorded habit:\n")
            append("${mostFrequent.key} - ${mostFrequent.value} times\n\n")

            append("Least recorded habit:\n")
            append("${leastFrequent.key} - ${leastFrequent.value} times\n\n")

            append("All Habits:\n")
            sortedStats.forEach{ (habit,count) ->
                append(". $habit: $count times\n")
            }

            append("\nTotal habits tracked: ${stats.values.sum()}")
        }

        AlertDialog.Builder(this)
            .setTitle("Statistics")
            .setMessage(message)
            .setPositiveButton("Ok",null)
            .show()

    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}

