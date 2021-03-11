/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.mobileperf.battery;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.mobileperf.battery.databinding.ActivityPowerBinding;


public class WaitForPowerActivity extends AppCompatActivity {

    public static final String LOG_TAG = "WaitForPowerActivity";
    private ActivityPowerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPowerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.powerTakePhoto.setText(R.string.take_photo_button);

        binding.powerApplyFilter.setText(R.string.filter_photo_button);

        binding.powerTakePhoto.setOnClickListener(v -> {
            takePhoto();
            // After we take the photo, we should display the filter option.
            binding.powerApplyFilter.setVisibility(View.VISIBLE);
        });

        binding.powerApplyFilter.setOnClickListener(v -> applyFilter());
    }

    /**
     * These are placeholder methods for where your app might do something interesting! Try not to
     * confuse them with functional code.
     *
     * In this case, we are showing how your app might want to manipulate a photo a user has
     * uploaded--perhaps by performing facial detection, applying filters, generating thumbnails,
     * or backing up the image. In many instances, these actions might not be immediately necessary,
     * and may even be better done in batch. In this sample, we allow the user to "take" a photo,
     * and then "apply" a simple magenta filter to the photo. For brevity, the photos are already
     * included in the sample.
     */
    private void takePhoto() {
        // Make photo of Cheyenne appear.
        binding.cheyenneTxt.setText(R.string.photo_taken);
        binding.cheyenneImg.setImageResource(R.drawable.cheyenne);
    }

    private void applyFilter() {
        // If not plugged in, wait to apply the filter.
        boolean isPow = checkForPower();
        Log.i(LOG_TAG, "checkForPower = " + isPow);
        if (isPow) {
            binding.cheyenneImg.setImageResource(R.drawable.pink_cheyenne);
            binding.cheyenneTxt.setText(R.string.photo_filter);
        } else binding.cheyenneTxt.setText(R.string.waiting_for_power);
    }

    /**
     * This method checks for power by comparing the current battery state against all possible
     * plugged in states. In this case, a device may be considered plugged in either by USB, AC, or
     * wireless charge. (Wireless charge was introduced in API Level 17.)
     */
    private boolean checkForPower() {
        // It is very easy to subscribe to changes to the battery state, but you can get the current
        // state by simply passing null in as your receiver.  Nifty, isn't that?
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, filter);

        // There are currently three ways a device can be plugged in. We should check them all.
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
        boolean acCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);
        boolean wirelessCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_WIRELESS);
        return (usbCharge || acCharge || wirelessCharge);
    }
}
