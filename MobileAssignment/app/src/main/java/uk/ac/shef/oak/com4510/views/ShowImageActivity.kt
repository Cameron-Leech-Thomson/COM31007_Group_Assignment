package uk.ac.shef.oak.com4510.views

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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
        val longitude = selectedImage.longitude
        val latitude = selectedImage.latitude
        val date = selectedImage.time
        val temp = selectedImage.temperature
        val press = selectedImage.pressure
        val humid = selectedImage.humidity

        imageView.setImageURI(Uri.parse(uri))
    }

}
