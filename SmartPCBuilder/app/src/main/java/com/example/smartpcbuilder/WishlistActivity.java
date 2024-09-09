package com.example.smartpcbuilder;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private ListView listViewWishlist;
    private List<String> configurationsList;
    private DatabaseReference databaseRef;
    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        listViewWishlist = findViewById(R.id.listViewWishlist);
        configurationsList = new ArrayList<>();
        databaseRef = FirebaseDatabase.getInstance().getReference("wishlist");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        listViewWishlist = findViewById(R.id.listViewWishlist);





        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(WishlistActivity.this, HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(WishlistActivity.this, ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });

        displayWishlist();
    }

    private void displayWishlist() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = databaseRef.child(userId);
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    configurationsList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String configurationName = snapshot.getKey();
                        configurationsList.add(configurationName);
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(WishlistActivity.this, android.R.layout.simple_list_item_1, configurationsList);
                    listViewWishlist.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(WishlistActivity.this, "Failed to load wishlist: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            listViewWishlist.setOnItemClickListener((parent, view, position, id) -> {
                String configurationName = configurationsList.get(position);
                DatabaseReference configurationRef = databaseRef.child(userId).child(configurationName);

                configurationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String configurationName = configurationsList.get(position);

                        String processorRecommendation = dataSnapshot.child("Processor").child("Recommendation").getValue(String.class);
                        String processorPrice = dataSnapshot.child("Processor").child("Price").getValue(String.class);

                        String coolerRecommendation = dataSnapshot.child("Cooler").child("Recommendation").getValue(String.class);
                        String coolerPrice = dataSnapshot.child("Cooler").child("Price").getValue(String.class);

                        String graphicsCardRecommendation = dataSnapshot.child("GraphicsCard").child("Recommendation").getValue(String.class);
                        String graphicsCardPrice = dataSnapshot.child("GraphicsCard").child("Price").getValue(String.class);

                        String motherboardRecommendation = dataSnapshot.child("Motherboard").child("Recommendation").getValue(String.class);
                        String motherboardPrice = dataSnapshot.child("Motherboard").child("Price").getValue(String.class);

                        String ramRecommendation = dataSnapshot.child("RAM").child("Recommendation").getValue(String.class);
                        String ramPrice = dataSnapshot.child("RAM").child("Price").getValue(String.class);

                        String storageRecommendation = dataSnapshot.child("Storage").child("Recommendation").getValue(String.class);
                        String storagePrice = dataSnapshot.child("Storage").child("Price").getValue(String.class);

                        String powerSupplyRecommendation = dataSnapshot.child("PowerSupply").child("Recommendation").getValue(String.class);
                        String powerSupplyPrice = dataSnapshot.child("PowerSupply").child("Price").getValue(String.class);

                        String caseRecommendation = dataSnapshot.child("Case").child("Recommendation").getValue(String.class);
                        String casePrice = dataSnapshot.child("Case").child("Price").getValue(String.class);

                        String monitorRecommendation = dataSnapshot.child("Monitor").child("Recommendation").getValue(String.class);
                        String monitorPrice = dataSnapshot.child("Monitor").child("Price").getValue(String.class);

                        String total = dataSnapshot.child("Total").getValue(String.class);

                        if (graphicsCardRecommendation != null && !graphicsCardRecommendation.isEmpty()) {
                            showPopup1(userId, configurationName,
                                    processorRecommendation, processorPrice,
                                    coolerRecommendation, coolerPrice,
                                    graphicsCardRecommendation, graphicsCardPrice,
                                    motherboardRecommendation, motherboardPrice,
                                    ramRecommendation, ramPrice,
                                    storageRecommendation, storagePrice,
                                    powerSupplyRecommendation, powerSupplyPrice,
                                    caseRecommendation, casePrice,
                                    monitorRecommendation, monitorPrice,total);
                        } else {
                            showPopup2(userId, configurationName,
                                    processorRecommendation, processorPrice,
                                    motherboardRecommendation, motherboardPrice,
                                    ramRecommendation, ramPrice,
                                    storageRecommendation, storagePrice,
                                    powerSupplyRecommendation, powerSupplyPrice,
                                    caseRecommendation, casePrice,
                                    monitorRecommendation, monitorPrice,total);
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(WishlistActivity.this, "Error reading from FB:" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }
    }

    private void showPopup1(String userId, String configurationName, String processorRecommendation, String processorPrice, String coolerRecommendation, String coolerPrice, String graphicsCardRecommendation, String graphicsCardPrice, String motherboardRecommendation, String motherboardPrice, String ramRecommendation, String ramPrice, String storageRecommendation, String storagePrice, String powerSupplyRecommendation, String powerSupplyPrice, String caseRecommendation, String casePrice, String monitorRecommendation, String monitorPrice, String total) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_configuration, null);


        TextView textViewPopUpTitle = popupView.findViewById(R.id.textViewPopUpTitle);
        TextView textViewMonitor = popupView.findViewById(R.id.textViewMonitor);
        textViewMonitor.setVisibility(View.GONE);
        LinearLayout layoutMonitor = popupView.findViewById(R.id.layoutMonitor);
        layoutMonitor.setVisibility(View.GONE);

        TextView processorRecommendationTextView = popupView.findViewById(R.id.textViewProcessorRecommendation);
        TextView processorPriceTextView = popupView.findViewById(R.id.textViewProcessorPrice);
        ImageButton buttonSearchProcessor = popupView.findViewById(R.id.buttonSearchProcessor);
        buttonSearchProcessor.setOnClickListener(view -> {
            searchOnGoogle(processorRecommendation);
        });

        TextView coolerRecommendationTextView = popupView.findViewById(R.id.textViewCoolerRecommendation);
        TextView coolerPriceTextView = popupView.findViewById(R.id.textViewCoolerPrice);
        ImageButton buttonSearchCooler = popupView.findViewById(R.id.buttonSearchCooler);
        buttonSearchCooler.setOnClickListener(view -> {
            searchOnGoogle(coolerRecommendation);
        });

        TextView graphicsCardRecommendationTextView = popupView.findViewById(R.id.textViewGraphicsCardRecommendation);
        TextView graphicsCardPriceTextView = popupView.findViewById(R.id.textViewGraphicsCardPrice);
        ImageButton buttonSearchGraphicsCard = popupView.findViewById(R.id.buttonSearchGraphicsCard);
        buttonSearchGraphicsCard.setOnClickListener(view -> {
            searchOnGoogle(graphicsCardRecommendation);
        });

        TextView motherboardRecommendationTextView = popupView.findViewById(R.id.textViewMotherboardRecommendation);
        TextView motherboardPriceTextView = popupView.findViewById(R.id.textViewMotherboardPrice);
        ImageButton buttonSearchMotherboard = popupView.findViewById(R.id.buttonSearchMotherboard);
        buttonSearchMotherboard.setOnClickListener(view -> {
            searchOnGoogle(motherboardRecommendation);
        });

        TextView ramRecommendationTextView = popupView.findViewById(R.id.textViewRAMRecommendation);
        TextView ramPriceTextView = popupView.findViewById(R.id.textViewRAMPrice);
        ImageButton buttonSearchRAM = popupView.findViewById(R.id.buttonSearchRAM);
        buttonSearchRAM.setOnClickListener(view -> {
            searchOnGoogle(ramRecommendation);
        });

        TextView storageRecommendationTextView = popupView.findViewById(R.id.textViewStorageRecommendation);
        TextView storagePriceTextView = popupView.findViewById(R.id.textViewStoragePrice);
        ImageButton buttonSearchStorage = popupView.findViewById(R.id.buttonSearchStorage);
        buttonSearchStorage.setOnClickListener(view -> {
            searchOnGoogle(storageRecommendation);
        });

        TextView powerSupplyRecommendationTextView = popupView.findViewById(R.id.textViewPowerSupplyRecommendation);
        TextView powerSupplyPriceTextView = popupView.findViewById(R.id.textViewPowerSupplyPrice);
        ImageButton buttonSearchPowerSupply = popupView.findViewById(R.id.buttonSearchPowerSupply);
        buttonSearchPowerSupply.setOnClickListener(view -> {
            searchOnGoogle(powerSupplyRecommendation);
        });

        TextView caseRecommendationTextView = popupView.findViewById(R.id.textViewCaseRecommendation);
        TextView casePriceTextView = popupView.findViewById(R.id.textViewCasePrice);
        ImageButton buttonSearchCase = popupView.findViewById(R.id.buttonSearchCase);
        buttonSearchCase.setOnClickListener(view -> {
            searchOnGoogle(caseRecommendation);
        });

        if (monitorRecommendation != null && !monitorRecommendation.isEmpty()) {
            Log.e(TAG, "A intrat in if");
            TextView monitorRecommendationTextView = popupView.findViewById(R.id.textViewMonitorRecommendation);
            TextView monitorPriceTextView = popupView.findViewById(R.id.textViewMonitorPrice);
            ImageButton buttonSearchMonitor = popupView.findViewById(R.id.buttonSearchMonitor);
            textViewMonitor.setVisibility(View.VISIBLE);
            layoutMonitor.setVisibility(View.VISIBLE);

            monitorRecommendationTextView.setText(monitorRecommendation);
            monitorPriceTextView.setText(monitorPrice);
            buttonSearchMonitor.setOnClickListener(view -> {
                searchOnGoogle(monitorRecommendation);
            });
        } else {
            Log.e(TAG, "A intrat pe else");
            textViewMonitor.setVisibility(View.GONE);
            layoutMonitor.setVisibility(View.GONE);
        }



        TextView textViewTotal = popupView.findViewById(R.id.textViewTotal);
        Button saveToWhishlist =popupView.findViewById(R.id.saveButton);
        saveToWhishlist.setVisibility(View.GONE);

        processorRecommendationTextView.setText(processorRecommendation);
        processorPriceTextView.setText(processorPrice);

        coolerRecommendationTextView.setText(coolerRecommendation);
        coolerPriceTextView.setText(coolerPrice);

        graphicsCardRecommendationTextView.setText(graphicsCardRecommendation);
        graphicsCardPriceTextView.setText(graphicsCardPrice);

        motherboardRecommendationTextView.setText(motherboardRecommendation);
        motherboardPriceTextView.setText(motherboardPrice);

        ramRecommendationTextView.setText(ramRecommendation);
        ramPriceTextView.setText(ramPrice);

        storageRecommendationTextView.setText(storageRecommendation);
        storagePriceTextView.setText(storagePrice);

        powerSupplyRecommendationTextView.setText(powerSupplyRecommendation);
        powerSupplyPriceTextView.setText(powerSupplyPrice);

        caseRecommendationTextView.setText(caseRecommendation);
        casePriceTextView.setText(casePrice);

        textViewTotal.setText(total);

        textViewPopUpTitle.setText("❤\uFE0F   " + configurationName);



        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        Button closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> popupWindow.dismiss());

        Button deleteButton = popupView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(WishlistActivity.this);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this configuration?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                DatabaseReference configurationRef = databaseRef.child(userId).child(configurationName);

                configurationRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WishlistActivity.this, "Wishlist item deleted", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    } else {
                        Toast.makeText(WishlistActivity.this, "Failed to delete wishlist item", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        });


        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    private void showPopup2(String userId, String configurationName, String processorRecommendation, String processorPrice, String motherboardRecommendation, String motherboardPrice, String ramRecommendation, String ramPrice, String storageRecommendation, String storagePrice, String powerSupplyRecommendation, String powerSupplyPrice, String caseRecommendation, String casePrice, String monitorRecommendation, String monitorPrice, String total) {
        View popupView = LayoutInflater.from(this).inflate(R.layout.popup_configuration, null);

        Log.d(TAG, "Apelat fara placa video");

        TextView textViewPopUpTitle = popupView.findViewById(R.id.textViewPopUpTitle);
        TextView textViewMonitor = popupView.findViewById(R.id.textViewMonitor);
        textViewMonitor.setVisibility(View.GONE);
        LinearLayout layoutMonitor = popupView.findViewById(R.id.layoutMonitor);
        layoutMonitor.setVisibility(View.GONE);

        TextView processorRecommendationTextView = popupView.findViewById(R.id.textViewProcessorRecommendation);
        TextView processorPriceTextView = popupView.findViewById(R.id.textViewProcessorPrice);
        ImageButton buttonSearchProcessor = popupView.findViewById(R.id.buttonSearchProcessor);
        buttonSearchProcessor.setOnClickListener(view -> {
            searchOnGoogle(processorRecommendation);
        });

        TextView textViewCoolerRecommendation = popupView.findViewById(R.id.textViewCoolerRecommendation);
        TextView textViewCooler = popupView.findViewById(R.id.textViewCooler);
        LinearLayout layoutCooler = popupView.findViewById(R.id.layoutCooler);
        TextView textViewCoolerPrice = popupView.findViewById(R.id.textViewCoolerPrice);
        TextView textViewSpaceCooler = popupView.findViewById(R.id.textViewSpaceCooler);
        textViewCoolerRecommendation.setVisibility(View.GONE);
        textViewCooler.setVisibility(View.GONE);
        layoutCooler.setVisibility(View.GONE);
        textViewCoolerPrice.setVisibility(View.GONE);
        textViewSpaceCooler.setVisibility(View.GONE);

        TextView textViewGraphicsCard = popupView.findViewById(R.id.textViewGraphicsCard);
        LinearLayout layoutGraphicsCard = popupView.findViewById(R.id.layoutGraphicsCard);
        TextView textViewGraphicsCardRecommendation = popupView.findViewById(R.id.textViewGraphicsCardRecommendation);
        TextView textViewGraphicsCardPrice = popupView.findViewById(R.id.textViewGraphicsCardPrice);
        TextView textViewSpaceGraphicsCard=popupView.findViewById(R.id.textViewSpaceGraphicsCard);
        textViewGraphicsCard.setVisibility(View.GONE);
        layoutGraphicsCard.setVisibility(View.GONE);
        textViewGraphicsCardRecommendation.setVisibility(View.GONE);
        textViewGraphicsCardPrice.setVisibility(View.GONE);
        textViewSpaceGraphicsCard.setVisibility(View.GONE);

        TextView motherboardRecommendationTextView = popupView.findViewById(R.id.textViewMotherboardRecommendation);
        TextView motherboardPriceTextView = popupView.findViewById(R.id.textViewMotherboardPrice);
        ImageButton buttonSearchMotherboard = popupView.findViewById(R.id.buttonSearchMotherboard);
        buttonSearchMotherboard.setOnClickListener(view -> {
            searchOnGoogle(motherboardRecommendation);
        });

        TextView ramRecommendationTextView = popupView.findViewById(R.id.textViewRAMRecommendation);
        TextView ramPriceTextView = popupView.findViewById(R.id.textViewRAMPrice);
        ImageButton buttonSearchRAM = popupView.findViewById(R.id.buttonSearchRAM);
        buttonSearchRAM.setOnClickListener(view -> {
            searchOnGoogle(ramRecommendation);
        });

        TextView storageRecommendationTextView = popupView.findViewById(R.id.textViewStorageRecommendation);
        TextView storagePriceTextView = popupView.findViewById(R.id.textViewStoragePrice);
        ImageButton buttonSearchStorage = popupView.findViewById(R.id.buttonSearchStorage);
        buttonSearchStorage.setOnClickListener(view -> {
            searchOnGoogle(storageRecommendation);
        });

        TextView powerSupplyRecommendationTextView = popupView.findViewById(R.id.textViewPowerSupplyRecommendation);
        TextView powerSupplyPriceTextView = popupView.findViewById(R.id.textViewPowerSupplyPrice);
        ImageButton buttonSearchPowerSupply = popupView.findViewById(R.id.buttonSearchPowerSupply);
        buttonSearchPowerSupply.setOnClickListener(view -> {
            searchOnGoogle(powerSupplyRecommendation);
        });

        TextView caseRecommendationTextView = popupView.findViewById(R.id.textViewCaseRecommendation);
        TextView casePriceTextView = popupView.findViewById(R.id.textViewCasePrice);
        ImageButton buttonSearchCase = popupView.findViewById(R.id.buttonSearchCase);
        buttonSearchCase.setOnClickListener(view -> {
            searchOnGoogle(caseRecommendation);
        });

        if (monitorRecommendation != null && !monitorRecommendation.isEmpty()) {
            TextView monitorRecommendationTextView = popupView.findViewById(R.id.textViewMonitorRecommendation);
            TextView monitorPriceTextView = popupView.findViewById(R.id.textViewMonitorPrice);
            ImageButton buttonSearchMonitor = popupView.findViewById(R.id.buttonSearchMonitor);
            textViewMonitor.setVisibility(View.VISIBLE);
            layoutMonitor.setVisibility(View.VISIBLE);
            monitorRecommendationTextView.setText(monitorRecommendation);
            monitorPriceTextView.setText(monitorPrice);
            buttonSearchMonitor.setOnClickListener(view -> {
                searchOnGoogle(monitorRecommendation);
            });
        } else {
            textViewMonitor = popupView.findViewById(R.id.textViewMonitor);
            textViewMonitor.setVisibility(View.GONE);
            layoutMonitor = popupView.findViewById(R.id.layoutMonitor);
            layoutMonitor.setVisibility(View.GONE);
        }

        TextView textViewTotal = popupView.findViewById(R.id.textViewTotal);
        Button saveToWhishlist =popupView.findViewById(R.id.saveButton);
        saveToWhishlist.setVisibility(View.GONE);

        processorRecommendationTextView.setText(processorRecommendation);
        processorPriceTextView.setText(processorPrice);


        motherboardRecommendationTextView.setText(motherboardRecommendation);
        motherboardPriceTextView.setText(motherboardPrice);

        ramRecommendationTextView.setText(ramRecommendation);
        ramPriceTextView.setText(ramPrice);

        storageRecommendationTextView.setText(storageRecommendation);
        storagePriceTextView.setText(storagePrice);

        powerSupplyRecommendationTextView.setText(powerSupplyRecommendation);
        powerSupplyPriceTextView.setText(powerSupplyPrice);

        caseRecommendationTextView.setText(caseRecommendation);
        casePriceTextView.setText(casePrice);

        textViewTotal.setText(total);
        textViewPopUpTitle.setText("❤\uFE0F   " + configurationName);




        PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        Button closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(v -> popupWindow.dismiss());

        Button deleteButton = popupView.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(WishlistActivity.this);
            builder.setTitle("Confirm Delete");
            builder.setMessage("Are you sure you want to delete this configuration?");

            builder.setPositiveButton("Yes", (dialog, which) -> {
                DatabaseReference configurationRef = databaseRef.child(userId).child(configurationName);

                configurationRef.removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(WishlistActivity.this, "Wishlist item deleted", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                    } else {
                        Toast.makeText(WishlistActivity.this, "Failed to delete wishlist item", Toast.LENGTH_SHORT).show();
                    }
                });
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.show();
        });


        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }


    private void searchOnGoogle(String searchTerm) {
        String searchUrl = GOOGLE_SEARCH_URL + searchTerm.replace(" ", "+");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(searchUrl));
        startActivity(intent);
    }
}