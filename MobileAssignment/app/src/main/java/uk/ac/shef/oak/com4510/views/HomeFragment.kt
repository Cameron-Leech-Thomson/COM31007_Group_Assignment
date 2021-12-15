package uk.ac.shef.oak.com4510.views

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import uk.ac.shef.oak.com4510.R
import uk.ac.shef.oak.com4510.model.Path
import uk.ac.shef.oak.com4510.viewmodels.PathViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment: Fragment(), View.OnFocusChangeListener {

    private var pathViewModel: PathViewModel? = null
    private var title: EditText? = null
    private var date: Date? = null
    private var startButton: Button? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        pathViewModel = ViewModelProvider(this)[PathViewModel::class.java]

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        title = getView()?.findViewById(R.id.editText)
        title?.setOnFocusChangeListener(this)

        startButton = getView()?.findViewById(R.id.Start)

        startButton?.setOnClickListener(View.OnClickListener {

            val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")

            try {
                date = dateFormat.parse(dateFormat.format(Calendar.getInstance().time))

            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (title?.text.toString() == "") {
                title?.setError("this field cant be blank")
            } else {

            }
        })
    }

    private suspend fun insertPath(pathViewModel: PathViewModel, path: Path) {
        pathViewModel.insertPath(path)
    }

    override fun onFocusChange(p0: View?, p1: Boolean) {
        title?.setHint("")
    }
}