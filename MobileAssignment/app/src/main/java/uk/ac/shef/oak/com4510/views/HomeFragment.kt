package uk.ac.shef.oak.com4510.views

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.ac.shef.oak.com4510.MapsActivity
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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        title = getView()?.findViewById(R.id.editText)
        title?.setOnFocusChangeListener(this)

        startButton = getView()?.findViewById(R.id.Start)

        startButton?.setOnClickListener(View.OnClickListener {
            val config = this.context?.resources?.configuration
            val locale = config!!.locales[0]

            val dateFormat: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", locale)

            try {
                date = dateFormat.parse(dateFormat.format(Calendar.getInstance().time))

            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (title?.text.toString() == "") {
                title?.setError("this field cant be blank")
            } else {
                val path = Path(0, title?.text.toString(), date!!, "null", "null")

                GlobalScope.launch(Dispatchers.IO) {
                    insertPath(pathViewModel!!, path)
                }
                val intent: Intent = Intent(activity, MapsActivity::class.java)
                startActivity(intent)
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