package com.example.todo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

const val DB_NAME = "ToDo.db"

class TaskActivity : AppCompatActivity() {

    lateinit var myCalender : Calendar
    lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    lateinit var timeSetListener: TimePickerDialog.OnTimeSetListener
    private val labels = arrayListOf("Personal","Business","Insurance","Shopping","Banking")

    val db by lazy {
        AppDatabase.getDatabase(this)
    }


    var finalTime: Long = 0L
    var finalDate: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        setSupportActionBar(toolbarAddTask)

        dateEdt.setOnClickListener {
            /**
             * i have to do 2 things
             * create a calender object to store the values of date(dd/mm/yyy)
             * open up the date dialog to let user enter the date
             */
            setListener()
        }

        timeEdt.setOnClickListener {
            //same as datepickerdialog
            setListener2()
        }
        setUpSpinner()
        saveBtn.setOnClickListener {
            //save the task details into database
            saveTodo()
        }

    }

    private fun saveTodo() {
        val category = spinnerCategory.selectedItem.toString()
        val title = titleInpLay.editText?.text.toString()
        val description = taskInpLay.editText?.text.toString()
        val todoModel = TodoModel(title , description , category , finalDate , finalTime )

        GlobalScope.launch {
            val id = db.todoDao().insertTask(todoModel)
            finish()
        }
//        GlobalScope.launch(Dispatchers.Main) {
//            val id = withContext(Dispatchers.IO) {
//                return@withContext db.todoDao().insertTask(
//                    TodoModel(
//                        title,
//                        description,
//                        category,
//                        finalDate,
//                        finalTime
//                    )
//                )
//            }
//            finish()
//        }



    }

    private fun setUpSpinner() {

        labels.sort()
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,labels)
        spinnerCategory.adapter = adapter
    }

    private fun setListener2() {

        myCalender = Calendar.getInstance()

        timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            myCalender.set(Calendar.HOUR_OF_DAY , hourOfDay)
            myCalender.set(Calendar.MINUTE , minute)
            updateTime()

        }

        val timePickerDialog = TimePickerDialog(
            this,
            timeSetListener,
            myCalender.get(Calendar.HOUR_OF_DAY),
            myCalender.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()

    }

    private fun updateTime() {
        val myFormat = "h:mm a"
        val sdf = SimpleDateFormat(myFormat)
        timeEdt.setText(sdf.format(myCalender.time))
        finalTime = myCalender.time.time

val date = Date()
    }


    private fun setListener() {

        myCalender = Calendar.getInstance()

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            myCalender.set(Calendar.YEAR , year)
            myCalender.set(Calendar.MONTH , month)
            myCalender.set(Calendar.DAY_OF_MONTH , dayOfMonth)

            //now update the dates in your editText
            updateDate()
        }

        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            myCalender.get(Calendar.YEAR),
            myCalender.get(Calendar.MONTH),
            myCalender.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis()
        datePickerDialog.show()
    }

    private fun updateDate() {
        //Mon, 5 Jan 2020
        val myFormat = "EEE, d MMM yyyy"
        val sdf = SimpleDateFormat(myFormat)
        dateEdt.setText(sdf.format(myCalender.time))

        timeInpLay.visibility = View.VISIBLE
        finalDate = myCalender.time.time

    }


}