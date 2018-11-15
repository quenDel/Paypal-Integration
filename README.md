# Paypal Integration

There are two way to implementation - [Express Checkout or Native Checkout](https://paypal.github.io/paypalnativecheckout-docs/) and [Braintree SDK](https://developers.braintreepayments.com/start/hello-client/android/v2)  

# BRAINTREE SDK IMPLEMENTATION
1. Login to [sendbox account](https://sandbox.braintreegateway.com/login)  
`Link your Paypal account under *Account-> My User*, if not linked`  
2. Integrate server side script for generate token.  
   Download library from [here](https://developers.braintreepayments.com/start/hello-server/php)  
   Create php file below for generate token and start transction usin nonce -  
   
   *init_gateway.php*    (Get merchantId/publicKey/privateKey from dashboad under Settings->API Keys)
      <!-- language: php -->
   ```
   <?php 
        session_start();
        require_once ("lib/autoload.php");
        if(file_exists(__DIR__ . "/../.env")){
          $dotenv = new Dotenv\Dotenv(__DIR__ . "/../");
          $dotenv->load();
        }
        $gateway = new Braintree_Gateway([
            'environment' => 'sandbox',
            'merchantId' => 'xxxxxxxxxxxx',
            'publicKey' => 'xxxxxxxxxxxxxxxx',
            'privateKey' => 'xxxxxxxxxxxxxxxxxxxxxxxxxxxx'
        ]);
   ?>
   ```
   *generate_token.php*  
    <!-- language: php -->
    ```
    <?php
         require_once("braintree_init.php");
         require_once("lib/Braintree.php");
         echo ($clientToken = $gateway->clientToken()->generate());
      ?>
      ```  
   *checkout.php*  
    <!-- language: php -->
    ```
    <?php
      require_once("braintree_init.php");
      require_once 'lib/Braintree.php';
      $nonce = $_POST['nonce'];
      $amount = $_POST['amount'];
      $result = $gateway->transaction()->sale([
        'amount' => $amount,
        'paymentMethodNonce' => $nonce,
        'options' => ['submitForSettlement' => True ]  
      ]);
      ?>
      ```
3. Android integration  
  1. Add gradle path `implementation 'com.braintreepayments.api:drop-in:3.7.1'`  
  2. Make server call to get clientToken from `generate_token.php`
  3. Send clientToken to Braintree server to get nonce - 
   ```
   public void onBraintreeSubmit(View v) {
        DropInRequest dropInRequest = new DropInRequest()
                                          .clientToken("eyJ2ZXJzZlbm1vIjoib2ZmIn0=");
        startActivityForResult(dropInRequest.getIntent(this), REQUEST_CODE);
   }
   ```  
   4. Override onActivityResult method to receive nonce -
   ```
   @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
     if (requestCode == REQUEST_CODE) {
       if (resultCode == Activity.RESULT_OK) {
         DropInResult result = data.getParcelableExtra(DropInResult.EXTRA_DROP_IN_RESULT);
         // use the result to update your UI and send the payment method nonce to your server
         PaymentMethodNonce nonce = result.getPaymentMethodNonce();
         String strNonce = nonce.getNonce();
         sendNonceForCheckout(strNonce);
       } else if (resultCode == Activity.RESULT_CANCELED) {
         // the user canceled
       } else {
         // handle errors here, an exception may be available in
         Exception error = (Exception) data.getSerializableExtra(DropInActivity.EXTRA_ERROR);
       }
     }
   }
   ```
   5. Send payment method nonce to server (Checkout now)  
   ```
   void sendNonceForCheckout(String nonce) {
     AsyncHttpClient client = new AsyncHttpClient();
     RequestParams params = new RequestParams();
     params.put("payment_method_nonce", nonce);
     client.post("http://your-server/checkout", params,
       new AsyncHttpResponseHandler() {
         // Your implementation here
       }
     );
   }
   ```
   That's all :)
   
