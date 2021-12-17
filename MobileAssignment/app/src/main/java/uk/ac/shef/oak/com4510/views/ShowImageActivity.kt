package uk.ac.shef.oak.com4510.views

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.ac.shef.oak.com4510.MainActivity
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.model.Path
import uk.ac.shef.oak.com4510.viewmodels.ImageRepository
import uk.ac.shef.oak.com4510.viewmodels.PathRepository
import java.io.Serializable
import kotlin.properties.Delegates

class ShowImageActivity : Activity(), Serializable {

    private var imageID by Delegates.notNull<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageID = intent.getSerializableExtra("image id") as Int

        setContentView(R.layout.activity_image)

        val imageRepo = ImageRepository(this.application)
        val pathRepository = PathRepository(this.application)

        val imageView: ImageView = findViewById(R.id.image_full)

        lateinit var selectedImage: Image
        lateinit var imagePath: Path

        runBlocking {
            val searchImage = GlobalScope.launch(Dispatchers.IO) {
                selectedImage = imageRepo.findImageByImageId(imageID)!!.first()
            }
            searchImage.invokeOnCompletion {
                imagePath = pathRepository.getPathByID(selectedImage.path_id)!!.first()
            }
            searchImage.join()
        }

        // Get image data:
        val uri = selectedImage.imageUri
        val path = "Journey: " + imagePath.title
        val longitude = "Longitude: " + selectedImage.longitude.toString()
        val latitude = "Latitude: " + selectedImage.latitude.toString()
        val date = selectedImage.time.toString()
        val temp = "Ambient Temperature: " + selectedImage.temperature.toString()
        val press = "Atmospheric Pressure: " + selectedImage.pressure.toString()
        val humid = "Relative Humidity: " + selectedImage.humidity.toString()

        // Set Image Preview:
        imageView.setImageURI(Uri.parse(uri))

        // Set image data:
        findViewById<TextView>(R.id.image_detail_pathName).text = path
        findViewById<TextView>(R.id.image_detail_long).text = longitude
        findViewById<TextView>(R.id.image_detail_lat).text = latitude
        findViewById<TextView>(R.id.image_detail_date).text = date
        findViewById<TextView>(R.id.image_detail_temp).text = temp
        findViewById<TextView>(R.id.image_detail_press).text = press
        findViewById<TextView>(R.id.image_detail_humid).text = humid

        val backButton = findViewById<FloatingActionButton>(R.id.fab_back)

        backButton.setOnClickListener{
            val intent = Intent(this@ShowImageActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

}
