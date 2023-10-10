package com.technifysoft.docstudent;



import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.technifysoft.docstudent.databinding.ActivityDashboardBinding;



public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //view binding
    private ActivityDashboardBinding binding ;

    //firebase auth
    private FirebaseAuth firebaseAuth;
    private DrawerLayout drawer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeActivity()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }



        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new HomeActivity()).commit();
                break;
            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new ProfileActivity()).commit();
                break;
            case R.id.nav_add:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new AddActivity()).commit();
                break;
            case R.id.nav_show:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new DocsActivity()).commit();
                break;

            case R.id.nav_network:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        new network()).commit();
                break;

            case R.id.nav_logout:
                firebaseAuth.signOut();
                checkUser();
                break;

            default:
                return false;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private ValueEventListener userListener;

    private void checkUser() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser == null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

            // Add a value event listener to listen for changes in the database
            userListener = userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Get user info from the snapshot
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("Email").getValue(String.class);
                    String imgUrl = snapshot.child("profileImage").getValue(String.class);

                    // Update the UI
                    NavigationView navigationView = findViewById(R.id.nav_view);
                    View headerView = navigationView.getHeaderView(0);
                    TextView nameView = headerView.findViewById(R.id.userTV);
                    TextView emailView = headerView.findViewById(R.id.userEmailTV);
                    ImageView profileImg = headerView.findViewById(R.id.profileIV);

                    if (name!= null && nameView!= null) {
                        nameView.setText(name);
                    }
                    if ( email != null && emailView!= null) {
                        emailView.setText(email);
                    }
                    if (imgUrl != null && profileImg!= null){
                        Glide.with(getBaseContext())
                                .load(imgUrl)
                                .placeholder(R.drawable.placeholder_image)
                                .error(R.drawable.default_image)
                                .into(profileImg);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle error
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start listening for changes in the database
        if (userListener != null && FirebaseAuth.getInstance().getCurrentUser().getUid()!= null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            userRef.addValueEventListener(userListener);
        }
    }



}