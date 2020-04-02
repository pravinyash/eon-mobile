package `in`.bitspilani.eon.presentation.auth.fragments.signup

import `in`.bitspilani.eon.R
import `in`.bitspilani.eon.utils.Validator
import `in`.bitspilani.eon.utils.clickWithDebounce
import `in`.bitspilani.eon.utils.getViewModelFactory
import `in`.bitspilani.eon.viewmodel.AuthViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_basic_details.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BasicInfo.newInstance] factory method to
 * create an instance of this fragment.
 */
class BasicDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val authViewModel by viewModels<AuthViewModel> { getViewModelFactory() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_basic_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btn_next.clickWithDebounce {

            if (Validator.isValidName(edt_org_name, true) &&
                Validator.isValidName(edt_org_contact, true) &&
                Validator.isValidName(edt_org_address, true)
            )

                findNavController().navigate(R.id.action_basicInfo_to_createPasswordFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BasicInfo.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BasicDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
