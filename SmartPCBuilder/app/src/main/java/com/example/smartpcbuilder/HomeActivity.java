package com.example.smartpcbuilder;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    public int roundedTotalPower;
    private TextView textViewProcessor;
    private TextView textViewMonitor;
    private TextView textViewCase;
    private TextView textViewGettingReady;
    TextView textViewCasePrice;
    private TextView textViewPowerSupplyRecommendation;
    private TextView textViewPowerSupplyPrice;
    private TextView textViewStoragePrice;
    TextView textViewStorageRecommendation;
    private TextView textViewRAMRecommendation;
    private TextView textViewRAMPrice;
    private TextView textViewMotherboardRecommendation;
    private TextView textViewMotherboardPrice;
    private TextView textViewSpaceGraphicsCard;
    private TextView textViewCooler;
    private TextView textViewGraphicsCardRecommendation;
    private TextView textViewGraphicsCardPrice;
    private TextView textViewProcessorPrice;
    private TextView textViewCoolerPrice;
    private TextView textViewProcessorRecommendation;
    private TextView textViewStorage;
    TextView textViewCaseRecommendation;
    private TextView textViewGraphicsCard;
    private TextView textViewMotherboard;
    TextView textViewTotal;
    private TextView textViewRAM;
    private TextView textViewPowerSupply;
    TextView textViewCoolerRecommendation;
    EditText editTextMaxPrice;
    LinearLayout layoutCooler, layoutGraphicsCard, layoutMonitor;
    private Button buttonSubmit, saveButton, deleteButton;
    private FirebaseFirestore db;
    String processorSocket = null;
    private List<String> processorMemoryType, processorMemorySpeed, motherboardMemorySpeed;

    private static final String GOOGLE_SEARCH_URL = "https://www.google.com/search?q=";

    private View popupView;
    private PopupWindow popupWindow;

    Spinner spinnerUsage;
    private Spinner spinnerProcessor;
    String selectedUsage;
    double totalPrice = 0;
    private int asyncTasksCompleted = 0;
    String motherboardFormat = "";
    private ProgressBar progressBar;
    CheckBox checkBoxIncludeMonitor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        editTextMaxPrice = findViewById(R.id.editTextMaxPrice);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        spinnerUsage = findViewById(R.id.spinnerUsage);
        spinnerProcessor = findViewById(R.id.spinnerProcessor);
        checkBoxIncludeMonitor = findViewById(R.id.checkBoxIncludeMonitor);
        textViewGettingReady =findViewById(R.id.textViewGettingReady);
        progressBar = findViewById(R.id.progressBar);
        textViewGettingReady.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        LayoutInflater inflater = LayoutInflater.from(this);
        popupView = inflater.inflate(R.layout.popup_configuration, null);

        textViewProcessor = popupView.findViewById(R.id.textViewProcessor);
        textViewProcessorRecommendation = popupView.findViewById(R.id.textViewProcessorRecommendation);
        textViewProcessorPrice = popupView.findViewById(R.id.textViewProcessorPrice);

        textViewCoolerRecommendation = popupView.findViewById(R.id.textViewCoolerRecommendation);
        textViewCooler = popupView.findViewById(R.id.textViewCooler);
        layoutCooler = popupView.findViewById(R.id.layoutCooler);
        textViewCoolerPrice = popupView.findViewById(R.id.textViewCoolerPrice);
        textViewCooler.setVisibility(View.GONE);
        layoutCooler.setVisibility(View.GONE);

        textViewGraphicsCard = popupView.findViewById(R.id.textViewGraphicsCard);
        layoutGraphicsCard = popupView.findViewById(R.id.layoutGraphicsCard);
        textViewGraphicsCardRecommendation = popupView.findViewById(R.id.textViewGraphicsCardRecommendation);
        textViewGraphicsCardPrice = popupView.findViewById(R.id.textViewGraphicsCardPrice);
        textViewSpaceGraphicsCard=popupView.findViewById(R.id.textViewSpaceGraphicsCard);
        textViewGraphicsCard.setVisibility(View.GONE);
        layoutGraphicsCard.setVisibility(View.GONE);

        textViewMotherboard = popupView.findViewById(R.id.textViewMotherboard);
        textViewMotherboardRecommendation = popupView.findViewById(R.id.textViewMotherboardRecommendation);
        textViewMotherboardPrice = popupView.findViewById(R.id.textViewMotherboardPrice);

        textViewRAMRecommendation = popupView.findViewById(R.id.textViewRAMRecommendation);
        textViewRAMPrice = popupView.findViewById(R.id.textViewRAMPrice);
        textViewRAM = popupView.findViewById(R.id.textViewRAM);

        textViewStorage = popupView.findViewById(R.id.textViewStorage);
        textViewStoragePrice = popupView.findViewById(R.id.textViewStoragePrice);
        textViewStorageRecommendation = popupView.findViewById(R.id.textViewStorageRecommendation);

        textViewPowerSupply = popupView.findViewById(R.id.textViewPowerSupply);
        textViewPowerSupplyRecommendation = popupView.findViewById(R.id.textViewPowerSupplyRecommendation);
        textViewPowerSupplyPrice = popupView.findViewById(R.id.textViewPowerSupplyPrice);

        textViewCase = popupView.findViewById(R.id.textViewCase);
        textViewCaseRecommendation = popupView.findViewById(R.id.textViewCaseRecommendation);
        textViewCasePrice = popupView.findViewById(R.id.textViewCasePrice);

        textViewTotal = popupView.findViewById(R.id.textViewTotal);
        saveButton = popupView.findViewById(R.id.saveButton);
        deleteButton = popupView.findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.GONE);

        textViewMonitor= popupView.findViewById(R.id.textViewMonitor);
        layoutMonitor= popupView.findViewById(R.id.layoutMonitor);




        db = FirebaseFirestore.getInstance();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.usage_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUsage.setAdapter(adapter);

        spinnerUsage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedUsage = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_dashboard) {
                startActivity(new Intent(HomeActivity.this, WishlistActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            } else if (itemId == R.id.nav_notifications) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                return true;
            }
            return false;
        });

        saveButton.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Enter configuration name");

            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("Save", (dialog, which) -> {
                String configurationName = input.getText().toString();
                saveConfigurationToFirebase(configurationName);
                Toast.makeText(this, "Configuration saved to wishlist", Toast.LENGTH_SHORT).show();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        });





        buttonSubmit.setOnClickListener(view -> {
            if (editTextMaxPrice.getText().toString().isEmpty()) {
                editTextMaxPrice.setError("Please enter a budget");
                return;
            }
            String maxPriceText = editTextMaxPrice.getText().toString();

            if (maxPriceText.isEmpty()) {
                editTextMaxPrice.setError("Please enter a budget");
                return;
            }

            double maxPrice = Double.parseDouble(maxPriceText);

            String selectedUsage = spinnerUsage.getSelectedItem().toString();

            if ((selectedUsage.equals("Office") || selectedUsage.equals("Programming")) && maxPrice < 499) {
                editTextMaxPrice.setError("For a good experience, we recommend staying above $499 on "+selectedUsage +". Otherwise, it may not be worth purchasing PC parts separately.");
                return;
            }

            if ((selectedUsage.equals("Gaming") || selectedUsage.equals("Graphic Design") || selectedUsage.equals("Other")) && maxPrice < 799) {
                editTextMaxPrice.setError("For a good experience, we recommend staying above $799 on "+selectedUsage +". Otherwise, it may not be worth purchasing PC parts separately.");
                return;
            }


            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            textViewGettingReady.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);

            new Handler().post(new Runnable() {
                int count = 0;

                @Override
                public void run() {
                    switch (count % 3) {
                        case 0:
                            textViewGettingReady.setText("Getting ready your configuration.");
                            break;
                        case 1:
                            textViewGettingReady.setText("Getting ready your configuration..");
                            break;
                        case 2:
                            textViewGettingReady.setText("Getting ready your configuration...");
                            break;
                    }
                    count++;

                    new Handler().postDelayed(this, 500);
                }
            });

            new Handler().postDelayed(() -> {
                textViewGettingReady.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);

                showRecommendation();
            }, 3000);
        });




    }


    private void showRecommendation() {
        totalPrice = 0;
        asyncTasksCompleted = 0;
        String maxPriceString = editTextMaxPrice.getText().toString();
        double maxPrice = Double.parseDouble(maxPriceString);
        ImageButton buttonSearchGraphicsCard = popupView.findViewById(R.id.buttonSearchGraphicsCard);

        int totalAsyncTasks = 2;
        String selectedProcessorProducer = spinnerProcessor.getSelectedItem().toString();
        Query processorQuery;
        double processorPricePercentage = 0;
        if (maxPrice <= 1500) {
            if ((selectedUsage.equals("Office") || selectedUsage.equals("Programming")) && checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.38;
            } else if ((selectedUsage.equals("Office") || selectedUsage.equals("Programming")) && !checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.43;
            } else if (!checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.23;
            } else {
                processorPricePercentage = 0.18;
            }
        } else if(maxPrice > 1500 && maxPrice < 2500) {
            if ((selectedUsage.equals("Office") || selectedUsage.equals("Programming")) && checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.43;
            } else if ((selectedUsage.equals("Office") || selectedUsage.equals("Programming")) && !checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.50;
            } else if (!checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.30;
            } else {
                processorPricePercentage = 0.23;
            }
        } else {
            if ((selectedUsage.equals("Office") || selectedUsage.equals("Programming")) && checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.48;
            } else if ((selectedUsage.equals("Office") || selectedUsage.equals("Programming")) && !checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.53;
            } else if (!checkBoxIncludeMonitor.isChecked()) {
                processorPricePercentage = 0.35;
            } else {
                processorPricePercentage = 0.28;
            }
        }

        if (selectedProcessorProducer.equals("AMD")) {
            processorQuery = db.collection("PROCESSORS")
                    .whereLessThanOrEqualTo("PRICE", maxPrice * processorPricePercentage)
                    .whereEqualTo("PRODUCER", "AMD")
                    .orderBy("PRICE", Query.Direction.DESCENDING)
                    .limit(1);
        } else {
            processorQuery = db.collection("PROCESSORS")
                    .whereLessThanOrEqualTo("PRICE", maxPrice * processorPricePercentage)
                    .whereEqualTo("PRODUCER", "INTEL")
                    .orderBy("PRICE", Query.Direction.DESCENDING)
                    .limit(1);
        }


        processorQuery.get().addOnSuccessListener(queryDocumentSnapshots -> {
            asyncTasksCompleted++;

            for (DocumentSnapshot processorDocument : queryDocumentSnapshots) {
                String maxProcessorModel = processorDocument.getString("MODEL");
                double maxProcessorPrice = processorDocument.getDouble("PRICE");
                processorSocket = processorDocument.getString("SOCKET");
                double processorPower = processorDocument.getDouble("POWER");
                processorMemoryType = (List<String>) processorDocument.get("MEMORY_TYPE");
                processorMemorySpeed = (List<String>) processorDocument.get("MHZ");

                if (selectedUsage.equals("Office") || selectedUsage.equals("Programming")) {
                    textViewProcessorRecommendation.setText(maxProcessorModel);
                    textViewProcessorPrice.setText("Price : " + String.format("%.2f", maxProcessorPrice) + " $");
                    textViewGraphicsCard.setVisibility(View.GONE);
                    textViewGraphicsCardRecommendation.setVisibility(View.GONE);
                    buttonSearchGraphicsCard.setVisibility(View.GONE);
                    textViewCoolerRecommendation.setVisibility(View.GONE);
                    findMotherboard(processorSocket, processorMemoryType, processorMemorySpeed);
                    calculatePowerSupply(processorPower, 0);
                    addStorageToConfiguration(maxPrice);
                    textViewCooler.setVisibility(View.GONE);
                    layoutCooler.setVisibility(View.GONE);
                    textViewGraphicsCardPrice.setVisibility(View.GONE);
                    textViewSpaceGraphicsCard.setVisibility(View.GONE);
                    textViewMonitor.setVisibility(View.GONE);
                    layoutMonitor.setVisibility(View.GONE);
                    updateMonitorRecommendation();
                    showPopup();

                    if (selectedUsage != null && (selectedUsage.equals("Gaming") || selectedUsage.equals("Graphic Design") || selectedUsage.equals("Other"))) {
                        totalPrice += 75;
                    }

                    ImageButton buttonSearchProcessor = popupView.findViewById(R.id.buttonSearchProcessor);
                    buttonSearchProcessor.setOnClickListener(view -> {
                        if (maxProcessorModel != null) {
                            searchOnGoogle(maxProcessorModel);
                        } else {
                            Toast.makeText(HomeActivity.this, "Modelul procesorului nu este disponibil.", Toast.LENGTH_SHORT).show();
                        }
                    });

                    totalPrice += maxProcessorPrice;
                    textViewTotal.setText("This configuration will cost you approximately: " + String.format("%.2f", totalPrice) + " $");
                } else {
                    double graphicsCardPricePercentage;
                    if (maxPrice <= 1500) {
                        if (!checkBoxIncludeMonitor.isChecked()) {
                            graphicsCardPricePercentage = 0.20;
                        } else {
                            graphicsCardPricePercentage = 0.18;
                        }
                    } else if (maxPrice > 1500 && maxPrice < 2500) {
                        if (!checkBoxIncludeMonitor.isChecked()) {
                            graphicsCardPricePercentage = 0.26;
                        } else {
                            graphicsCardPricePercentage = 0.23;
                        }
                }else{
                        if (!checkBoxIncludeMonitor.isChecked()) {
                            graphicsCardPricePercentage = 0.35;
                        } else {
                            graphicsCardPricePercentage = 0.30;
                        }
                }



                    db.collection("GRAPHICS_CARDS")
                            .whereLessThanOrEqualTo("PRICE", maxPrice * graphicsCardPricePercentage)
                            .orderBy("PRICE", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                asyncTasksCompleted++;

                                for (DocumentSnapshot graphicsCardDocument : queryDocumentSnapshots1) {
                                    String maxGraphicsCardModel = graphicsCardDocument.getString("MODEL");
                                    double maxGraphicsCardPrice = graphicsCardDocument.getDouble("PRICE");
                                    double graphicsCardPower = graphicsCardDocument.getDouble("POWER");

                                    ImageButton buttonSearchProcessor = popupView.findViewById(R.id.buttonSearchProcessor);
                                    buttonSearchProcessor.setOnClickListener(view -> {
                                        if (maxProcessorModel != null) {
                                            searchOnGoogle(maxProcessorModel);
                                        } else {
                                            Toast.makeText(HomeActivity.this, "Modelul procesorului nu este disponibil.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    if (maxProcessorPrice + maxGraphicsCardPrice <= maxPrice) {
                                        textViewProcessorRecommendation.setText(maxProcessorModel);
                                        textViewProcessorPrice.setText("Price : " + String.format("%.2f", maxProcessorPrice) + " $");

                                        textViewGraphicsCard.setVisibility(View.VISIBLE);
                                        layoutGraphicsCard.setVisibility(View.VISIBLE);
                                        textViewGraphicsCardRecommendation.setVisibility(View.VISIBLE);
                                        textViewGraphicsCardPrice.setVisibility(View.VISIBLE);
                                        buttonSearchGraphicsCard.setVisibility(View.VISIBLE);
                                        textViewSpaceGraphicsCard.setVisibility(View.VISIBLE);
                                        textViewMonitor.setVisibility(View.GONE);
                                        layoutMonitor.setVisibility(View.GONE);
                                        textViewGraphicsCardRecommendation.setText(maxGraphicsCardModel);
                                        textViewGraphicsCardPrice.setText("Price : " + String.format("%.2f", maxGraphicsCardPrice) + " $");
                                        buttonSearchGraphicsCard.setOnClickListener(view -> {
                                            if (maxGraphicsCardModel != null) {
                                                searchOnGoogle(maxGraphicsCardModel);
                                            } else {
                                                Toast.makeText(HomeActivity.this, "Graphics Card does not exists", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        findMotherboard(processorSocket, processorMemoryType, processorMemorySpeed);
                                        calculatePowerSupply(processorPower, graphicsCardPower);
                                        showCoolerRecommendation();
                                        addStorageToConfiguration(maxPrice);
                                        updateMonitorRecommendation();
                                        showPopup();

                                        double total = maxProcessorPrice + maxGraphicsCardPrice;
                                        totalPrice += total;
                                        textViewTotal.setText("This configuration will cost you approximately: " + String.format("%.2f", totalPrice) + " $");
                                    } else {
                                        textViewProcessor.setText("Nu s-a găsit nicio recomandare.");
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Graphical cards reading error", e);
                                asyncTasksCompleted++;
                                if (asyncTasksCompleted == totalAsyncTasks) {
                                    updateTotalAndShowRecommendations(maxPrice);
                                }
                            });

                }
            }

            if (asyncTasksCompleted == totalAsyncTasks) {
                updateTotalAndShowRecommendations(maxPrice);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Processors reading error", e);
            asyncTasksCompleted++;
            if (asyncTasksCompleted == totalAsyncTasks) {
                updateTotalAndShowRecommendations(maxPrice);
            }
        });
    }


    private void updateTotalAndShowRecommendations(double maxPrice) {
        showRecommendation();
    }

    void showCoolerRecommendation() {
        String coolerModel = "Cooler CPU be quiet! Dark Rock 4";

        if (selectedUsage != null && (selectedUsage.equals("Gaming") || selectedUsage.equals("Graphic Design") || selectedUsage.equals("Other"))) {
            totalPrice += 75;
            if (!textViewProcessorRecommendation.getText().toString().contains(coolerModel)) {
                textViewCooler.setVisibility(View.VISIBLE);
                layoutCooler.setVisibility(View.VISIBLE);
                textViewCoolerRecommendation.setVisibility(View.VISIBLE);
                String coolerRecommendation = coolerModel + " Socket " + processorSocket;
                textViewCoolerRecommendation.setText(coolerRecommendation);
            ImageButton buttonSearchCooler = popupView.findViewById(R.id.buttonSearchCooler);
                buttonSearchCooler.setOnClickListener(view -> {
                    if (coolerModel != null) {
                        searchOnGoogle(coolerModel);
                    } else {
                        Log.e(TAG, "Cooler reading error");
                    }
                });
            }
        } else {
            textViewCoolerRecommendation.setVisibility(View.GONE);

            if (!selectedUsage.equals("Office") && !selectedUsage.equals("Programming")) {
                totalPrice += 75;
            }
        }
    }





    void findMotherboard(String processorSocket, List<String> processorMemoryType, List<String> processorMemorySpeed) {
        String maxPriceString = editTextMaxPrice.getText().toString();
        double maxPrice = Double.parseDouble(maxPriceString);
        double maxMotherboardPrice = 0;

        if (maxPrice <= 1500){
            maxMotherboardPrice = maxPrice * 0.20;
        }else if(maxPrice > 1500 && maxPrice < 2500){
            maxMotherboardPrice = maxPrice * 0.25;
        }else{
            maxMotherboardPrice = maxPrice * 0.32;
        }



        db.collection("MOTHERBOARD")
                .whereEqualTo("SOCKET", processorSocket)
                .whereIn("MEMORY_TYPE", processorMemoryType)
                .whereLessThanOrEqualTo("PRICE", maxMotherboardPrice)
                .orderBy("PRICE", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> compatibleMotherboards = new ArrayList<>();
                    for (DocumentSnapshot motherboardDocument : queryDocumentSnapshots) {
                        List<String> supportedMemorySpeeds = (List<String>) motherboardDocument.get("MHZ");
                        motherboardFormat = motherboardDocument.getString("FORMAT");
                        motherboardMemorySpeed = (List<String>) motherboardDocument.get("MHZ");
                        if (supportedMemorySpeeds != null && !supportedMemorySpeeds.isEmpty()) {
                            for (String speed : supportedMemorySpeeds) {
                                try {
                                    int speedValue = Integer.parseInt(speed.trim());
                                    if (processorMemorySpeed.contains(String.valueOf(speedValue))) {
                                        compatibleMotherboards.add(motherboardDocument);
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    Log.e(TAG, "Eroare la conversia frecvenței de memorie: " + speed, e);
                                }
                            }
                        }
                    }

                    if (!compatibleMotherboards.isEmpty()) {
                        DocumentSnapshot recommendedMotherboard = compatibleMotherboards.get(0);
                        String motherboardModel = recommendedMotherboard.getString("MODEL");
                        double motherboardPrice = recommendedMotherboard.getDouble("PRICE");


                        if (totalPrice + motherboardPrice <= maxPrice) {
                            ImageButton buttonSearchMotherboard = popupView.findViewById(R.id.buttonSearchMotherboard);
                            textViewMotherboardRecommendation.setText(motherboardModel);
                            textViewMotherboardPrice.setText("Price : " + String.format("%.2f", motherboardPrice) + " $");
                            buttonSearchMotherboard.setOnClickListener(view -> {
                                if (motherboardModel != null) {
                                    searchOnGoogle(motherboardModel);
                                } else {
                                    Toast.makeText(HomeActivity.this, "Motherboard does not exists", Toast.LENGTH_SHORT).show();
                                }
                            });
                            showCaseRecommendation();
                            showRAMRecommendation(processorMemoryType, motherboardMemorySpeed);
                            totalPrice += motherboardPrice;
                            textViewTotal.setText("This configuration will cost you approximately: " + String.format("%.2f", totalPrice) + " $");
                        }
                    } else {
                        Log.d(TAG, "No compatibility found ");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "No compatibility found ", e);
                });
    }


    void showCaseRecommendation() {
        if (!motherboardFormat.isEmpty()) {
            double casePrice = 0;
            String caseModel = "";

            switch (motherboardFormat) {
                case "ATX":
                    casePrice = 78;
                    caseModel = "Corsair 3000D Airflow Black";
                    break;
                case "E-ATX":
                    casePrice = 130;
                    caseModel = "Lian Li LanCool II Mesh C Performance Black";
                    break;
                case "mATX":
                    casePrice = 63;
                    caseModel = "Aerocool Hive ARGB";
                    break;
                case "MicroATX":
                    casePrice = 53;
                    caseModel = "Deepcool CC360 ARGB";
                    break;
                case "MiniITX":
                    casePrice = 49;
                    caseModel = "Deepcool MATREXX 40 3FS";
                    break;
                default:
                    casePrice = 0;
                    break;
            }

            if (casePrice != 0) {
                ImageButton buttonSearchCase = popupView.findViewById(R.id.buttonSearchCase);
                textViewCase.setVisibility(View.VISIBLE);
                textViewCaseRecommendation.setText(caseModel);
                textViewCasePrice.setText("Price : " + String.format("%.2f", casePrice) + " $");
                String finalCaseModel = caseModel;
                buttonSearchCase.setOnClickListener(view -> {
                        searchOnGoogle(finalCaseModel);
                });
                totalPrice += casePrice;
            } else {
                caseModel = "No case found with " + motherboardFormat + " Format";
            }

            textViewTotal.setText("This configuration will cost you approximately: " + String.format("%.2f", totalPrice) + " $");
        } else {
            textViewCaseRecommendation.setText("Nu s-a găsit formatul plăcii de bază.");
            textViewCaseRecommendation.setVisibility(View.VISIBLE);
        }
    }




    private void showRAMRecommendation(List<String> processorMemoryType, List<String> motherboardMemorySpeed) {
        String maxPriceString = editTextMaxPrice.getText().toString();
        double maxPrice = Double.parseDouble(maxPriceString);
        double maxRAMPrice = 0;
        if (maxPrice <= 1500){
            maxRAMPrice = maxPrice * 0.1;
        }else if(maxPrice > 1500 && maxPrice < 2500){
            maxRAMPrice = maxPrice * 0.15;
        }else{
            maxRAMPrice = maxPrice * 0.23;
        }

        Log.e(TAG, "memorie ram" + motherboardMemorySpeed);
        Log.e(TAG, "tip memorie ram" + processorMemoryType);
        db.collection("RAM")
                .whereArrayContainsAny("MHZ", motherboardMemorySpeed)
                .whereLessThanOrEqualTo("PRICE", maxRAMPrice)
                .orderBy("PRICE", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot recommendedRAM = queryDocumentSnapshots.getDocuments().get(0);

                        String documentId = recommendedRAM.getId();

                        String ramModel = recommendedRAM.getString("MODEL");
                        Double ramPrice = recommendedRAM.getDouble("PRICE");
                        if (ramPrice != null) {
                            ImageButton buttonSearchRAM = popupView.findViewById(R.id.buttonSearchRAM);
                            textViewRAMRecommendation.setText(ramModel);
                            totalPrice+=ramPrice;
                            textViewRAMPrice.setText("Price : " + String.format("%.2f", ramPrice) + " $");
                            textViewTotal.setText("This configuration will cost you approximately: " + String.format("%.2f", totalPrice) + " $");
                            buttonSearchRAM.setOnClickListener(view -> {
                                if (ramModel != null) {
                                    searchOnGoogle(ramModel);
                                } else {
                                    Toast.makeText(HomeActivity.this, "RAM does not exists", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.d(TAG, "Cannot find RAM.");
                        }

                        Log.d(TAG, "RAM Doc ID  " + documentId);
                    } else {
                        Log.d(TAG, "No compt RAM");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firebase error reading ", e);
                });
    }



    void calculatePowerSupply(double processorPower, double graphicsCardPower) {
        double motherboardPower = 50;
        double ramPower = 5;
        double additionalPower = 120;

        Log.e(TAG, "calculator : " +processorPower + " + "+  graphicsCardPower + " + "+ motherboardPower + " + "+ ramPower + " + "+ additionalPower);

        double totalPower = processorPower + graphicsCardPower + motherboardPower + ramPower + additionalPower;
        Log.e(TAG, "total power : " + totalPower);
        if (selectedUsage != null && (selectedUsage.equals("Gaming") || selectedUsage.equals("Graphic Design") || selectedUsage.equals("Other"))) {
            double coolerPower = 255;
            totalPower += coolerPower;
        }

        int roundedTotalPower = (int) Math.ceil(totalPower / 50) * 50;

        String recommendedPowerSupply = "";

        int availablePowerSupply = roundedTotalPower;

        db.collection("POWER_SUPPLY")
                .whereEqualTo("POWER", availablePowerSupply)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot powerSupplyDocument : queryDocumentSnapshots) {
                        Double powerSupplyPrice = powerSupplyDocument.getDouble("PRICE");
                        String powerSupplyModel = powerSupplyDocument.getString("MODEL");
                        if (powerSupplyPrice != null) {
                            totalPrice += powerSupplyPrice;

                            ImageButton buttonSearchPowerSupply = popupView.findViewById(R.id.buttonSearchPowerSupply);
                            textViewPowerSupplyRecommendation.setText(powerSupplyModel);
                            textViewPowerSupplyPrice.setText("Price : " + String.format("%.2f", powerSupplyPrice) + " $");
                            textViewTotal.setText("This configuration will cost you approximately: " + String.format("%.2f", totalPrice) + " $");
                            buttonSearchPowerSupply.setOnClickListener(view -> {
                                if (powerSupplyModel != null) {
                                    searchOnGoogle(powerSupplyModel);
                                } else {
                                    Toast.makeText(HomeActivity.this, "powerSupplyModel does not exists", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Log.e(TAG, "powerSupplyModel IS NULL");
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error reading PS ", e);
                });

        Log.d(TAG, "Recommended PS: " + availablePowerSupply);
    }

    void addStorageToConfiguration(double maxPrice) {
        double storagePrice = 0;
        ImageButton buttonSearchStorage = popupView.findViewById(R.id.buttonSearchStorage);
        if (maxPrice <= 1000) {
            storagePrice = 60;
            String SSDModel = "Solid-State Drive (SSD) ADATA XPG GAMMIX S70 Blade, 512GB, NVMe, M.2";
            textViewStorageRecommendation.setText(SSDModel);
            textViewStoragePrice.setText("Price : 60.00 $");
            buttonSearchStorage.setOnClickListener(view -> {
                if (SSDModel != null) {
                    searchOnGoogle(SSDModel);
                } else {
                    Toast.makeText(HomeActivity.this, "SSDModel does not exists", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (maxPrice <= 2000 && maxPrice > 1000) {
            storagePrice = 101;
            String SSDModel = "Solid State Drive (SSD) Samsung 980 PRO Gen.4, 1TB, NVMe, M.2.";
            textViewStorageRecommendation.setText(SSDModel);
            textViewStoragePrice.setText("Price : 101.00 $");
            buttonSearchStorage.setOnClickListener(view -> {
                if (SSDModel != null) {
                    searchOnGoogle(SSDModel);
                } else {
                    Toast.makeText(HomeActivity.this, "SSDModel does not exists", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (maxPrice >= 2000) {
            storagePrice = 190;
            String SSDModel = "Solid State Drive (SSD) Samsung 990 PRO 2TB, PCIe Gen 4.0 x4, NVMe, M.2";
            textViewStorageRecommendation.setText(SSDModel);
            textViewStoragePrice.setText("Price : 190.00 $");
            buttonSearchStorage.setOnClickListener(view -> {
                if (SSDModel != null) {
                    searchOnGoogle(SSDModel);
                } else {
                    Toast.makeText(HomeActivity.this, "SSDModel does not exists", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            textViewStorage.setText("SSDModel does not exists");
        }

        totalPrice += storagePrice;
        textViewTotal.setText("This configuration will cost you approximately: " + String.format("%.2f", totalPrice) + " $");
    }

    private void searchOnGoogle(String searchTerm) {
        String searchUrl = GOOGLE_SEARCH_URL + searchTerm.replace(" ", "+");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(searchUrl));
        startActivity(intent);
    }


    private void showPopup() {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }

        popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

        Button closeButton = popupView.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    private void saveConfigurationToFirebase(String configurationName) {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("wishlist");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = databaseRef.child(userId).child(configurationName);

        // Procesor
        saveComponentToFirebase(userRef, "Processor", R.id.textViewProcessorRecommendation, R.id.textViewProcessorPrice);

        // Cooler
        saveComponentToFirebase(userRef, "Cooler", R.id.textViewCoolerRecommendation, R.id.textViewCoolerPrice);

        // GC
        saveComponentToFirebase(userRef, "GraphicsCard", R.id.textViewGraphicsCardRecommendation, R.id.textViewGraphicsCardPrice);

        // Motherboard
        saveComponentToFirebase(userRef, "Motherboard", R.id.textViewMotherboardRecommendation, R.id.textViewMotherboardPrice);

        // RAM
        saveComponentToFirebase(userRef, "RAM", R.id.textViewRAMRecommendation, R.id.textViewRAMPrice);

        // Storage
        saveComponentToFirebase(userRef, "Storage", R.id.textViewStorageRecommendation, R.id.textViewStoragePrice);

        // PS
        saveComponentToFirebase(userRef, "PowerSupply", R.id.textViewPowerSupplyRecommendation, R.id.textViewPowerSupplyPrice);

        // Case
        saveComponentToFirebase(userRef, "Case", R.id.textViewCaseRecommendation, R.id.textViewCasePrice);

        // Monitor
        saveComponentToFirebase(userRef, "Monitor", R.id.textViewMonitorRecommendation, R.id.textViewMonitorPrice);

        TextView totalTextView = popupView.findViewById(R.id.textViewTotal);
        if (totalTextView != null) {
            String total = totalTextView.getText().toString();
            userRef.child("Total").setValue(total);
        } else {
            Log.e(TAG, "TextView for total not found");
        }
    }

    private void saveComponentToFirebase(DatabaseReference userRef, String componentName, int recommendationId, int priceId) {
        TextView recommendationTextView = popupView.findViewById(recommendationId);
        TextView priceTextView = popupView.findViewById(priceId);

        if (recommendationTextView != null && priceTextView != null) {
            String recommendation = recommendationTextView.getText().toString();
            String price = priceTextView.getText().toString();

            userRef.child(componentName).child("Recommendation").setValue(recommendation);
            userRef.child(componentName).child("Price").setValue(price);
        } else {
            Log.e(TAG, "TextView for " + componentName + " recommendation or price not found");
        }
    }

    void updateMonitorRecommendation() {
        EditText editTextMaxPrice = findViewById(R.id.editTextMaxPrice);
        TextView textViewMonitorRecommendation = popupView.findViewById(R.id.textViewMonitorRecommendation);
        TextView textViewMonitorPrice = popupView.findViewById(R.id.textViewMonitorPrice);
        TextView textViewTotal = popupView.findViewById(R.id.textViewTotal);
        ImageButton buttonSearchMonitor = popupView.findViewById(R.id.buttonSearchMonitor);

        String MonitorModel;
        double monitorPrice = 0;
        Log.e(TAG, "updateMonitorRecommendation has been called");

        if (checkBoxIncludeMonitor.isChecked()) {
            textViewMonitor.setVisibility(View.VISIBLE);
            layoutMonitor.setVisibility(View.VISIBLE);
            String selectedUsage = spinnerUsage.getSelectedItem().toString();
            double maxPrice = Double.parseDouble(editTextMaxPrice.getText().toString());


            if (selectedUsage != null && (selectedUsage.equals("Gaming") || selectedUsage.equals("Graphic Design") || selectedUsage.equals("Other"))) {
                if (maxPrice <= 1500) {
                    MonitorModel = "Monitor Gaming Philips EVNIA IPS 23.8 inch, FHD, 165Hz, 4ms";
                    textViewMonitorRecommendation.setText(MonitorModel);
                    textViewMonitorPrice.setText("Price : 121.00 $");
                    monitorPrice = 121;
                } else {
                    MonitorModel = "Monitor Gaming Dell G2723H, 27 inch, Full HD, 240Hz, Fast IPS, AMD FreeSync™";
                    textViewMonitorRecommendation.setText(MonitorModel);
                    textViewMonitorPrice.setText("Price : 220.00 $");
                    monitorPrice = 220;
                }
            } else if (selectedUsage.equals("Office") || selectedUsage.equals("Programming")) {
                if (maxPrice <= 1000) {
                    MonitorModel = "Monitor AOC LED IPS VA 23.8 inch, Full HD, 75Hz, 4ms, HDMI, VGA, 24B2XH/EU";
                    textViewMonitorRecommendation.setText(MonitorModel);
                    textViewMonitorPrice.setText("Price : 84.00 $");
                    monitorPrice = 84;
                } else {
                    MonitorModel = "Monitor ASUS VA27DCP, 27 inch, Full HD, IPS, Eye Care, Low Blue Light, USB-C";
                    textViewMonitorRecommendation.setText(MonitorModel);
                    textViewMonitorPrice.setText("Price : 182.00 $");
                    monitorPrice = 182;
                }
            } else {
                MonitorModel = "";
            }

            buttonSearchMonitor.setOnClickListener(view -> {
                if (MonitorModel != null) {
                    searchOnGoogle(MonitorModel);
                } else {
                    Toast.makeText(HomeActivity.this, "Monitor Model does not exists", Toast.LENGTH_SHORT).show();
                }
            });
            totalPrice += monitorPrice;
            textViewTotal.setText("This configuration will cost you approximately: " + String.format("%.2f", totalPrice) + " $");
        } else {
            MonitorModel = "";
        }
    }



}