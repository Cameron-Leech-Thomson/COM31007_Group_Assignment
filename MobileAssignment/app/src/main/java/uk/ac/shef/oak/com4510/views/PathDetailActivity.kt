package uk.ac.shef.oak.com4510.views

import android.app.Activity
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.viewmodels.ImageViewModel

class PathDetailActivity : Activity() {

    private var imageViewModel: ImageViewModel? = null
    private lateinit var pathDetailAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    private var recyclerView: RecyclerView? = null
    private var dataset: ArrayList<Image> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_detail)

        var numberOfColumns: Int = 4

        //TODO get the pathid from the path tab
        initData(1)

        recyclerView = findViewById(R.id.grid_recycler_view)
        recyclerView?.layoutManager =  GridLayoutManager(this, numberOfColumns)

        pathDetailAdapter = PathDetailAdapter(dataset) as Adapter<RecyclerView.ViewHolder>

        recyclerView?.adapter = pathDetailAdapter
    }

    private fun initData(path_id: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            imageViewModel?.findImagesByPathId(path_id)?.let { dataset.addAll(it) }
        }
    }



}