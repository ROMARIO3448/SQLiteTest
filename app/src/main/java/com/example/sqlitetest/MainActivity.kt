package com.example.sqlitetest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlitetest.databinding.ActivityMainBinding
import com.example.sqlitetest.db.MyDbManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    val myDbManager = MyDbManager(this)
    private val myAdapter = MyAdapter(ArrayList(),this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initSearchView()
    }

    override fun onDestroy() {
        super.onDestroy()
        myDbManager.closeDb()
    }

    //onResume() запускается каждый раз, когда перезапускается наш
    // Activity. К примеру, после возвращения с другого Activity
    override fun onResume() {
        super.onResume()
        myDbManager.openDb()
        fillAdapter()
    }

    fun onClickNew(view: View) {
        val i = Intent(this, EditActivity::class.java)
        startActivity(i)
    }

    //private fun init() = with(binding), тогда можно не писать binding.apply {}
    private fun init() = with(binding) {
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        val swapHelper = getSwapMg()
        swapHelper.attachToRecyclerView(recyclerView)
        recyclerView.adapter = myAdapter
    }

    fun initSearchView(){
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                val list = myDbManager.readDbArrayList(p0!!)
                myAdapter.updateAdapter(list)
                return true
            }
        })
    }

    private fun fillAdapter()
    {
        val list = myDbManager.readDbArrayList("")
        myAdapter.updateAdapter(list)
        if(list.size>0){
            binding.textNoElements.visibility = View.GONE
        }
        else
        {
            binding.textNoElements.visibility = View.VISIBLE
        }
    }
    private fun getSwapMg(): ItemTouchHelper{
        return ItemTouchHelper(object:ItemTouchHelper.
        SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                myAdapter.removeItem(viewHolder.adapterPosition, myDbManager)
            }

        })
    }

}