package com.example.otphind;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_REQUEST_CODE = 101;
    private static final Uri SMS_URI = Uri.parse("content://sms/inbox");

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> smsList;

//    private final BroadcastReceiver smsBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            loadSMS(); // Reload SMS list when a new SMS is received
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        smsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, smsList);
        listView.setAdapter(adapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_REQUEST_CODE);
        } else {
            loadSMS();
//            startSmsListenerService();// Start the service when permission is granted
        }
//        LocalBroadcastManager.getInstance(this).registerReceiver(smsBroadcastReceiver, new IntentFilter("SMS_RECEIVED"));
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Register BroadcastReceiver to receive local broadcasts from SmsListenerService
//        LocalBroadcastManager.getInstance(this).registerReceiver(smsBroadcastReceiver, new IntentFilter("SMS_RECEIVED"));
        loadSMS(); // Load SMS messages when the activity resumes
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the BroadcastReceiver when the activity is paused
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(smsBroadcastReceiver);
    }

    private void loadSMS() {
        smsList.clear(); // Clear the existing list

        // Query the SMS content provider to get the latest SMS messages
        Cursor cursor = getContentResolver().query(SMS_URI, null, null, null, Telephony.Sms.DATE + " DESC LIMIT 5");

        if (cursor != null && cursor.moveToFirst()) {
            int bodyIndex = cursor.getColumnIndex(Telephony.Sms.BODY);

            do {
                // Read the message body and add it to the smsList
                String body = cursor.getString(bodyIndex);
                smsList.add(body);
            } while (cursor.moveToNext());

            cursor.close(); // Close the cursor
            adapter.notifyDataSetChanged(); // Notify the adapter that the data set has changed
        } else {
            // Handle case when no SMS messages are found
            Toast.makeText(this, "No SMS messages found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                startSmsListenerService(); // Start the service when permission is granted
            } else {
                Toast.makeText(this, "SMS permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    private void startSmsListenerService() {
//        Intent serviceIntent = new Intent(this, SmsListenerService.class);
//        ContextCompat.startForegroundService(this, serviceIntent);
//    }
}