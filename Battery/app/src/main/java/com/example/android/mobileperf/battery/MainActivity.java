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
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.android.mobileperf.battery.databinding.ActivityMainBinding;

/**
 * Just a "Table of Contents" Activity to springboard you into the various exercises.  Seriously,
 * there is NOTHING interesting here.  Why are you still reading?  Why didn't you learn in
 * Lesson 2??? We expected better.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        addButton(WaitForPowerActivity.class, "Battery-heavy is no good", binding.mainRootview);
        addButton(FreeTheWakelockActivity.class, "Release the wakelock!", binding.mainRootview);
    }

    public void addButton(final Class<?> destination, String description, ViewGroup parent) {
        Button button = new Button(this);
        button.setOnClickListener(v -> {
            Intent problemIntent = new Intent(MainActivity.this, destination);
            startActivity(problemIntent);
        });

        button.setText(description);
        parent.addView(button);
    }
}
