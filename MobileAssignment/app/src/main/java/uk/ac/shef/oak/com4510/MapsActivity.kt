package uk.ac.shef.oak.com4510

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.*
import uk.ac.shef.oak.com4510.databinding.ActivityMapsBinding
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.sensors.ImageElement
import uk.ac.shef.oak.com4510.sensors.SensorsController
import uk.ac.shef.oak.com4510.viewmodels.ImageViewModel
import uk.ac.shef.oak.com4510.views.GalleryFragment
import uk.ac.shef.oak.com4510.views.HomeFragment
import java.io.Serializable
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.properties.Delegates

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, Serializable {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var sensorsController: SensorsController
    private lateinit var easyImage: EasyImage
    private lateinit var image: ImageElement
    private lateinit var previousUri: Uri
    private lateinit var pathTitle: String
    private var pathImages = ArrayList<Image>()
    private var pathID by Delegates.notNull<Int>()
    private var imagesInPath = 0

    private lateinit var imageViewModel: ImageViewModel

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MapsActivity)
        sensorsController = SensorsController(this@MapsActivity, fusedLocationClient)
        sensorsController.requestLocation()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Snackbar.make(binding.root,
            "Loading, please wait.", Snackbar.LENGTH_LONG).show()

        checkPermissions(applicationContext)
        initEasyImage()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MapsActivity)

        binding.fabCamera.setOnClickListener(View.OnClickListener {
            if (sensorsController.getLatLng() != null) {
                sensorsController.requestLocation()
                easyImage.openChooser(this@MapsActivity)
            } else{
                Snackbar.make(binding.root,
                    "Loading, please wait.", Snackbar.LENGTH_SHORT).show()
            }
        })
        binding.fabStop.setOnClickListener{
            val intent = Intent(this@MapsActivity, HomeFragment::class.java)
            startActivity(intent)
        }
        binding.fabSubmit.setOnClickListener(View.OnClickListener {
            if (imagesInPath != 0) {
                for (image in pathImages) {
                    GlobalScope.launch(Dispatchers.IO) {
                        imageViewModel.insertImage(image)
                    }
                }

//                val intent = Intent(this@MapsActivity, GalleryFragment::class.java)
//                startActivity(intent)

            } else {
                Snackbar.make(binding.root,
                    "Please upload some images to the path before submitting.",
                    Snackbar.LENGTH_SHORT).show()
            }
        })

        pathTitle = intent.getSerializableExtra("path title") as String
        pathID = intent.getSerializableExtra("path id") as Int
    }

    /**
     * pinImage()
     * takes the image, as well as all the data relevant to it, and submit it to the database.
     */
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun pinImage(){
        // Get Location:
        val location = sensorsController.getLatLng()!!
        val imageTitle = "$pathTitle: Image #$imagesInPath"
        // Add pin on map:
        mMap.addMarker(MarkerOptions().position(location).title(imageTitle))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location))

        // Get sensor data:
        val sensorData = sensorsController.getSensorData()

        val uri = image.getUri().toString()
        // Create image data class:

        // DEBUG PRINT PATH ID
        Log.d("path_id", pathID.toString())

        //TODO CHANGE PATH ID FROM 1 TO THE PATH ID THAT COMES FROM THE HOME FRAGMENT
        val imageData = Image(0,uri,imageTitle,location.longitude,location.latitude,
            getDate()!!, 1, sensorData[0]!!, sensorData[1]!!, sensorData[2]!!)

        pathImages.add(imageData)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getDate(): Date?{
        val config = applicationContext.resources.configuration
        val locale = config!!.locales[0]

        val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale)

        var date: Date? = null

        try {
            date = dateFormat.parse(dateFormat.format(Calendar.getInstance().time))

        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    private fun initEasyImage() {
        easyImage = EasyImage.Builder(this)
            .setChooserTitle("Choose an Image")
            .setFolderName("Maps Application")
            .setChooserType(ChooserType.CAMERA_AND_GALLERY)
            .allowMultiple(false)
            .setCopyImagesToPublicGalleryFolder(true)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onPause() {
        super.onPause()
        sensorsController.stopSensing()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        sensorsController.startSensing()
        if (sensorsController.getLatLng() != null){
            sensorsController.requestLocation()
        }

        // Check if an image was taken when focus was lost:
        if (::image.isInitialized){
            // Check that the image is different from the previous:
            val uri = image.getUri()!!
            if (!(::previousUri.isInitialized)){
                previousUri = uri
                pinImage()
            }

            if (uri != previousUri) {
                // If so, pin image:
                previousUri = uri
                Log.d("ImageFile", "ImageFile exists:")
                Log.d("ImageFile", uri.toString())
                Log.d("ImageFile", "LatLong:" + sensorsController.getLatLng().toString())
                pinImage()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        easyImage.handleActivityResult(requestCode, resultCode,data,this,
            object: DefaultCallback() {
                override fun onMediaFilesPicked(imageFiles: Array<MediaFile>, source: MediaSource) {
                    // Only one image returned so .first():
                    image = getImageElements(imageFiles).first()
                    imagesInPath += 1
                }

                override fun onImagePickerError(error: Throwable, source: MediaSource) {
                    super.onImagePickerError(error, source)
                }

                override fun onCanceled(source: MediaSource) {
                    super.onCanceled(source)
                }
            })
    }

    /**
     * given a list of photos, it creates a list of ImageElements
     * we do not know how many elements we will have
     * @param returnedPhotos
     * @return list of image elements
     */
    private fun getImageElements(returnedPhotos: Array<MediaFile>): List<ImageElement> {
        val imageElementList: MutableList<ImageElement> = ArrayList<ImageElement>()
        for (file in returnedPhotos) {
            val element = ImageElement(file)
            imageElementList.add(element)
        }
        return imageElementList
    }

    /**
     * check permissions are necessary starting from Android 6
     * if you do not set the permissions, the activity will simply not work and you will be probably baffled for some hours
     * until you find a note on StackOverflow
     * @param context the calling context
     */
    private fun checkPermissions(context: Context) {
        val currentAPIVersion = Build.VERSION.SDK_INT
        if (currentAPIVersion >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder: AlertDialog.Builder =
                        AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("External storage permission is necessary")
                    alertBuilder.setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { _, _ ->
                            ActivityCompat.requestPermissions(
                                context as Activity, arrayOf(
                                    Manifest.permission.READ_EXTERNAL_STORAGE
                                ), MapsActivity.REQUEST_READ_EXTERNAL_STORAGE
                            )
                        })
                    val alert: AlertDialog = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MapsActivity.REQUEST_READ_EXTERNAL_STORAGE
                    )
                }
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    val alertBuilder: AlertDialog.Builder = AlertDialog.Builder(context)
                    alertBuilder.setCancelable(true)
                    alertBuilder.setTitle("Permission necessary")
                    alertBuilder.setMessage("Writing external storage permission is necessary")
                    alertBuilder.setPositiveButton(android.R.string.ok,
                        DialogInterface.OnClickListener { _, _ ->
                            ActivityCompat.requestPermissions(
                                context as Activity, arrayOf(
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ), MapsActivity.REQUEST_WRITE_EXTERNAL_STORAGE
                            )
                        })
                    val alert: AlertDialog = alertBuilder.create()
                    alert.show()
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        MapsActivity.REQUEST_WRITE_EXTERNAL_STORAGE
                    )
                }
            }
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    MapsActivity.REQUEST_CAMERA_CODE
                );
            }
        }
    }

    companion object {
        private val REQUEST_READ_EXTERNAL_STORAGE = 2987
        private val REQUEST_WRITE_EXTERNAL_STORAGE = 7829
        private val REQUEST_CAMERA_CODE = 100
    }
}