package com.example.codeseasy.stripeandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    PaymentSheet paymentSheet;
    String paymentIntentClientSecret;
    PaymentSheet.CustomerConfiguration customerConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchPaymentApi();

        Button button = findViewById(R.id.btn_pay);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (paymentIntentClientSecret != null)
                    paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, new PaymentSheet.Configuration("Harry",
                            customerConfig));
            }

        });

        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);

    }

    public void fetchPaymentApi() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = "https://6ac3-2401-4900-1c29-3a5-90ab-bbf0-ce51-e448.ngrok-free.app/payment-sheet";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("R", response);
                        try {
                            final JSONObject result = new JSONObject(response);
                            customerConfig = new PaymentSheet.CustomerConfiguration(
                                    result.getString("customer"),
                                    result.getString("ephemeralKey")
                            );
                            paymentIntentClientSecret = result.getString("paymentIntent");
                            PaymentConfiguration.init(getApplicationContext(), result.getString("publishableKey"));

                        } catch (JSONException e) { e.printStackTrace(); }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            protected Map<String, String> getParams() {
                Map<String, String> paramV = new HashMap<>();
                paramV.put("authKey", "abc");
                return paramV;
            }
        };
        queue.add(stringRequest);
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            Log.e("R:", "Canceled");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Log.e("App", "Got error: ", ((PaymentSheetResult.Failed) paymentSheetResult).getError());
        } else if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            // Display for example, an order confirmation screen
            Log.e("R:", "Completed");
        }
    }
}