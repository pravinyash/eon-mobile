package `in`.bitspilani.eon.utils

/**
 * Extension functions for Fragment.
 */

import `in`.bitspilani.eon.EonViewModelFactory
import `in`.bitspilani.eon.api.ApiService
import `in`.bitspilani.eon.api.RestClient
import androidx.fragment.app.Fragment


fun Fragment.getViewModelFactory(): EonViewModelFactory {
    //Add common things here
    val apiService=  RestClient().authClient.create(ApiService::class.java)
    return EonViewModelFactory(this,apiService)
}
