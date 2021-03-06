package `in`.bitspilani.eon.event_organiser.ui


import `in`.bitspilani.eon.BitsEonActivity
import `in`.bitspilani.eon.R
import `in`.bitspilani.eon.event_organiser.models.EventList
import `in`.bitspilani.eon.event_organiser.models.MonoEvent
import `in`.bitspilani.eon.event_organiser.ui.adapter.EventAdapter
import `in`.bitspilani.eon.event_organiser.viewmodel.EventDashboardViewModel
import `in`.bitspilani.eon.login.ui.ActionbarHost
import `in`.bitspilani.eon.utils.Constants
import `in`.bitspilani.eon.utils.ModelPreferencesManager
import `in`.bitspilani.eon.utils.clickWithDebounce
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_dashboard.btn_filter
import kotlinx.android.synthetic.main.fragment_dashboard.btn_filter_clear
import kotlinx.android.synthetic.main.fragment_dashboard.event_search_view
import kotlinx.android.synthetic.main.fragment_dashboard.rv_event_list
import kotlinx.android.synthetic.main.fragment_wishlist.*
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 *
 */
class EventWishListFragment : Fragment() {
    // private val dashboardViewModel by viewModels<EventDashboardViewModel> { getViewModelFactory() }
    private var actionbarHost: ActionbarHost? = null
    private var isWishListed: Boolean = false
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventDashboardViewModel: EventDashboardViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        eventDashboardViewModel = activity?.run {
            ViewModelProviders.of(this).get(EventDashboardViewModel::class.java)
        } ?: throw Exception("Invalid Activity")


        eventDashboardViewModel.getEvents()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_wishlist, container, false)

    }

    override fun onResume() {
        super.onResume()
        actionbarHost?.showToolbar(showToolbar = false, showBottomNav = false)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpObservables()
        setUpClickListeners()
        setUpSearch()

    }


    private fun setUpSearch() {
        event_search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                eventAdapter.filter.filter(newText!!.toString())
                return false
            }

        })
    }

    private fun setUpClickListeners() {
        btn_filter.clickWithDebounce {
            val dialogFragment = FilterDialogFragmentV2()
            dialogFragment.show(childFragmentManager, "filterDialog")

        }
        btn_filter_clear.clickWithDebounce {
            btn_filter_clear.visibility = View.GONE
            eventDashboardViewModel.getEvents()
        }

        btn_close.clickWithDebounce {

            findNavController().popBackStack()
        }
    }

    private fun setUpObservables() {
        eventDashboardViewModel.eventDetailsObservables.observe(viewLifecycleOwner, Observer {
            setEventRecyclerView(it)
        })

        eventDashboardViewModel.eventClickObservable.observe(viewLifecycleOwner, Observer {
            if (ModelPreferencesManager.getInt(Constants.USER_ROLE) == 1)
                findNavController().navigate(
                    R.id.action_homeFragment_to_organiser_eventDetailsFragment,
                    bundleOf("id" to it),
                    NavOptions.Builder()
                        .setPopUpTo(
                            R.id.homeFragment,
                            false
                        ).build()
                )
            else
                findNavController().navigate(
                    R.id.action_wishlist_to_subscriber_eventDetails,
                    bundleOf(Constants.EVENT_ID to it),
                    NavOptions.Builder()
                        .setPopUpTo(
                            R.id.homeFragment,
                            false
                        ).build()
                )
        })

        eventDashboardViewModel.progressLiveData.observe(viewLifecycleOwner, Observer {

            (activity as BitsEonActivity).showProgress(it)
        })

    }

    private fun setEventRecyclerView(eventList: EventList) {

        val isSubscriber: Boolean = ModelPreferencesManager.getInt(Constants.USER_ROLE) == 2
        Timber.e("is subcriber$isSubscriber")
        Timber.e("is from wish list$isSubscriber")
        //if from wishlist
        arguments?.getInt("id")?.let { isWishListed = true }
        layoutManager = LinearLayoutManager(activity)
        if (eventList.fromFilter) {
            btn_filter_clear.visibility = View.VISIBLE
            bindList(eventList, isSubscriber, isWishListed)
        } else
            bindList(eventList, isSubscriber, isWishListed)

    }

    private fun bindList(
        eventList: EventList,
        isSubscriber: Boolean = false,
        wishListed: Boolean
    ) {
        eventAdapter = if (wishListed) {
            val filteredList = eventList.eventList.filter {
                it.is_wishlisted!!
            } as ArrayList<MonoEvent>
            EventAdapter(filteredList, eventDashboardViewModel, isSubscriber)

        } else {

            EventAdapter(eventList.eventList, eventDashboardViewModel, isSubscriber)

        }
        rv_event_list.layoutManager = layoutManager
        rv_event_list.adapter = eventAdapter
    }


    /**
     * toggle visibility of different navigation
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActionbarHost) {
            actionbarHost = context
        }
    }
}
