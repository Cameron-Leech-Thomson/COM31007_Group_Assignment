package uk.ac.shef.oak.com4510.views

import android.app.PendingIntent.getActivity
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pl.aprilapps.easyphotopicker.MediaFile
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.viewmodels.ImageViewModel
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GalleryFragment: Fragment() {

    private var imageViewModel: ImageViewModel? = null
    private lateinit var galleryAdapter: Adapter<RecyclerView.ViewHolder>
    private var recyclerView: RecyclerView? = null
    private var dataset: ArrayList<Image> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val v: View = inflater.inflate(R.layout.fragment_gallery, container, false)


        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        GlobalScope.launch(Dispatchers.IO) {
            imageViewModel?.findAllImages()?.let { dataset.addAll(it) }
        }

        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView?.layoutManager = GridLayoutManager(this.context, 4)
        galleryAdapter = GalleryAdapter(dataset) as Adapter<RecyclerView.ViewHolder>
        recyclerView?.adapter = galleryAdapter

        return v
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}