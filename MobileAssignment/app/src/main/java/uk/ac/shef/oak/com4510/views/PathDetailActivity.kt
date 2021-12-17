package uk.ac.shef.oak.com4510.views

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.MainActivity
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.viewmodels.ImageViewModel
import java.io.Serializable

class PathDetailActivity : AppCompatActivity(), Serializable {

    private var imageViewModel: ImageViewModel? = null
    private lateinit var pathDetailAdapter: Adapter<RecyclerView.ViewHolder>
    private var recyclerView: RecyclerView? = null
    private var dataset: ArrayList<Image> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_detail)
        val pathID = intent.getSerializableExtra("path id") as Int

        var numberOfColumns: Int = 4

        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        initData(pathID)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView?.layoutManager =  GridLayoutManager(this, numberOfColumns)

        pathDetailAdapter = PathDetailAdapter(dataset) as Adapter<RecyclerView.ViewHolder>

        recyclerView?.adapter = pathDetailAdapter

        val backButton = findViewById<FloatingActionButton>(R.id.fab_path_back)
        backButton.setOnClickListener {
            val intent = Intent(this@PathDetailActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initData(path_id: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            imageViewModel?.findImagesByPathId(path_id)?.let { dataset.addAll(it) }

            if (dataset.size == 0){
                val snackbar = Snackbar.make(findViewById(R.id.activity_path_layout),
                    "Sorry, there are no images available for this path.",
                    Snackbar.LENGTH_INDEFINITE)
                val params = snackbar.view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                snackbar.view.layoutParams = params
                snackbar.show()
            }
        }
    }


}