package com.example.smartpcbuilder;

import static org.junit.Assert.*;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.test.core.app.ApplicationProvider;

import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import org.junit.runner.RunWith;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30, manifest = Config.NONE)
public class HomeActivityTest {

    private HomeActivity homeActivity;
    private LoginActivity loginActivity;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        FirebaseApp.initializeApp(context);
        homeActivity = Robolectric.buildActivity(HomeActivity.class).create().get();
    }



    @Test
    public void testAddStorageToConfiguration() {
        homeActivity.editTextMaxPrice = new EditText(ApplicationProvider.getApplicationContext());
        homeActivity.editTextMaxPrice.setText("1200");
        homeActivity.totalPrice = 0.0;

        homeActivity.addStorageToConfiguration(1200);

        double expectedStoragePrice = 101.0;
        assertEquals(expectedStoragePrice, homeActivity.totalPrice, 0.01);
        assertEquals("Solid State Drive (SSD) Samsung 980 PRO Gen.4, 1TB, NVMe, M.2.", homeActivity.textViewStorageRecommendation.getText().toString());
    }

    @Test
    public void testShowCaseRecommendation() {
        homeActivity.motherboardFormat = "ATX";
        homeActivity.showCaseRecommendation();

        assertEquals("Corsair 3000D Airflow Black", homeActivity.textViewCaseRecommendation.getText().toString());
        assertEquals("Price : 78.00 $", homeActivity.textViewCasePrice.getText().toString());
    }



    @Test
    public void testShowCoolerRecommendation() {
        homeActivity.selectedUsage = "Gaming";
        homeActivity.processorSocket = "AM4";
        homeActivity.totalPrice = 0.0;

        homeActivity.showCoolerRecommendation();

        assertEquals("Cooler CPU be quiet! Dark Rock 4 Socket AM4", homeActivity.textViewCoolerRecommendation.getText().toString());
        assertTrue(homeActivity.totalPrice > 0);
    }
}
