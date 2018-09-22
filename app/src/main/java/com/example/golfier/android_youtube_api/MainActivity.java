package com.example.golfier.android_youtube_api;


import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.os.Bundle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class MainActivity extends Activity {

  private TextView mOutputText;
  private Button mCallApiButton;

  ProgressDialog mProgress;




  /**
   * Create the main activity.
   * @param savedInstanceState previously saved instance data.
   */
  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mCallApiButton = (Button) findViewById(R.id.button);
    mOutputText = (TextView) findViewById(R.id.textview);


    mCallApiButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          mCallApiButton.setEnabled(false);
          mOutputText.setText("");
          getResultsFromApi();
          mCallApiButton.setEnabled(true);
      }
    });


    mProgress = new ProgressDialog(this);
    mProgress.setMessage("Calling YouTube Data API ...");


  }


  /**
   * Called when an activity launched here (specifically, AccountPicker
   * and authorization) exits, giving you the requestCode you started it with,
   * the resultCode it returned, and any additional data from it.
   * @param requestCode code indicating which activity result is incoming.
   * @param resultCode code indicating the result of the incoming
   *     activity result.
   * @param data Intent (containing result data) returned by incoming
   *     activity result.
   */
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    switch(requestCode) {
      case REQUEST_GOOGLE_PLAY_SERVICES:
        if (resultCode != RESULT_OK) {
          mOutputText.setText(
                  "This app requires Google Play Services. Please install " +
                          "Google Play Services on your device and relaunch this app.");
        } else {
          getResultsFromApi();
        }
        break;
      case REQUEST_ACCOUNT_PICKER:
        if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
          String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
          if (accountName != null) {
            SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(PREF_ACCOUNT_NAME, accountName);
            editor.apply();
            mCredential.setSelectedAccountName(accountName);
            getResultsFromApi();
          }
        }
        break;
      case REQUEST_AUTHORIZATION:
        if (resultCode == RESULT_OK) {
          getResultsFromApi();
        }
        break;
    }
  }









}