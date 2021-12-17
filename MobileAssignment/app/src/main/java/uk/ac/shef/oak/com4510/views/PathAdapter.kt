package uk.ac.shef.oak.com4510.views.Path

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.model.Path
import uk.ac.shef.oak.com4510.viewmodels.ImageRepository
import uk.ac.shef.oak.com4510.views.PathDetailActivity


class PathAdapter() : RecyclerView.Adapter<PathAdapter.ViewHolder>() {

    private lateinit var context: Context

    constructor(items: List<Path>) : this() {
        PathAdapter.items = items as MutableList<Path>
    }

    constructor(cont: Context, items: List<Path>) : this() {
        PathAdapter.items = items as MutableList<Path>
        context = cont
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate the layout, initialize the View Holder
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.item_path,
            parent, false
        )
        val holder: PathAdapter.ViewHolder = ViewHolder(v)
        context = parent.context
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView
        holder.title.text = items[position].title

        holder.itemView.setOnClickListener{
            val intent = Intent(context, PathDetailActivity::class.java)
            intent.putExtra("path id", items[position].path_id)
            startActivity(context, intent, null)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById<View>(R.id.text1) as TextView
    }

    companion object {
        lateinit var items: MutableList<Path>

    }
}