package com.example.smartpcbuilder;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.common.SignInButton;


public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout textInputLayoutEmail,textInputLayoutPassword, textInputLayoutConfirmPassword;
    private EditText editTextRegisterEmail, editTextRegisterPassword, editTextConfirmPassword;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private DatabaseReference mDatabase;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextView textViewRegisterHead = findViewById(R.id.textView_register_head);
        TextView textViewOr = findViewById(R.id.textView_or);
        editTextRegisterEmail = findViewById(R.id.editText_register_email);
        editTextRegisterPassword = findViewById(R.id.editText_register_password);
        editTextConfirmPassword = findViewById(R.id.editText_confirm_password);
        progressBar = findViewById(R.id.progressBar);
        authProfile = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        SignInButton googleRegisterButton = findViewById(R.id.google_button);
        Button buttonRegister = findViewById(R.id.button_register);
        textInputLayoutEmail = findViewById(R.id.textInputLayout_email);
        textInputLayoutPassword = findViewById(R.id.textInputLayout_password);
        textInputLayoutConfirmPassword = findViewById(R.id.textInputLayout_confirm_password);

        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        editTextRegisterEmail.startAnimation(slideUp);
        editTextRegisterPassword.startAnimation(slideUp);
        editTextConfirmPassword.startAnimation(slideUp);
        googleRegisterButton.startAnimation(slideUp);
        buttonRegister.startAnimation(slideUp);
        textViewRegisterHead.startAnimation(slideUp);
        textViewOr.startAnimation(slideUp);
        textInputLayoutEmail.startAnimation(slideUp);
        textInputLayoutPassword.startAnimation(slideUp);
        textInputLayoutConfirmPassword.startAnimation(slideUp);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextRegisterEmail.getText().toString().trim();
                String password = editTextRegisterPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextRegisterEmail.setError("Please enter a valid email address");
                    editTextRegisterEmail.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextRegisterPassword.setError("Please enter a password");
                    editTextRegisterPassword.requestFocus();
                    return;
                }

                if (password.length() < 8) {
                    editTextRegisterPassword.setError("Password should be at least 8 characters long");
                    editTextRegisterPassword.requestFocus();
                    return;
                }

                if (password.contains(";") || password.contains("'") || password.contains("-")) {
                    editTextRegisterPassword.setError("You cannot use ; or ' or -");
                    editTextRegisterPassword.requestFocus();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    editTextConfirmPassword.setError("Passwords do not match");
                    editTextConfirmPassword.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                registerUser(email, password);
            }
        });


        googleRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void registerUser(String email, String password) {
        authProfile.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);

                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = authProfile.getCurrentUser();
                            if (currentUser != null) {
                                currentUser.sendEmailVerification()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(RegisterActivity.this, "Email verification sent to " + email,
                                                            Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                    finish();
                                                } else {
                                                    Toast.makeText(RegisterActivity.this, "Failed to send email verification",
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterActivity.this, "Email address is already in use",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Registration failed",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
    private void checkEmailVerificationStatus(FirebaseUser user) {
        if (user.isEmailVerified()) {
            Toast.makeText(RegisterActivity.this, "Email verified. You can now log in.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "Please verify your email before logging in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(task);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            progressBar.setVisibility(View.GONE);
            Log.w("GoogleSignIn", "Google sign in failed", e);
            Toast.makeText(RegisterActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        authProfile.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Google sign in successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                            finish();
                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(RegisterActivity.this, "Google sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
