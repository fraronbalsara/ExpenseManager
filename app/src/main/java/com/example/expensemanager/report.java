package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class report extends AppCompatActivity {

    private static final int STORAGE_CODE = 1000;
    private static final String TAG ="report";

    String month, year, currency, mFileName, mFilePath, Food, Bills, Transportation, Home, Car, Entertainment, Clothing, Insurance, Tax, Health, Sport, Kids, Pet, Beauty, Electronics, Gift, Social, Travel, Education, Office, Other, Total, Salary, Awards, Grants, Rental, Refunds, Lottery, Dividends, Investments, Other2, Total2;

    private ArrayList<String> xData = new ArrayList<>();
    private ArrayList<String> yData = new ArrayList<>();
    private String mode, mode2;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;
    String id;

    //Progress Dialog
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        userDatabase = FirebaseFirestore.getInstance();
        id = firebaseAuth.getCurrentUser().getUid();

        //Declaring Views
        TextView t1 = findViewById(R.id.t1);
        TextView t2 = findViewById(R.id.t2);
        ImageButton b1 = findViewById(R.id.b1);
        ImageButton b2 = findViewById(R.id.b2);
        ImageButton b3 = findViewById(R.id.b3);
        ImageButton b4 = findViewById(R.id.b4);

        //Progress Dialog
        pd = new ProgressDialog(this);

        //Retrieving currency value
        userDatabase.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currency = documentSnapshot.getString("currency");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve currency");
                    }
                });

        Date d = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String date = simpleDateFormat.format(d);
        String[] date_split = date.split("-");
        month = date_split[1] + "-" + date_split[2];
        year = date_split[2];

        t1.setText("Path: Internal Storage/Expense Manager/" + "Report-" + month);
        t2.setText("Path: Internal Storage/Expense Manager/" + "Report-" + year);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileName = "Report-" + month;
                mode = "month";
                mode2 = "close";
                permission();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("Loading...");
                pd.show();
                mFileName = "Report-" + month;
                mode = "month";
                mode2 = "open";
                permission();
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileName = "Report-" + year;
                mode = "year";
                mode2 = "close";
                permission();
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.setTitle("Loading...");
                pd.show();
                mFileName = "Report-" + year;
                mode = "year";
                mode2  = "open";
                permission();
            }
        });

    }

    public  void permission(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //if permission is denied on marshmallow and above
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions,STORAGE_CODE);
            }
            else {
                //called if already granted for marshmallow and above
                if(mode.equals("month")) {
                    monthReport();
                }
                else if(mode.equals("year")){
                    yearReport();
                }
            }
        }
        else{
            //if sdk version below marshmallow
            if(mode.equals("month")) {
                monthReport();
            }
            else if(mode.equals("year")){
                yearReport();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case STORAGE_CODE:{
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(mode.equals("month")) {
                        monthReport();
                    }
                    else if(mode.equals("year")){
                        yearReport();
                    }
                }
                else {
                    AlertDialog.Builder ad = new AlertDialog.Builder(report.this);
                    ad.setMessage("Permission denied. \nWrite External Storage permission is required to execute this.");
                    ad.setCancelable(false);
                    ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });
                    ad.show();
                }
            }
        }
    }

    public void openPDF(){
        Intent intent = new Intent();
        File pdfFile = new File(mFilePath);
        Uri path = Uri.fromFile(pdfFile);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(path);
        startActivity(intent);
    }

    public void savePDF(String textData, String textData2){
        File f = new File(String.valueOf(Environment.getExternalStorageDirectory())+"/Expense Manager");
        f.mkdir();
        Document mDoc = new Document();
        Environment.getExternalStorageDirectory().mkdir();
        mFilePath = Environment.getExternalStorageDirectory() + "/Expense Manager/" + mFileName + ".pdf";
        try{
            PdfWriter.getInstance(mDoc, new FileOutputStream(mFilePath));
            mDoc.open();
            mDoc.addAuthor("Expense Manager");
            Paragraph Title = new Paragraph(mFileName);
            Title.setAlignment(Paragraph.ALIGN_CENTER);
            mDoc.add(new Paragraph(Title));
            mDoc.add(new Paragraph("\nExpense"));
            mDoc.add(new Paragraph("\n" + textData));
            mDoc.add(new Paragraph("\nIncome"));
            mDoc.add(new Paragraph("\n" + textData2));
            mDoc.close();
            if (!mode2.equals("open")) {
                Toast.makeText(this,"File saved successfully.",Toast.LENGTH_LONG).show();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        catch (Exception e){
            Log.d(TAG,"Error Occurred: " + e.toString());
        }
        if(mode2.equals("open")){
            pd.dismiss();
            openPDF();
        }
    }

    public void monthReport() {
        userDatabase.collection("users").document(id).collection("Expense").document("Total").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Food = documentSnapshot.getString("total-Food-" + month);
                        Bills = documentSnapshot.getString("total-Bills-" + month);
                        Transportation = documentSnapshot.getString("total-Transportation-" + month);
                        Home = documentSnapshot.getString("total-Home-" + month);
                        Car = documentSnapshot.getString("total-Car-" + month);
                        Entertainment = documentSnapshot.getString("total-Entertainment-" + month);
                        Clothing = documentSnapshot.getString("total-Clothing-" + month);
                        Insurance = documentSnapshot.getString("total-Insurance-" + month);
                        Tax = documentSnapshot.getString("total-Tax-" + month);
                        Health = documentSnapshot.getString("total-Health-" + month);
                        Sport = documentSnapshot.getString("total-Sport-" + month);
                        Kids = documentSnapshot.getString("total-Kids-" + month);
                        Pet = documentSnapshot.getString("total-Pet-" + month);
                        Beauty = documentSnapshot.getString("total-Beauty-" + month);
                        Electronics = documentSnapshot.getString("total-Electronics-" + month);
                        Gift = documentSnapshot.getString("total-Gift-" + month);
                        Social = documentSnapshot.getString("total-Social-" + month);
                        Travel = documentSnapshot.getString("total-Travel-" + month);
                        Education = documentSnapshot.getString("total-Education-" + month);
                        Office = documentSnapshot.getString("total-Office-" + month);
                        Other = documentSnapshot.getString("total-Other-" + month);
                        Total = documentSnapshot.getString("total-" + month);

                        xData.clear();
                        yData.clear();

                        if(Food != null){xData.add("Food"); yData.add(Food);}
                        if(Bills != null){xData.add("Bills"); yData.add(Bills);}
                        if(Transportation != null){xData.add("Transportation"); yData.add(Transportation);}
                        if(Home != null){xData.add("Home"); yData.add(Home);}
                        if(Car != null){xData.add("Car"); yData.add(Car);}
                        if(Entertainment != null){xData.add("Entertainment"); yData.add(Entertainment);}
                        if(Clothing != null){xData.add("Clothing"); yData.add(Clothing);}
                        if(Insurance != null){xData.add("Insurance"); yData.add(Insurance);}
                        if(Tax != null){xData.add("Tax"); yData.add(Tax);}
                        if(Health != null){xData.add("Health"); yData.add(Health);}
                        if(Sport != null){xData.add("Sport"); yData.add(Sport);}
                        if(Kids != null){xData.add("Kids"); yData.add(Kids);}
                        if(Pet != null){xData.add("Pet"); yData.add(Pet);}
                        if(Beauty != null){xData.add("Beauty"); yData.add(Beauty);}
                        if(Electronics != null){xData.add("Electronics"); yData.add(Electronics);}
                        if(Gift != null){xData.add("Gift"); yData.add(Gift);}
                        if(Social != null){xData.add("Social"); yData.add(Social);}
                        if(Travel != null){xData.add("Travel"); yData.add(Travel);}
                        if(Education != null){xData.add("Education"); yData.add(Education);}
                        if(Office != null){xData.add("Office"); yData.add(Office);}
                        if(Other != null){xData.add("Other"); yData.add(Other);}
                        if(Total != null){xData.add("Total"); yData.add(Total);}

                        String textData = "";
                        for(int i=0;i<xData.size();i++){
                            textData = textData + xData.get(i) + ": " + currency + yData.get(i) + "\n";
                        }

                        final String finalTextData = textData;
                        userDatabase.collection("users").document(id).collection("Income").document("Total").get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        Salary = documentSnapshot.getString("total-Salary-" + month);
                                        Awards = documentSnapshot.getString("total-Awards-" + month);
                                        Grants = documentSnapshot.getString("total-Grants-" + month);
                                        Rental = documentSnapshot.getString("total-Rental-" + month);
                                        Refunds = documentSnapshot.getString("total-Refunds-" + month);
                                        Lottery = documentSnapshot.getString("total-Lottery-" + month);
                                        Dividends = documentSnapshot.getString("total-Dividends-" + month);
                                        Investments = documentSnapshot.getString("total-Investments-" + month);
                                        Other2 = documentSnapshot.getString("total-Other-" + month);
                                        Total2 = documentSnapshot.getString("total-" + month);

                                        xData.clear();
                                        yData.clear();

                                        if(Salary != null){xData.add("Salary"); yData.add(Salary);}
                                        if(Awards != null){xData.add("Awards"); yData.add(Awards);}
                                        if(Grants != null){xData.add("Grants"); yData.add(Grants);}
                                        if(Rental != null){xData.add("Rental"); yData.add(Rental);}
                                        if(Refunds != null){xData.add("Refunds"); yData.add(Refunds);}
                                        if(Lottery != null){xData.add("Lottery"); yData.add(Lottery);}
                                        if(Dividends != null){xData.add("Dividends"); yData.add(Dividends);}
                                        if(Investments != null){xData.add("Investments"); yData.add(Investments);}
                                        if(Other2 != null){xData.add("Other"); yData.add(Other2);}
                                        if(Total2 != null){xData.add("Total"); yData.add(Total2);}

                                        String textData2 = "";
                                        for(int i=0;i<xData.size();i++){
                                            textData2 = textData2 + xData.get(i) + ": " + currency + yData.get(i) + "\n";
                                        }
                                        savePDF(finalTextData, textData2);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"Failed to retrieve monthly income data. --- " + e.toString());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve monthly expense data. --- " + e.toString());
                    }
                });
    }

    public void yearReport(){
        userDatabase.collection("users").document(id).collection("Expense").document("Total").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Food = documentSnapshot.getString("total-Food-" + year);
                        Bills = documentSnapshot.getString("total-Bills-" + year);
                        Transportation = documentSnapshot.getString("total-Transportation-" + year);
                        Home = documentSnapshot.getString("total-Home-" + year);
                        Car = documentSnapshot.getString("total-Car-" + year);
                        Entertainment = documentSnapshot.getString("total-Entertainment-" + year);
                        Clothing = documentSnapshot.getString("total-Clothing-" + year);
                        Insurance = documentSnapshot.getString("total-Insurance-" + year);
                        Tax = documentSnapshot.getString("total-Tax-" + year);
                        Health = documentSnapshot.getString("total-Health-" + year);
                        Sport = documentSnapshot.getString("total-Sport-" + year);
                        Kids = documentSnapshot.getString("total-Kids-" + year);
                        Pet = documentSnapshot.getString("total-Pet-" + year);
                        Beauty = documentSnapshot.getString("total-Beauty-" + year);
                        Electronics = documentSnapshot.getString("total-Electronics-" + year);
                        Gift = documentSnapshot.getString("total-Gift-" + year);
                        Social = documentSnapshot.getString("total-Social-" + year);
                        Travel = documentSnapshot.getString("total-Travel-" + year);
                        Education = documentSnapshot.getString("total-Education-" + year);
                        Office = documentSnapshot.getString("total-Office-" + year);
                        Other = documentSnapshot.getString("total-Other-" + year);
                        Total = documentSnapshot.getString("total-" + year);

                        xData.clear();
                        yData.clear();

                        if(Food != null){xData.add("Food"); yData.add(Food);}
                        if(Bills != null){xData.add("Bills"); yData.add(Bills);}
                        if(Transportation != null){xData.add("Transportation"); yData.add(Transportation);}
                        if(Home != null){xData.add("Home"); yData.add(Home);}
                        if(Car != null){xData.add("Car"); yData.add(Car);}
                        if(Entertainment != null){xData.add("Entertainment"); yData.add(Entertainment);}
                        if(Clothing != null){xData.add("Clothing"); yData.add(Clothing);}
                        if(Insurance != null){xData.add("Insurance"); yData.add(Insurance);}
                        if(Tax != null){xData.add("Tax"); yData.add(Tax);}
                        if(Health != null){xData.add("Health"); yData.add(Health);}
                        if(Sport != null){xData.add("Sport"); yData.add(Sport);}
                        if(Kids != null){xData.add("Kids"); yData.add(Kids);}
                        if(Pet != null){xData.add("Pet"); yData.add(Pet);}
                        if(Beauty != null){xData.add("Beauty"); yData.add(Beauty);}
                        if(Electronics != null){xData.add("Electronics"); yData.add(Electronics);}
                        if(Gift != null){xData.add("Gift"); yData.add(Gift);}
                        if(Social != null){xData.add("Social"); yData.add(Social);}
                        if(Travel != null){xData.add("Travel"); yData.add(Travel);}
                        if(Education != null){xData.add("Education"); yData.add(Education);}
                        if(Office != null){xData.add("Office"); yData.add(Office);}
                        if(Other != null){xData.add("Other"); yData.add(Other);}
                        if(Total != null){xData.add("Total"); yData.add(Total);}

                        String textData = "";
                        for(int i=0;i<xData.size();i++){
                            textData = textData + xData.get(i) + ": " + currency + yData.get(i) + "\n";
                        }

                        final String finalTextData = textData;
                        userDatabase.collection("users").document(id).collection("Income").document("Total").get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        Salary = documentSnapshot.getString("total-Salary-" + year);
                                        Awards = documentSnapshot.getString("total-Awards-" + year);
                                        Grants = documentSnapshot.getString("total-Grants-" + year);
                                        Rental = documentSnapshot.getString("total-Rental-" + year);
                                        Refunds = documentSnapshot.getString("total-Refunds-" + year);
                                        Lottery = documentSnapshot.getString("total-Lottery-" + year);
                                        Dividends = documentSnapshot.getString("total-Dividends-" + year);
                                        Investments = documentSnapshot.getString("total-Investments-" + year);
                                        Other2 = documentSnapshot.getString("total-Other-" + year);
                                        Total2 = documentSnapshot.getString("total-" + year);

                                        xData.clear();
                                        yData.clear();

                                        if(Salary != null){xData.add("Salary"); yData.add(Salary);}
                                        if(Awards != null){xData.add("Awards"); yData.add(Awards);}
                                        if(Grants != null){xData.add("Grants"); yData.add(Grants);}
                                        if(Rental != null){xData.add("Rental"); yData.add(Rental);}
                                        if(Refunds != null){xData.add("Refunds"); yData.add(Refunds);}
                                        if(Lottery != null){xData.add("Lottery"); yData.add(Lottery);}
                                        if(Dividends != null){xData.add("Dividends"); yData.add(Dividends);}
                                        if(Investments != null){xData.add("Investments"); yData.add(Investments);}
                                        if(Other2 != null){xData.add("Other"); yData.add(Other2);}
                                        if(Total2 != null){xData.add("Total"); yData.add(Total2);}

                                        String textData2 = "";
                                        for(int i=0;i<xData.size();i++){
                                            textData2 = textData2 + xData.get(i) + ": " + currency + yData.get(i) + "\n";
                                        }
                                        savePDF(finalTextData, textData2);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"Failed to retrieve yearly income data. --- " + e.toString());
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve yearly expense data. --- " + e.toString());
                    }
                });
    }
}
