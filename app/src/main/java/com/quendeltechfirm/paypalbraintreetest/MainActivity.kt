package com.quendeltechfirm.paypalbraintreetest

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.quendeltechfirm.paypalbraintreetest.databinding.ActivityMainBinding
import android.content.Intent.getIntent
import com.braintreepayments.api.dropin.DropInRequest
import com.braintreepayments.api.dropin.DropInActivity
import android.app.Activity
import com.braintreepayments.api.models.PaymentMethodNonce
import com.braintreepayments.api.dropin.DropInResult
import android.content.Intent




class MainActivity : AppCompatActivity() {

    val REQUEST_CODE = 101

    lateinit var viewModel: MainActivityViewModel
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel::class.java)

        binding.viewModel = viewModel

        viewModel.eventResponse.observe(this, Observer {handleResponse(it) })
    }

    fun checkout(view: View) {
        viewModel.getClientToken()
    }

    private fun braintreeSubmit(token: String) {
        val dropInRequest = DropInRequest().clientToken(token)
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val result = data!!.getParcelableExtra<DropInResult>(DropInResult.EXTRA_DROP_IN_RESULT)
                    // use the result to update your UI and send the payment method nonce to your server
                    val nonce = result.paymentMethodNonce
                    val strNonce = nonce!!.nonce
                    viewModel.sendNonceForCheckout(strNonce)
                }
                Activity.RESULT_CANCELED -> {
                    // the user canceled
                    viewModel.setMsg("User canceled")
                    viewModel.setLoading(false)
                }
                else -> {
                    // handle errors here, an exception may be available in
                    val error = data!!.getSerializableExtra(DropInActivity.EXTRA_ERROR) as Exception
                    viewModel.setMsg(error.message)
                    viewModel.setLoading(false)
                }
            }
        }
    }


    private fun handleResponse(response: MainActivityViewModel.EventData?) {
        when {
            response?.onClientTokenGenerateSuccess!! -> {
                val token = response.token
                braintreeSubmit(token)
            }
            response.onPaymentSuccessful -> {

            }
        }
    }


}
