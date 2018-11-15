# Paypal Integration

There are two way to implementation - [Express Checkout or Native Checkout](https://paypal.github.io/paypalnativecheckout-docs/) and [Braintree SDK](https://developers.braintreepayments.com/start/hello-client/android/v2)  

# BRAINTREE SDK IMPLEMENTATION
1. Login to [sendbox account](https://sandbox.braintreegateway.com/login)  
`Link your Paypal account under *Account->My User*, if not linked`  
2. Integrate server side script for generate token.  
   Download library from [here](https://developers.braintreepayments.com/start/hello-server/php)  
   Create php file below for generate token and start transction usin nonce -    
   *init_gateway.php*  
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
            'merchantId' => 'w9cqw8z83y7mndcz',
            'publicKey' => 'q3s3hvk4sqcr7bv7',
            'privateKey' => '7cb07b1adc065c2c1062c0461c85770d'
        ]);
   ?>
   ```
