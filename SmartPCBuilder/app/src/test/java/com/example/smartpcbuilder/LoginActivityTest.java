package com.example.smartpcbuilder;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.mockito.Mockito;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class LoginActivityTest {

    private LoginActivity loginActivity;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        loginActivity = Robolectric.buildActivity(LoginActivity.class).create().get();
    }

    @Test
    public void testValidateInput_ValidInput() {
        String validEmail = "test@example.com";
        String validPassword = "password123";

        boolean result = loginActivity.validateInput(validEmail, validPassword);

        assertTrue(result);
    }

    @Test
    public void testValidateInput_InvalidEmail() {
        String invalidEmail = "invalid-email";
        String validPassword = "password123";

        boolean result = loginActivity.validateInput(invalidEmail, validPassword);

        assertFalse(result);
    }

    @Test
    public void testValidateInput_InvalidPassword() {
        String validEmail = "test@example.com";
        String invalidPassword = "pass;";

        boolean result = loginActivity.validateInput(validEmail, invalidPassword);

        assertFalse(result);
    }
}
