package `in`.bitspilani.eon.event_organiser.ui


import `in`.bitspilani.eon.BitsEonActivity
import `in`.bitspilani.eon.R
import `in`.bitspilani.eon.event_organiser.models.EventList
import `in`.bitspilani.eon.event_organiser.models.MonoEvent
import `in`.bitspilani.eon.event_organiser.ui.adapter.EventAdapter
import `in`.bitspilani.eon.event_organiser.viewmodel.EventDashboardViewModel
import `in`.bitspilani.eon.event_organiser.viewmodel.NotificationViewModel
import `in`.bitspilani.eon.login.ui.ActionbarHost
import `in`.bitspilani.eon.utils.*
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_dashboard.*
import timber.log.Timber


/**
 * A simple [Fragment] subclass.
 *
 */
class HomeFragment : Fragment() {
    private var actionbarHost: ActionbarHost? = null
    lateinit var eventListUpdated: ArrayList<MonoEvent>
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventDashboardViewModel: EventDashboardViewModel

    private val notificationViewModel by viewModels<NotificationViewModel> { getViewModelFactory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventDashboardViewModel = activity?.run {
            ViewModelProviders.of(this).get(EventDashboardViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

        eventDashboardViewModel.getEvents()
        eventDashboardViewModel.setupEventTypes()
        setHasOptionsMenu(true)
        eventDashboardViewModel.resetFilters()


    }



    lateinit var itemToHide: MenuItem

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.top_navigation, menu)
        if (ModelPreferencesManager.getInt(Constants.USER_ROLE) == 2) {
            itemToHide = menu.findItem(R.id.notificationFragment)
            itemToHide?.isVisible = true
        }
        super.onCreateOptionsMenu(menu, inflater)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_dashboard, container, false)

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
                defocusAndHideKeyboard(activity)
                return false
            }


            override fun onQueryTextChange(newText: String?): Boolean {


                eventAdapter.filter.filter(newText.toString())

                return false
            }


        })

        if (ModelPreferencesManager.getInt(Constants.USER_ROLE) == 2){

            notificationViewModel.getNotification()
        }
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
    }

    private fun setUpObservables() {
        eventDashboardViewModel.eventDetailsObservables.observe(viewLifecycleOwner, Observer {
            setEventRecyclerView(it)
        })

        eventDashboardViewModel.eventClickObservable.observe(viewLifecycleOwner, Observer {

            event_search_view.setQuery("", false);
            event_search_view.clearFocus();

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
                    R.id.eventDetails,
                    bundleOf(Constants.EVENT_ID to it)
                )
        })

        eventDashboardViewModel.progressLiveData.observe(viewLifecycleOwner, Observer {

            (activity as BitsEonActivity).showProgress(it)
        })

        eventDashboardViewModel.errorView.observe(viewLifecycleOwner, Observer {

            if (it != null)
                view?.showSnackbar(it, 0)
        })

        notificationViewModel.notificationLiveData.observe(viewLifecycleOwner, Observer {

            if(it.data.size>0){

                itemToHide.setIcon(R.drawable.ic_notifications)

            }
        })
    }

    private fun setEventRecyclerView(eventList: EventList) {

        eventListUpdated = eventList.eventList
        val isSubscriber: Boolean = ModelPreferencesManager.getInt(Constants.USER_ROLE) == 2
        layoutManager = LinearLayoutManager(activity)
        if (eventList.fromFilter) {
            btn_filter_clear.visibility = View.VISIBLE
            bindList(eventListUpdated, isSubscriber)
        } else
            bindList(eventListUpdated, isSubscriber)

    }

    private fun bindList(
        eventList: ArrayList<MonoEvent>,
        isSubscriber: Boolean = false
    ) {


        rv_event_list.layoutManager = layoutManager
        eventAdapter = EventAdapter(eventList, eventDashboardViewModel, isSubscriber)
        rv_event_list.adapter = eventAdapter
    }


    override fun onResume() {
        super.onResume()

        if (ModelPreferencesManager.getInt(Constants.USER_ROLE) == UserType.SUBSCRIBER.ordinal)
            actionbarHost?.showToolbar(
                showToolbar = true,
                title = "Event Management",
                showBottomNav = true
            )
        else
            actionbarHost?.showToolbar(
                showToolbar = true,
                title = "Event Management",
                showBottomNav = false
            )
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
