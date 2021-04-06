package com.example.todo

import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    val list = arrayListOf<TodoModel>()
    val adapter = TodoAdapter(list)
    val db by lazy {
        AppDatabase.getDatabase(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        floatingActionButton.setOnClickListener {
            startActivity(Intent(this,TaskActivity::class.java))
        }
        //setting up recycler view
        rvTodo.layoutManager = LinearLayoutManager(this)
        rvTodo.adapter = adapter

        //fetching data from database
        db.todoDao().getTask().observe( this , Observer {
            if(it.isNullOrEmpty()){
                list.clear()
                adapter.notifyDataSetChanged()
            }else{
                list.clear()
                list.addAll(it)
                adapter.notifyDataSetChanged()
            }
        })
        initSwipe()
    }

    fun initSwipe(){
        //swipe left to delete task     //swipe right to finish task

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                //since we are not implementing dragging and dropping of a view actions, hence return false
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                if(direction == ItemTouchHelper.LEFT){
                    GlobalScope.launch {
                        db.todoDao().deleteTask(adapter.getItemId(position))
                    }
                }else if (direction == ItemTouchHelper.RIGHT){
                    GlobalScope.launch {
                        db.todoDao().finishTask(adapter.getItemId(position))
                    }
                }
            }

            override fun onChildDraw(                                   // -> function to manually implement the icons on swiping
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                    val itemView = viewHolder.itemView          //returns the view currently selected

                    var paint = Paint()
                    var icon : Bitmap

                    itemView.translationX = dX

                    if(dX > 0){    //-> right swipe
                        paint.color = Color.parseColor("#008000")
                        icon = BitmapFactory.decodeResource(resources , R.mipmap.baseline_done_black_18dp)

                        canvas.drawRect(
                            itemView.left.toFloat() , itemView.top.toFloat(),
                            itemView.left.toFloat() + dX , itemView.bottom.toFloat(),
                            paint
                        )
                        canvas.drawBitmap(
                            icon,
                            itemView.left.toFloat() + dX/2,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            null
                        )

                    }else {                 //-> left swipe

                        paint.color = Color.parseColor("#FF0000")
                        icon = BitmapFactory.decodeResource(resources , R.mipmap.baseline_delete_black_18dp)

                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat() , itemView.bottom.toFloat(),
                            paint
                        )
                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width.toFloat() + dX/2,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            null
                        )
                    }

                }else{
                    super.onChildDraw( canvas, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                }
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(rvTodo)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menumain_history ->{
                startActivity(Intent(this, HistoryActivity::class.java))
            }
            R.id.menumain_search -> {
                searchFun(item)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun searchFun(item: MenuItem) {
        val searchView = item.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                displayTodo(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                displayTodo(newText)
                return true
            }
        })
    }
    private fun displayTodo(query: String? = "") {

        db.todoDao().getTask().observe(this , Observer{
            if(it.isNotEmpty()){
                list.clear()
                list.addAll(
                    it.filter { todo ->
                        todo.title.contains(query.toString(), true)
                    }
                )
                adapter.notifyDataSetChanged()
            }
        })
    }
}