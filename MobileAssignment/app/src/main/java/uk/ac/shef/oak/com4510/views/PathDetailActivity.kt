package uk.ac.shef.oak.com4510.views

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.viewmodels.ImageViewModel
import java.io.Serializable

class PathDetailActivity : Activity(), Serializable {

    private var imageViewModel: ImageViewModel? = null
    private lateinit var pathDetailAdapter: Adapter<RecyclerView.ViewHolder>
    private var recyclerView: RecyclerView? = null
    private var dataset: ArrayList<Image> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_detail)
        val pathID = intent.getSerializableExtra("path id") as Int

        var numberOfColumns: Int = 4

        initData(pathID)

        //TODO THIS IS THE ERROR - uncomment
        //imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView?.layoutManager =  GridLayoutManager(this, numberOfColumns)

        pathDetailAdapter = PathDetailAdapter(dataset) as Adapter<RecyclerView.ViewHolder>

        recyclerView?.adapter = pathDetailAdapter
    }

    private fun initData(path_id: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            imageViewModel?.findAllImages()?.let { dataset.addAll(it) }
        }
    }


}