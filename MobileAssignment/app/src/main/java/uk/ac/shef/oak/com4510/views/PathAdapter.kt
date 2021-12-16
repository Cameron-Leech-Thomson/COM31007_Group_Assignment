package uk.ac.shef.oak.com4510.views.Path

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Path
import uk.ac.shef.oak.com4510.views.GalleryAdapter


class PathAdapter : RecyclerView.Adapter<PathAdapter.ViewHolder>() {

    private lateinit var context: Context

    constructor(items: List<Path>) : super() {
        PathAdapter.items = items as MutableList<Path>
    }

    constructor(cont: Context, items: List<Path>) : super() {
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
        if (items[position] != null) {
            holder.title.text = items[position].title
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById<View>(R.id.title) as TextView
    }

    companion object {
        lateinit var items: MutableList<Path>
    }
}