package com.quendeltechfirm.paypalbraintreetest

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.json.JSONObject

class MainActivityViewModel : ViewModel() {

    var statusMsg = ObservableField<String>()
    var isLoading = ObservableField<Boolean>()
    var eventResponse = MutableLiveData<EventData>()
    val disposable = CompositeDisposable()

    val transactionAmount = 10 // USD

    init {
        setMsg("Checkout using Braintree SDK")
    }

    fun getClientToken() {
        setMsg("Loading...")
        setLoading(true)
        disposable.add(
            Single.fromCallable {
                RetrofitConnectionService.getInstance().getToken()
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val json = JSONObject(it.body().string())
                    val clientToken = json.getString("clientToken")
                    if (json.getBoolean("status")) {
                        eventResponse.value = EventData(
                            true, false, clientToken
                        )
                    } else {
                        setMsg(clientToken)
                        setLoading(false)
                    }
                }, {
                    setMsg(it.message)
                    setLoading(false)
                })
        )
    }

    fun sendNonceForCheckout(nonce: String) {
        disposable.add(
            Single.fromCallable {
                RetrofitConnectionService.getInstance().checkout(nonce, transactionAmount.toString())
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    val json = JSONObject(it.body().string())
                    val response = json.getString("result")
                    if (json.getBoolean("status")) {
                        eventResponse.value = EventData(
                            false, true
                            , ""
                        )
                    }
                    setLoading(false)
                    setMsg(response)
                }, {
                    setMsg(it.message)
                    setLoading(false)
                })
        )

    }

    fun setMsg(msg: String?) {
        statusMsg.set(msg)
    }

    fun setLoading(b: Boolean) {
        isLoading.set(b)
    }

    data class EventData(val onClientTokenGenerateSuccess: Boolean, val onPaymentSuccessful: Boolean, val token: String)
}