package uk.ac.shef.oak.com4510.sensors

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import uk.ac.shef.oak.com4510.R

class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private lateinit var context: Context

    constructor(items: List<ImageElement>): super() {
        MyAdapter.items = items
    }

    constructor(cont: Context, items: List<ImageElement>) : super() {
        MyAdapter.items = items
        context = cont
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //Inflate the layout, initialize the View Holder
        val v: View = LayoutInflater.from(parent.context).inflate(
            R.layout.list_item_image,
            parent, false
        )
        val holder: ViewHolder = ViewHolder(v)
        context = parent.context
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Use the provided View Holder on the onCreateViewHolder method to populate the
        // current row on the RecyclerView
        if (items[position].image != -1) {
            holder.imageView.setImageResource(items[position].image)
        } else if (items[position].file != null) {
            val myBitmap = BitmapFactory.decodeFile(items[position].file?.file?.absolutePath)
            holder.imageView.setImageBitmap(myBitmap)
        }
        // Todo: exclude for none - click
        holder.itemView.setOnClickListener(View.OnClickListener {
            val intent = Intent(context, ShowImageActivity::class.java)
            intent.putExtra("position", position)
            context.startActivity(intent)
        })
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder constructor(itemView: View): RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById<View>(R.id.image_item) as ImageView
    }

    companion object {
        lateinit var items: List<ImageElement>
    }
}