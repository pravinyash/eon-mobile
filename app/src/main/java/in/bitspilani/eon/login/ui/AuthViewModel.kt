package `in`.bitspilani.eon.login.ui

import `in`.bitspilani.eon.BaseViewModel
import `in`.bitspilani.eon.api.ApiService
import `in`.bitspilani.eon.api.RestClient
import `in`.bitspilani.eon.login.data.*
import `in`.bitspilani.eon.utils.ApiCallback
import `in`.bitspilani.eon.utils.SingleLiveEvent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

enum class OrganiserDetailsSteps(val desc: String) {
    BASIC_DETAILS("Basic Details"),
    BANK_DETAILS("Bank Details"),
    PASSWORD("Password"),
}

enum class ForgotPasswordSteps(val desc: String) {
    ENTER_DETAILS("Basic"),
    VERIFICATION_CODE("Verification Code"),
    PASSWORD("Password"),
    SUCCESS("Success"),
}

enum class USER_TYPE(val desc: String) {
    ORGANISER("Event Organizer"),
    SUBSCRIBER("Event Subscriber"),
}

enum class ROLE(val role : String){
    ORGANIZER("organizer"),
    SUBSCRIBER("subscriber")

}
class AuthViewModel : BaseViewModel() {

    val progress: SingleLiveEvent<Boolean> = SingleLiveEvent()


    var registerCurrentStep: OrganiserDetailsSteps =
        OrganiserDetailsSteps.BASIC_DETAILS

    var forgotPasswordSteps: ForgotPasswordSteps = ForgotPasswordSteps.ENTER_DETAILS
    var userType: USER_TYPE? = null
    private val restClient: RestClient = RestClient()
    var registerData: SingleLiveEvent<Data> = SingleLiveEvent()
    var registerError: MutableLiveData<String> = MutableLiveData()
    val loginLiveData: SingleLiveEvent<LoginResponse> = SingleLiveEvent()
    val generateCodeLiveData: SingleLiveEvent<GenerateCodeResponse> = SingleLiveEvent()
    val resetPasswordLiveData: SingleLiveEvent<ResetPasswordResponse> = SingleLiveEvent()


    fun login(username:String, password:String){
        try {

            val body = JsonObject()
            body.addProperty("email",username)
            body.addProperty("password",password)
            showProgress(true)
            restClient.noAuthClient.create(ApiService::class.java).login(body)
                .enqueue(object : ApiCallback<LoginResponse>(){
                    override fun onSuccessResponse(responseBody: LoginResponse) {
                        loginLiveData.postValue(responseBody)
                        showProgress(false)
                    }

                    override fun onApiError(errorType: ApiError, error: String?) {
                        showProgress(false)
                        errorView.postValue(error)
                    }
                })

        }catch (e : Exception){

        }

    }

    fun generateCode(email:String)
    {
        val body = JsonObject()
        body.addProperty("email",email)
        showProgress(true)
        restClient.noAuthClient.create(ApiService::class.java).generateCode(body)
            .enqueue(object : ApiCallback<GenerateCodeResponse>(){
                override fun onSuccessResponse(generateCodeResponse: GenerateCodeResponse) {
                    generateCodeLiveData.postValue(generateCodeResponse)
                    showProgress(false)
                }

                override fun onApiError(errorType: ApiError, error: String?) {
                    showProgress(false)
                    errorView.postValue(error)
                }
            })

    }

    fun resetPassword(email:String,code:String,password:String)
    {
        val body = JsonObject()
        body.addProperty("email",email)
        body.addProperty("code",code)
        body.addProperty("password",password)
        showProgress(true)
        restClient.authClient.create(ApiService::class.java).resetPassword(body)
            .enqueue(object : ApiCallback<ResetPasswordResponse>(){
                override fun onSuccessResponse(resetPasswordResponse: ResetPasswordResponse) {
                    resetPasswordLiveData.postValue(resetPasswordResponse)
                    showProgress(false)
                }

                override fun onApiError(errorType: ApiError, error: String?) {
                    showProgress(false)
                    errorView.postValue(error)
                }
            })
    }

    fun register(hashMap: HashMap<String, Any>) {

        try {

            showProgress(true)

            restClient.noAuthClient.create(ApiService::class.java).registerUser(hashMap)
                .enqueue(object : ApiCallback<SignUpResponse>(){
                    override fun onSuccessResponse(response: SignUpResponse) {

                        showProgress(false)
                        registerData.postValue(response.data)
                    }

                    override fun onApiError(errorType: ApiError, error: String?) {
                        showProgress(false)
                        errorView.postValue(error)
                    }
                })
        } catch (e: Exception) {


        }
    }


}