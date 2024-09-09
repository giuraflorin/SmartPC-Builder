package com.example.smartpcbuilder;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ContactActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(ContactActivity.this, HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(ContactActivity.this, WishlistActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;

            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(ContactActivity.this, ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });

        TextView textViewContactEmail = findViewById(R.id.textViewContactEmail);
        textViewContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailAddress = "pcbuildersupp@gmail.com";
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + emailAddress));

                try {
                    startActivity(Intent.createChooser(emailIntent, "Send Email"));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ContactActivity.this, "No email app found", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView textViewContactPhone = findViewById(R.id.textViewContactPhone);
        textViewContactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = "+40770945654";
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));

                try {
                    startActivity(callIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ContactActivity.this, "No phone app found", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}

