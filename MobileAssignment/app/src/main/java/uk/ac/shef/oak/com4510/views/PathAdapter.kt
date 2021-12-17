package uk.ac.shef.oak.com4510.views.Path

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.model.Path
import uk.ac.shef.oak.com4510.viewmodels.ImageRepository


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
        if (items[position] != null) {
            holder.title.text = items[position].title
            val path_id = items[position].path_id
            val repo = ImageRepository(holder.itemView.context.applicationContext as Application)
            runBlocking {
                GlobalScope.launch(Dispatchers.IO) {
                    val images = repo.findImagesByPathId(path_id)
                    if (!images.isNullOrEmpty()) {
                        val uri = Uri.parse(images.first().imageUri)
                        holder.image.setImageURI(uri)
                    }
                }.join()
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById<View>(R.id.text1) as TextView
        var image: ImageView = itemView.findViewById(R.id.imagePreview1) as ImageView
    }

    companion object {
        lateinit var items: MutableList<Path>

    }
}