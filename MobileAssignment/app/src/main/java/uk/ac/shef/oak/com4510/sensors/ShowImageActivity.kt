package uk.ac.shef.oak.com4510.sensors

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import uk.ac.shef.oak.com4510.R

class ShowImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_message2)
        val b: Bundle? = intent.extras
        var position = -1

        if (b != null) {
            // this is the image position in the itemList
            position = b.getInt("position")
            if (position != -1) {
                val imageView = findViewById<ImageView>(R.id.image)
                val element = MyAdapter.items[position]
                if (element.image != -1) {
                    imageView.setImageResource(element.image)
                } else if (element.file != null) {
                    val myBitmap = BitmapFactory.decodeFile(element.file!!.file.absolutePath)
                    imageView.setImageBitmap(myBitmap)
                }
            }
        }
    }
}
