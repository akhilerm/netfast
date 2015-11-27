package com.arjunsajeev.netfast;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Spinner planSpinner = (Spinner) findViewById(R.id.spinner);
        Button activateButton = (Button)findViewById(R.id.activateButton);
        final ProgressDialog[] progressDialog = new ProgressDialog[1];
        final TextView planTextView = (TextView)findViewById(R.id.textView);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.plans_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        planSpinner.setAdapter(adapter);

        planSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String plan = planSpinner.getSelectedItem().toString();
                //plan = "R.string." + plan;
               // planTextView.setText(getApplicationContext().getResources().getString(Integer.parseInt(plan)));
                //planTextView.setText("Data Plan Selected!"+Math.random());
                String[] planDescriptions = getApplicationContext().getResources().getStringArray(R.array.plan_descriptions);
                planTextView.setText(planDescriptions[position]);
                //String s = getApplicationContext().getResources().getString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               progressDialog[0] = ProgressDialog.show(MainActivity.this, "Status",
                        "Sending Activation SMS", true);
                String plan=  planSpinner.getSelectedItem().toString();
                //Toast.makeText(getApplicationContext(),plan,Toast.LENGTH_LONG).show();
                String phoneNumber = "123";


//                Uri uri = Uri.parse("smsto:" + phoneNumber);
//
//                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
//                 smsIntent.setData(uri); // We just set the data in the constructor above
//
//                smsIntent.putExtra("sms_body", plan);
//
//                startActivity(smsIntent);

                String smsBody = "STV "+plan;

                String SMS_SENT = "SMS_SENT";
                String SMS_DELIVERED = "SMS_DELIVERED";

                PendingIntent sentPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SMS_SENT), 0);
                PendingIntent deliveredPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, new Intent(SMS_DELIVERED), 0);

// For when the SMS has been sent
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                progressDialog[0].dismiss();;
                                Toast.makeText(context, "Activation SMS sent successfully", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                                Toast.makeText(context, "Generic failure cause", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NO_SERVICE:
                                Toast.makeText(context, "Service is currently unavailable", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_NULL_PDU:
                                Toast.makeText(context, "No pdu provided", Toast.LENGTH_SHORT).show();
                                break;
                            case SmsManager.RESULT_ERROR_RADIO_OFF:
                                Toast.makeText(context, "Radio was explicitly turned off", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SMS_SENT));

// For when the SMS has been delivered
                registerReceiver(new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        switch (getResultCode()) {
                            case Activity.RESULT_OK:
                                Toast.makeText(getBaseContext(), "Activation SMS delivered", Toast.LENGTH_SHORT).show();
                                break;
                            case Activity.RESULT_CANCELED:
                                Toast.makeText(getBaseContext(), "Activation SMS not delivered", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                }, new IntentFilter(SMS_DELIVERED));

// Get the default instance of SmsManager
                SmsManager smsManager = SmsManager.getDefault();
// Send a text based SMS
                smsManager.sendTextMessage(phoneNumber, null, smsBody, sentPendingIntent, deliveredPendingIntent);

            }
        });

    }
}