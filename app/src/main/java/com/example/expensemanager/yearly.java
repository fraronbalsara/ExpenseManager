package com.example.expensemanager;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link yearly.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link yearly#newInstance} factory method to
 * create an instance of this fragment.
 */
public class yearly extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private static final String TAG = "yearly";
    private String Food, Bills, Transportation, Home, Car, Entertainment, Clothing, Insurance, Tax, Health, Sport, Kids, Pet, Beauty, Electronics, Gift, Social, Travel, Education, Office, Other, Total, Salary, Awards, Grants, Rental, Refunds, Lottery, Dividends, Investments, Other2, Total2, e_2015, e_2016, e_2017, e_2018, e_2019, e_2020, i_2015, i_2016, i_2017, i_2018, i_2019, i_2020;
    private Float a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,total,divisor;
    private Float a2,b2,c2,d2,e2,f2,g2,h2,i2,divisor2,total2;
    private ArrayList<String> xData = new ArrayList<>();
    private ArrayList<Float> yData = new ArrayList<>();
    private ArrayList<String> zData = new ArrayList<>();
    PieChart pieChart, pieChart2;
    BarChart barChart;
    Spinner yearly_spinner;
    TextView textView, textView2;

    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore userDatabase;
    private StorageReference storageReference;

    String currency;

    private OnFragmentInteractionListener mListener;

    public yearly() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment yearly.
     */
    // TODO: Rename and change types and number of parameters
    public static yearly newInstance(String param1, String param2) {
        yearly fragment = new yearly();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View v = inflater.inflate(R.layout.fragment_yearly, container, false);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        userDatabase = FirebaseFirestore.getInstance();
        String id = firebaseAuth.getCurrentUser().getUid();

        //Views
        yearly_spinner = v.findViewById(R.id.yearly_spinner);
        pieChart = v.findViewById(R.id.yearly_pie);
        pieChart2 = v.findViewById(R.id.yearly_pie2);
        barChart = v.findViewById(R.id.yearly_bar);
        textView = v.findViewById(R.id.yearlyPieChartData);
        textView2 = v.findViewById(R.id.yearlyPieChartData2);

        DateFormat dateFormat = new SimpleDateFormat("YYYY");
        Date date = new Date();
        final String year = dateFormat.format(date);

        yearly_spinner.post(new Runnable() {
            @Override
            public void run() {
                int selection = Integer.parseInt(year) - 2015;
                yearly_spinner.setSelection(selection);
            }
        });

        //Retrieving currency value
        userDatabase.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        currency = documentSnapshot.getString("currency");
                        displayPieChart(year);
                        displayPieChart2(year);
                        displayBarChart();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to retrieve currency");
                    }
                });

        yearly_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String year = String.valueOf(position + 2015);
                displayPieChart(year);
                displayPieChart2(year);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
    }

    private void displayPieChart(final String year){
        String id = firebaseAuth.getCurrentUser().getUid();
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
                        zData.clear();

                        if(Total != null){total = Float.parseFloat(Total); divisor = total / 100;}
                        if(Food != null){a = Float.parseFloat(Food) / divisor; xData.add("Food"); yData.add(a); zData.add(Food);}
                        if(Bills != null){b = Float.parseFloat(Bills) / divisor; xData.add("Bills"); yData.add(b); zData.add(Bills);}
                        if(Transportation != null){c = Float.parseFloat(Transportation) / divisor; xData.add("Transportation"); yData.add(c); zData.add(Transportation);}
                        if(Home != null){d = Float.parseFloat(Home) / divisor; xData.add("Home"); yData.add(d); zData.add(Home);}
                        if(Car != null){e = Float.parseFloat(Car) / divisor; xData.add("Car"); yData.add(e); zData.add(Car);}
                        if(Entertainment != null){f = Float.parseFloat(Entertainment) / divisor; xData.add("Entertainment"); yData.add(f); zData.add(Entertainment);}
                        if(Clothing != null){g = Float.parseFloat(Clothing) / divisor; xData.add("Clothing"); yData.add(g); zData.add(Clothing);}
                        if(Insurance != null){h = Float.parseFloat(Insurance) / divisor; xData.add("Insurance"); yData.add(h); zData.add(Insurance);}
                        if(Tax != null){i = Float.parseFloat(Tax) / divisor; xData.add("Tax"); yData.add(i); zData.add(Tax);}
                        if(Health != null){j = Float.parseFloat(Health) / divisor; xData.add("Health"); yData.add(j); zData.add(Health);}
                        if(Sport != null){k = Float.parseFloat(Sport) / divisor; xData.add("Sport"); yData.add(k); zData.add(Sport);}
                        if(Kids != null){l = Float.parseFloat(Kids) / divisor; xData.add("Kids"); yData.add(l); zData.add(Kids);}
                        if(Pet != null){m = Float.parseFloat(Pet) / divisor; xData.add("Pet"); yData.add(m); zData.add(Pet);}
                        if(Beauty != null){n = Float.parseFloat(Beauty) / divisor; xData.add("Beauty"); yData.add(n); zData.add(Beauty);}
                        if(Electronics != null){o = Float.parseFloat(Electronics) / divisor; xData.add("Electronics"); yData.add(o); zData.add(Electronics);}
                        if(Gift != null){p = Float.parseFloat(Gift) / divisor; xData.add("Gift"); yData.add(p); zData.add(Gift);}
                        if(Social != null){q = Float.parseFloat(Social) / divisor; xData.add("Social"); yData.add(q); zData.add(Social);}
                        if(Travel != null){r = Float.parseFloat(Travel) / divisor; xData.add("Travel"); yData.add(r); zData.add(Travel);}
                        if(Education != null){s = Float.parseFloat(Education) / divisor; xData.add("Education"); yData.add(s); zData.add(Education);}
                        if(Office != null){t = Float.parseFloat(Office) / divisor; xData.add("Office"); yData.add(t); zData.add(Office);}
                        if(Other != null){u = Float.parseFloat(Other) / divisor; xData.add("Other"); yData.add(u); zData.add(Other);}

                        pieChart.setContentDescription("Yearly Expense");
                        pieChart.setRotationEnabled(true);
                        pieChart.setHoleRadius(0f);
                        pieChart.setTransparentCircleAlpha(0);
                        addDataSet(pieChart);

                        String textData = "";
                        for(int i=0;i<xData.size();i++){
                            textData = textData + xData.get(i) + ": " + currency + zData.get(i) + "\n";
                        }
                        textData = textData + "Total: " + currency + Total;
                        textView.setText(textData);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to display yearly pie chart. " + e.toString());
                    }
                });
    }

    private void displayPieChart2(final String year){
        String id = firebaseAuth.getCurrentUser().getUid();
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
                        zData.clear();

                        if(Total2 != null){total2 = Float.parseFloat(Total2); divisor2 = total2 / 100;}
                        if(Salary != null){a2 = Float.parseFloat(Salary) / divisor2; xData.add("Salary"); yData.add(a2);zData.add(Salary);}
                        if(Awards != null){b2 = Float.parseFloat(Awards) / divisor2; xData.add("Awards"); yData.add(b2); zData.add(Awards);}
                        if(Grants != null){c2 = Float.parseFloat(Grants) / divisor2; xData.add("Grants"); yData.add(c2); zData.add(Grants);}
                        if(Rental != null){d2 = Float.parseFloat(Rental) / divisor2; xData.add("Rental"); yData.add(d2); zData.add(Rental);}
                        if(Refunds != null){e2 = Float.parseFloat(Refunds) / divisor2; xData.add("Refunds"); yData.add(e2); zData.add(Refunds);}
                        if(Lottery != null){f2 = Float.parseFloat(Lottery) / divisor2; xData.add("Lottery"); yData.add(f2); zData.add(Lottery);}
                        if(Dividends != null){g2 = Float.parseFloat(Dividends) / divisor2; xData.add("Dividends"); yData.add(g2); zData.add(Dividends);}
                        if(Investments != null){h2 = Float.parseFloat(Investments) / divisor2; xData.add("Investments"); yData.add(h2); zData.add(Investments);}
                        if(Other2 != null){i2 = Float.parseFloat(Other2) / divisor2; xData.add("Other"); yData.add(i2); zData.add(Other2);}

                        pieChart2.setContentDescription("Yearly Income");
                        pieChart2.setRotationEnabled(true);
                        pieChart2.setHoleRadius(0f);
                        pieChart2.setTransparentCircleAlpha(0);
                        addDataSet(pieChart2);

                        String textData = "";
                        for(int i=0;i<xData.size();i++){
                            textData = textData + xData.get(i) + ": " + currency + zData.get(i) + "\n";
                        }
                        textData = textData + "Total: " + currency + Total2;
                        textView2.setText(textData);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to display yearly pie chart2. " + e.toString());
                    }
                });
    }

    private void addDataSet(PieChart pieChart){
        ArrayList<PieEntry> yEntry = new ArrayList<>();

        for(int i=0;i<yData.size();i++){
            yEntry.add(new PieEntry(yData.get(i), xData.get(i)));
        }

        PieDataSet pieDataSet = new PieDataSet(yEntry,"");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.BLUE);
        colors.add(Color.RED);
        colors.add(Color.GREEN);
        colors.add(Color.GRAY);
        colors.add(Color.MAGENTA);
        colors.add(Color.CYAN);
        colors.add(R.color.white);
        colors.add(R.color.lime);
        colors.add(R.color.olive);
        colors.add(R.color.purple);
        colors.add(R.color.aqua);
        colors.add(R.color.silver);
        colors.add(R.color.teal);
        colors.add(R.color.navy);
        colors.add(R.color.fuchsia);
        colors.add(R.color.maroon);
        colors.add(R.color.colorPreviousPrimary);
        colors.add(R.color.colorPreviousPrimaryDark);
        colors.add(R.color.colorAccent);
        colors.add(R.color.colorPrimary);
        colors.add(R.color.colorPrimaryDark);
        colors.add(Color.YELLOW);
        pieDataSet.setColors(colors);

        Legend legend = pieChart.getLegend();
        legend.setForm(Legend.LegendForm.CIRCLE);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void displayBarChart(){

        final String id = firebaseAuth.getCurrentUser().getUid();
        userDatabase.collection("users").document(id).collection("Expense").document("Total").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        e_2015 = documentSnapshot.getString("total-2015");
                        e_2016 = documentSnapshot.getString("total-2016");
                        e_2017 = documentSnapshot.getString("total-2017");
                        e_2018 = documentSnapshot.getString("total-2018");
                        e_2019 = documentSnapshot.getString("total-2019");
                        e_2020 = documentSnapshot.getString("total-2020");

                        userDatabase.collection("users").document(id).collection("Income").document("Total").get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        i_2015 = documentSnapshot.getString("total-2015");
                                        i_2016 = documentSnapshot.getString("total-2016");
                                        i_2017 = documentSnapshot.getString("total-2017");
                                        i_2018 = documentSnapshot.getString("total-2018");
                                        i_2019 = documentSnapshot.getString("total-2019");
                                        i_2020 = documentSnapshot.getString("total-2020");

                                        ArrayList<BarEntry> barEntries1 = new ArrayList<>();
                                        if(e_2020 != null){barEntries1.add(new BarEntry(6,Float.parseFloat(e_2020)));}
                                        else{barEntries1.add(new BarEntry(6,0));}
                                        if(e_2019 != null){barEntries1.add(new BarEntry(5,Float.parseFloat(e_2019)));}
                                        else{barEntries1.add(new BarEntry(5,0));}
                                        if(e_2018 != null){barEntries1.add(new BarEntry(4,Float.parseFloat(e_2018)));}
                                        else{barEntries1.add(new BarEntry(4,0));}
                                        if(e_2017 != null){barEntries1.add(new BarEntry(3,Float.parseFloat(e_2017)));}
                                        else{barEntries1.add(new BarEntry(3,0));}
                                        if(e_2016 != null){barEntries1.add(new BarEntry(2,Float.parseFloat(e_2016)));}
                                        else{barEntries1.add(new BarEntry(2,0));}
                                        if(e_2015 != null){barEntries1.add(new BarEntry(1,Float.parseFloat(e_2015)));}
                                        else{barEntries1.add(new BarEntry(1,0));}

                                        ArrayList<BarEntry> barEntries2 = new ArrayList<>();
                                        if(i_2020 != null){barEntries2.add(new BarEntry(6,Float.parseFloat(i_2020)));}
                                        else{barEntries2.add(new BarEntry(6,0));}
                                        if(i_2019 != null){barEntries2.add(new BarEntry(5,Float.parseFloat(i_2019)));}
                                        else{barEntries2.add(new BarEntry(5,0));}
                                        if(i_2018 != null){barEntries2.add(new BarEntry(4,Float.parseFloat(i_2018)));}
                                        else{barEntries2.add(new BarEntry(4,0));}
                                        if(i_2017 != null){barEntries2.add(new BarEntry(3,Float.parseFloat(i_2017)));}
                                        else{barEntries2.add(new BarEntry(3,0));}
                                        if(i_2016 != null){barEntries2.add(new BarEntry(2,Float.parseFloat(i_2016)));}
                                        else{barEntries2.add(new BarEntry(2,0));}
                                        if(i_2015 != null){barEntries2.add(new BarEntry(1,Float.parseFloat(i_2015)));}
                                        else{barEntries2.add(new BarEntry(1,0));}

                                        BarDataSet barDataSet1 = new BarDataSet(barEntries1,"Expense");
                                        barDataSet1.setColor(Color.RED);
                                        BarDataSet barDataSet2 = new BarDataSet(barEntries2,"Income");
                                        barDataSet2.setColor(Color.BLUE);

                                        BarData data = new BarData(barDataSet1,barDataSet2);
                                        barChart.setData(data);

                                        String[] months = new String[]{"2020","2019","2018","2017","2016","2015"};
                                        XAxis xAxis = barChart.getXAxis();
                                        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
                                        xAxis.setCenterAxisLabels(true);
                                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                        xAxis.setGranularity(1);
                                        xAxis.setGranularityEnabled(true);

                                        barChart.setDragEnabled(true);
                                        barChart.setVisibleXRangeMaximum(3);
                                        float barSpace = 0.1f;
                                        float groupSpace = 0.5f;
                                        data.setBarWidth(0.15f);
                                        barChart.getXAxis().setAxisMinimum(0);
                                        barChart.getXAxis().setAxisMaximum(0+barChart.getBarData().getGroupWidth(groupSpace,barSpace)*6);
                                        barChart.getAxisLeft().setAxisMinimum(0);
                                        barChart.groupBars(0,groupSpace,barSpace);
                                        barChart.invalidate();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG,"Failed to display yearly income data for bar chart. " + e.toString());
                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Failed to display yearly expense data for bar chart. " + e.toString());
                    }
                });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
