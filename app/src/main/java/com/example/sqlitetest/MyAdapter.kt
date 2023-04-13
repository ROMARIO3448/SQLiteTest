package com.example.sqlitetest

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlitetest.databinding.RcItemBinding
import com.example.sqlitetest.db.ListItem
import com.example.sqlitetest.db.MyDbManager
import com.example.sqlitetest.db.MyIntentConstants

class MyAdapter(listMain: ArrayList<ListItem>, contextMain: Context) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    var listArray = listMain
    val contextAdapter = contextMain

    class MyViewHolder(itemView: View, contextAdapter: Context): RecyclerView.ViewHolder(itemView) {
        val contextHolder = contextAdapter
        val binding = RcItemBinding.bind(itemView)
        fun setData(item: ListItem) = with(binding){
            textTitle.text = item.title
            textTitle2.text = item.time
            itemView.setOnClickListener {
                val intent = Intent(contextHolder, EditActivity::class.java).apply {
                    putExtra(MyIntentConstants.I_TITLE_KEY,item.title)
                    putExtra(MyIntentConstants.I_DESC_KEY,item.desc)
                    putExtra(MyIntentConstants.I_URI_KEY,item.uri)
                    putExtra(MyIntentConstants.I_ID_KEY, item.id)
                }
                contextHolder.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.rc_item, parent, false)
        return MyViewHolder(inflater, contextAdapter)
    }

    override fun getItemCount(): Int {
        return listArray.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.setData(listArray.get(position))
    }

    fun updateAdapter(itemLists: List<ListItem>){
        listArray.clear()
        listArray.addAll(itemLists)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int, dbManager: MyDbManager){
        dbManager.removeItemFromDb(listArray[position].id.toString())
        listArray.removeAt(position)
        notifyItemRangeChanged(0,listArray.size)
        notifyItemRemoved(position)
    }
}