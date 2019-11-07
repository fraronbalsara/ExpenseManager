package com.example.expensemanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG ="MainActivity";

    //View Declaration
    View headerView;
    TextView textview1, textview2;
    ImageView imageview1;

    //Firebase
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore userDatabase = FirebaseFirestore.getInstance();
    private FirebaseUser user = firebaseAuth.getCurrentUser();

    //NavigationDrawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //NavigationDrawer
        mDrawerLayout = findViewById(R.id.drawerlayout);
        mToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.open,R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Navigation View
        NavigationView navigationView = findViewById(R.id.navigationview);
        navigationView.setNavigationItemSelectedListener(this);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new home()).commit();
            navigationView.setCheckedItem(R.id.home);
        }

        //Views in Navigation Drawer Header
        headerView = navigationView.getHeaderView(0);
        textview1 = headerView.findViewById(R.id.textview1);
        textview2 = headerView.findViewById(R.id.textview2);
        imageview1 = headerView.findViewById(R.id.imageview1);

        //checking if any user is currently signed in
        if(user == null){
            //if no user is signed in, redirecting to login
            Intent i = new Intent(MainActivity.this, login.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
            finish();
        }
        else{
            //checking if user has verified his email
            if (!user.isEmailVerified()) {
                //if user has not verified email, redirecting to email verification page
                Intent i = new Intent(MainActivity.this, EmailVerification.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }
            else {
                //network state listener
                networkStateListener();

                //user details in nav header
                String id = firebaseAuth.getCurrentUser().getUid();
                userDatabase.collection("users").document(id).get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String fname = documentSnapshot.getString("fname");
                                String lname = documentSnapshot.getString("lname");
                                String full_name = fname + " " + lname;
                                String email = documentSnapshot.getString("email");
                                String downloadImage = documentSnapshot.getString("downloadImage");
                                Uri profile_pic = Uri.parse(downloadImage);
                                textview1.setText(full_name);
                                textview2.setText(email);
                                Picasso.with(MainActivity.this).load(profile_pic).into(imageview1);
                            }
                        });
            }
        }
    }

    //Defining logout using menu inflater in action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //NavigationDrawer Toggle on/off
        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        //Logout button
        int id = item.getItemId();
        if(id == R.id.logout){
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setMessage("Are you sure you want to log out?");
            ad.setPositiveButton("Log out", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    firebaseAuth.signOut();
                    Intent i = new Intent(MainActivity.this,login.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                    finish();
                }
            });
            ad.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Returns without doing anything
                }
            });
            ad.show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch(menuItem.getItemId()){
            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new home()).commit();
                break;
            case R.id.recentdata:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new recentdata()).commit();
                break;
            case R.id.analysis:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new analysis()).commit();
                break;
            case R.id.settings:
                Intent i = new Intent(MainActivity.this,settings.class);
                startActivity(i);
                break;
            case R.id.aboutus:
                AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
                ad.setMessage("v1.0 \nCreated By: Fraron Balsara \n\n\nAll data is stored in Firebase secure servers. Authentication is handled by Firebase with no third party interference.");
                ad.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                ad.show();
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Hey! I found this really cool Expense Manager and I think you should try it out too. \n\nhttps://www.dropbox.com/sh/u0i0duja7og25sj/AAD0WPgaH8wUNLVA7qNCvsoYa?dl=0";
                sharingIntent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(Intent.createChooser(sharingIntent,"Share using"));
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        String id = firebaseAuth.getCurrentUser().getUid();
        userDatabase.collection("users").document(id).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String fname = documentSnapshot.getString("fname");
                        String lname = documentSnapshot.getString("lname");
                        String full_name = fname + " " + lname;
                        String email = documentSnapshot.getString("email");
                        String downloadImage = documentSnapshot.getString("downloadImage");
                        Uri profile_pic = Uri.parse(downloadImage);
                        textview1.setText(full_name);
                        textview2.setText(email);
                        Picasso.with(MainActivity.this).load(profile_pic).into(imageview1);
                    }
                });
    }

    public void networkStateListener(){
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
        if(connected){}
        else {
            AlertDialog.Builder ad = new AlertDialog.Builder(MainActivity.this);
            ad.setMessage("No network detected. \nPlease connect to a network and try again.");
            ad.setCancelable(false);
            ad.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    networkStateListener();
                }
            });
            ad.show();
        }
    }
}
