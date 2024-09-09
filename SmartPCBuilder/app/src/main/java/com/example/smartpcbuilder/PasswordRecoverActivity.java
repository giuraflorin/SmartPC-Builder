package com.example.smartpcbuilder;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordRecoverActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutEmail;
    private EditText editTextRecoverEmail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recover);

        editTextRecoverEmail = findViewById(R.id.editText_recover_email);
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();
        TextView textViewRecoverHead = findViewById(R.id.textView_recover_head);
        textInputLayoutEmail = findViewById(R.id.textInputLayout_recover_email);

        Button buttonRecoverPassword = findViewById(R.id.button_recover_password);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        editTextRecoverEmail.startAnimation(slideUp);
        textViewRecoverHead.startAnimation(slideUp);
        textInputLayoutEmail.startAnimation(slideUp);
        buttonRecoverPassword.startAnimation(slideUp);

        buttonRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextRecoverEmail.getText().toString().trim();

                if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextRecoverEmail.setError("Please enter a valid email address");
                    editTextRecoverEmail.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                sendPasswordResetEmail(email);
            }
        });
    }

    private void sendPasswordResetEmail(String email) {
        authProfile.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            Toast.makeText(PasswordRecoverActivity.this, "Password reset email sent to " + email,
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(PasswordRecoverActivity.this, "Failed to send password reset email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
