package uk.ac.shef.oak.com4510.views
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Image
import uk.ac.shef.oak.com4510.model.Path
import uk.ac.shef.oak.com4510.viewmodels.ImageViewModel
import uk.ac.shef.oak.com4510.viewmodels.PathViewModel
import java.lang.ref.Reference

class PathsFragment : Fragment() {

    private var pathViewModel: PathViewModel? = null
    private var recyclerView: RecyclerView? = null
    private var dataset: ArrayList<Path> = ArrayList()
    private lateinit var pathAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>

    private var textView: TextView? = null

    // The title of the path.
    private var title: String? = null

    // The path id.
    private var path_id = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var v: View = inflater.inflate(R.layout.fragment_path, container, false)

        // Creates an ImageViewModel.
        pathViewModel = ViewModelProvider(this)[PathViewModel::class.java]

        // Returns the value associated with the key title from Bundle.
        title = resources.getString(R.string.pathTitle)

        textView = v.findViewById(R.id.pathsTitle)
        // Sets the text to be displayed.
        textView!!.text = title

        GlobalScope.launch(Dispatchers.IO) {
            pathViewModel?.getAllPaths()?.let { dataset.addAll(it) }
        }

        recyclerView = v.findViewById(R.id.recycler_view)
        recyclerView?.layoutManager = GridLayoutManager(this.context, 1)
        //pathAdapter = PathAdapter(dataset) as RecyclerView.Adapter<RecyclerView.ViewHolder>
//        recyclerView?.adapter = pathAdapter

        return v
    }
}