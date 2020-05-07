package `in`.bitspilani.eon.event_subscriber.subscriber.payments

import `in`.bitspilani.eon.BitsEonActivity
import `in`.bitspilani.eon.R
import `in`.bitspilani.eon.login.data.Data
import `in`.bitspilani.eon.login.ui.ActionbarHost
import `in`.bitspilani.eon.utils.Constants
import `in`.bitspilani.eon.utils.ModelPreferencesManager
import `in`.bitspilani.eon.utils.clickWithDebounce
import `in`.bitspilani.eon.utils.getViewModelFactory
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.layout_dialog_payment_success.view.*
import kotlinx.android.synthetic.main.payment_fragment.*


class PaymentFrag : Fragment() {

    private val paymentViewModel by viewModels<PaymentViewModel> { getViewModelFactory() }
    private var actionbarHost: ActionbarHost? = null

    var eventId: Int = 0

    var hashMap: HashMap<String, Any> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.payment_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservables()
        getDataFromArgs()
        setClicks()
        actionbarHost?.showToolbar(showToolbar = false, showBottomNav = false)

        val textCount: Int = 0

        et_expiry_date.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {


                val source = s.toString()
                val length = source.length

                val stringBuilder = StringBuilder()

                stringBuilder.append(source)

                if (length == 4 && !stringBuilder.contains("/")) {

                    // stringBuilder.deleteCharAt(length-1);

                    stringBuilder.insert(stringBuilder.length - 2, "/")
                    et_expiry_date.setText(stringBuilder);

                }


            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })

    }

    fun validateUsername(username: String) : Boolean{

        val pattern = Regex("[a-zA-Z.? ]*")


        return pattern.containsMatchIn(username)


    }

    fun setObservables() {

        paymentViewModel.subscriptionData.observe(viewLifecycleOwner, Observer {

            showSuccessDialog()
        })

        paymentViewModel.errorData.observe(viewLifecycleOwner, Observer {
            showUserMsg(it)

        })


        paymentViewModel.progressLiveData.observe(viewLifecycleOwner, Observer {

            (activity as BitsEonActivity).showProgress(it)
        })

    }

    fun getDataFromArgs() {

        eventId = arguments!!.getInt(Constants.EVENT_ID, 0)
        hashMap.put("event_id", eventId)


        val userData = ModelPreferencesManager.get<Data>(Constants.CURRENT_USER)

        hashMap.put("user_id", userData!!.user!!.user_id)
        hashMap.put("no_of_tickets", arguments!!.getInt("no_of_tickets", 0))
        hashMap.put("amount", arguments!!.getInt(Constants.AMOUNT, 0))
        hashMap.put("discount_amount", arguments!!.getInt(Constants.DISCOUNT_AMOUNT, 0))

    }

    fun setClicks() {

        btn_proceed.setOnClickListener {
            if (et_card_owner_name.text.isEmpty())
                showUserMsg("Please enter Card owner's name")
            else if (et_card_number.text.isEmpty())
                showUserMsg("Please enter Card number")
            else if (et_card_number.text.toString().length != 16)
                showUserMsg("please enter valid card number")
            else if (et_expiry_date.text.isEmpty()) {
                showUserMsg("Please enter expiry date")
            } else if (!checkExpiryDate()) {
                showUserMsg("Please enter valid expiry date")

            } else {


                hashMap.put("card_number", et_card_number.text.toString().toLong())
                hashMap.put("expiry_month", expiryMonth)
                hashMap.put("expiry_year", 2000+expiryYear)

                paymentViewModel.payAndSubscribe(hashMap)
            }

        }

        iv_back_payment.clickWithDebounce { findNavController().popBackStack() }

    }

    fun showSuccessDialog() {
        //Inflate the dialog with custom view
        val mDialogView =
            LayoutInflater.from(activity).inflate(R.layout.layout_dialog_payment_success, null)
        //AlertDialogBuilder
        val mBuilder = AlertDialog.Builder(activity)
            .setView(mDialogView)
        //show dialog
        val mAlertDialog = mBuilder.show()

        mDialogView.btn_okay.clickWithDebounce {

            findNavController().popBackStack(R.id.eventDetails, false)

//            findNavController().navigate(
//                R.id.action_payment_to_eventDetail,
//                bundleOf(Constants.EVENT_ID to eventId),
//                NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build()
//            )

            mAlertDialog.dismiss()
        }

    }

    fun showUserMsg(msg: String) {
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is ActionbarHost) {
            actionbarHost = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        actionbarHost?.showToolbar(showToolbar = false, showBottomNav = false)
    }

    var expiryMonth: Int = 0
    var expiryYear: Int=0

    fun checkExpiryDate(): Boolean {

        var date: String = et_expiry_date.text.toString()
        expiryMonth = date.substring(0, 2).toInt()
        expiryYear = date.substring(3, 5).toInt()

        if (expiryMonth < 1 || expiryMonth > 12) {

            return false
        }


        if (expiryYear < 20) {
            return false
        }

        if(expiryYear==20 && expiryMonth<4)
        {return false}

        return true
    }


}
