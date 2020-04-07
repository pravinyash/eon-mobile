package `in`.bitspilani.eon.eventOrganiser.ui

import `in`.bitspilani.eon.R
import `in`.bitspilani.eon.eventOrganiser.data.Invitee
import `in`.bitspilani.eon.utils.MarginItemDecoration
import `in`.bitspilani.eon.utils.clickWithDebounce
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_invitee.*


/**
 * A simple [Fragment] subclass.
 *
 */
class InviteeFragment : Fragment(),CallbackListener {

    // tab titles
    private val titles =
        arrayOf("Movies", "Events")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invitee, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        fab.clickWithDebounce {
            showDialog()
        }
    }

    private fun initView() {
        //dummy list
        val listOfEvent = mutableListOf<Invitee>()
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )
        listOfEvent.add(
            Invitee( "abcd@gmail.com","200","200","9876543210")
        )

        rv_invitee_list.layoutManager = LinearLayoutManager(activity)
        rv_invitee_list.addItemDecoration(
            MarginItemDecoration(
                resources.getDimension(R.dimen._16sdp).toInt())
        )
        val movieListAdapter = InviteesAdapter(listOfEvent)
        rv_invitee_list.adapter = movieListAdapter

    }

    private fun showDialog() {
        val dialogFragment = AddInviteeFragment(this)
        dialogFragment.show(childFragmentManager, "signature")
    }
    override fun onDataReceived(data: String) {

    }


}