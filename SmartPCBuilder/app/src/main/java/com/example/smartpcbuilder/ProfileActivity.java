package com.example.smartpcbuilder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();

        TextView textViewHelloUser = findViewById(R.id.textViewHelloUser);
        Button buttonChangePassword = findViewById(R.id.buttonChangePassword);
        Button buttonLogout = findViewById(R.id.buttonLogout);


        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String username = currentUser.getEmail();
            textViewHelloUser.setText("\uD83D\uDC4B Hello, PC enthusiast!\uD83D\uDE01 \nYour e-mail is:\n " + username);
        }

        buttonChangePassword.setOnClickListener(v -> changePassword());


        buttonLogout.setOnClickListener(v -> logout());

        CardView cardViewContact = findViewById(R.id.cardViewContact);
        CardView cardViewYouTube = findViewById(R.id.cardViewYouTube);

        cardViewContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, ContactActivity.class));
            }
        });

        cardViewYouTube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String youtubeLink = "https://www.youtube.com/watch?v=sxbL6hSRf0g";
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink)));
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(ProfileActivity.this, WishlistActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });
    }

    private void changePassword() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        EditText editTextNewPassword = findViewById(R.id.editTextNewPassword);

        if (user != null) {
            String newPassword = editTextNewPassword.getText().toString();

            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }



    private void logout() {
        mAuth.signOut();
        startActivity(new Intent(ProfileActivity.this, MainActivity.class));
        finish();
    }
}
