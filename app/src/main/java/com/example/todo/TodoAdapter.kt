package com.example.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.todo_list_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(val list: List<TodoModel>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){


    class TodoViewHolder(val containerView : View) : RecyclerView.ViewHolder(containerView){

        fun bind(todoModel: TodoModel) {

            with(containerView){
                val colors = resources.getIntArray(R.array.random_color)
                val randomColor = colors[Random().nextInt(colors.size)]
                viewColorTag.setBackgroundColor(randomColor)

                txtShowTitle.text = todoModel.title
                txtShowTask.text = todoModel.description
                txtShowCategory.text = todoModel.category

//                to update the date and time of the adapterview, we again implement the sdf format functions
                updateDate(todoModel.date)
                updateTime(todoModel.time)


            }
        }
        fun emptyBind() {
            with(containerView){
                txtShowTitle.text = "No Tasks Added Yet"
                txtShowTask.text = "Click on + button below to add todos"
            }

        }
        private fun updateDate(date: Long) {
            val myFormat = "EEE, d MMM yyyy"
            val sdf = SimpleDateFormat(myFormat)
            containerView.txtShowDate.text = sdf.format(Date(date))

        }
        private fun updateTime(time: Long) {
            val myFormat = "h:mm a"
            val sdf = SimpleDateFormat(myFormat)
            containerView.txtShowTime.text = sdf.format(Date(time))

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_list_item , parent , false)
        return TodoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        if(list.isNullOrEmpty()){
            holder.emptyBind()
        }else{
            holder.bind(list[position])
        }
    }


}

