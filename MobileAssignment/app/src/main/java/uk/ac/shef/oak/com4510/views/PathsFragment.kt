package uk.ac.shef.oak.com4510.views
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.viewmodels.ImageViewModel

class PathsFragment : Fragment() {

    private var imageViewModel: ImageViewModel? = null


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
        imageViewModel = ViewModelProvider(this)[ImageViewModel::class.java]

        // Returns the value associated with the key title from Bundle.
        title = requireArguments().getString("title")

        // Returns the value associated with the key path id from Bundle.
        path_id = requireArguments().getString("path_id")!!.toInt()

        textView = v.findViewById<TextView>(R.id.title)
        // Sets the text to be displayed.
        textView!!.setText(title)


        /**
         * Find the target path according to the path id.
         * Adds the given observer to the observers list within the lifespan of the given owner.
         */
        imageViewModel!!.findImagesByPathId(path_id)!!.observe(viewLifecycleOwner,
            {
//                recyclerView = v.findViewById(R.id.recycler_view)
//
//                // Initialize the PathDetailAdapter
//                pathDetailAdapter = PathDetailAdapter(images, false)

                // The number of columns in the grid.
                val numberOfColumns = 4
                // Set the GridLayoutManager that this RecyclerView will use.
//                recyclerView.setLayoutManager(GridLayoutManager(context, numberOfColumns))
//                // Set the path detail adapter to provide child views on demand.
//                recyclerView.setAdapter(pathDetailAdapter)
            })
        return v
    }
}