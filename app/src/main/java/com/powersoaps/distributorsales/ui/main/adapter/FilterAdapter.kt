package com.powersoaps.distributorsales.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.powersoaps.distributorsales.R
import com.powersoaps.distributorsales.data.model.FilterData
import com.powersoaps.distributorsales.databinding.BeatAdapterBinding
import com.powersoaps.distributorsales.databinding.FilterAdapterBinding
import com.powersoaps.distributorsales.ui.main.activity.shopdetail.TakeOrderActivity
import com.powersoaps.distributorsales.ui.utils.setOnDebounceListener

class FilterAdapter  (var dataList: ArrayList<FilterData>, var onClicked: (FilterData,Boolean,Int) -> Unit) :
    RecyclerView.Adapter<FilterAdapter.MyViewHolder>() {

    //this method is returning the view for each item in the list
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val beatAdapter = FilterAdapterBinding.inflate(LayoutInflater.from(parent.context), parent , false)
        return MyViewHolder(beatAdapter)
    }

    //this method is giving the size of the list
    override fun getItemCount(): Int {
        return dataList.size
    }

    //this method is binding the data on the list
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindItems(dataList[position],position)
    }

    //the class is holding the list view
    inner class MyViewHolder(private val filterAdapter: FilterAdapterBinding) : RecyclerView.ViewHolder(filterAdapter.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bindItems(task: FilterData, pos: Int) {
            val context = filterAdapter.root.context
            filterAdapter.filterTextView.text = task.filterName
            if (task.selected)
            {
                filterAdapter.filterTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                filterAdapter.filterTextView.background = ContextCompat.getDrawable(context, R.drawable.selectedfilter)
            }
            else
            {
                filterAdapter.filterTextView.setTextColor(ContextCompat.getColor(context, R.color.togglebutton))
                filterAdapter.filterTextView.background = ContextCompat.getDrawable(context, R.drawable.unselectfilter)
            }
            filterAdapter.filterTextView.setOnDebounceListener {
                val boolean:Boolean
                if (task.selected)
                {
                    boolean=false
                    dataList[pos].selected=false
//                    TakeOrderActivity.categoryList.set(pos, FilterData(task.filterName,false))
                    filterAdapter.filterTextView.setTextColor(ContextCompat.getColor(context, R.color.togglebutton))
                    filterAdapter.filterTextView.background = ContextCompat.getDrawable(context, R.drawable.unselectfilter)
                }
                else
                {
                    boolean=true
                    dataList[pos].selected=true
//                    TakeOrderActivity.categoryList.set(pos,FilterData(task.filterName,true))
                    filterAdapter.filterTextView.setTextColor(ContextCompat.getColor(context, R.color.white))
                    filterAdapter.filterTextView.background = ContextCompat.getDrawable(context, R.drawable.selectedfilter)
                }
                onClicked(task,boolean,pos)
            }
        }

    }

}