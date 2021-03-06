package com.floryt.app;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.firebase.ui.auth.AuthUI;
import com.floryt.app.fragments.AboutFragment;
import com.floryt.app.fragments.AccountFragment;
import com.floryt.app.fragments.ActivityLogFragment;
import com.floryt.app.fragments.ComputersActivityLogFragment;
import com.floryt.app.fragments.ContactUsFragment;
import com.floryt.app.fragments.DashboardFragment;
import com.floryt.app.fragments.HelpFragment;
import com.floryt.app.fragments.MyComputersFragment;
import com.floryt.app.fragments.NotificationFragment;
import com.floryt.app.fragments.ShareUsFragment;
import com.floryt.app.fragments.SharedComputersFragment;
import com.floryt.common.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MainActivityLog";
    NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setContent(DashboardFragment.getInstance());
    }

    @Override
    public void onStart() {
        assert FirebaseAuth.getInstance().getCurrentUser() != null;
        Common.saveToken();
        setUserHeader(FirebaseAuth.getInstance().getCurrentUser());
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            setContent(DashboardFragment.getInstance());
//            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.nav_dashboard:
                setContent(DashboardFragment.getInstance());
                break;
            case R.id.nav_activity_log:
                setContent(ActivityLogFragment.getInstance());
                break;
            case R.id.nav_computers_activity_log:
                setContent(ComputersActivityLogFragment.getInstance());
                break;
            case R.id.nav_my_computers:
                setContent(MyComputersFragment.getInstance());
                break;
            case R.id.nav_shared_computers:
                setContent(SharedComputersFragment.getInstance());
                break;
            case R.id.nav_account:
                setContent(AccountFragment.getInstance());
                break;
            case R.id.nav_notification:
                setContent(NotificationFragment.getInstance());
                break;
            case R.id.nav_help:
                setContent(HelpFragment.getInstance());
                break;
            case R.id.nav_contact_us:
                setContent(ContactUsFragment.getInstance());
                break;
            case R.id.nav_share_us:
                setContent(ShareUsFragment.getInstance());
                break;
            case R.id.nav_about:
                setContent(AboutFragment.getInstance());
                break;
            case R.id.nav_demo_owner:
                startDemo(true);
                break;
            case R.id.nav_demo_guest:
                startDemo(false);
                break;
            case R.id.sign_out_button:
                signOut();
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startDemo(boolean isAdmin) {
        HashMap<String, String> data = new HashMap<>();

        Intent intent;
        if (isAdmin) {
            intent = new Intent(this, IdentityVerificationActivity.class);
            data.put("computerName", "Steven's Computer");
            data.put("computerIp", "8.8.8.8");
            data.put("deadline", String.valueOf((System.currentTimeMillis() / 1000) + 120));
            data.put("computerLatitude", String.valueOf(31.782770));
            data.put("computerLongitude", String.valueOf(34.640800));
            data.put("verificationUid", null);
            intent.putExtra("data", data);
            startActivity(intent);
        } else {
            intent = new Intent(this, PermissionRequestActivity.class);
            data = new HashMap<>();
            data.put("guestPhotoUrl", String.valueOf(Common.getCurrentUser().getPhotoUrl()));
            data.put("guestName", String.valueOf(Common.getCurrentUser().getDisplayName()));
            data.put("guestEmail", String.valueOf(Common.getCurrentUser().getEmail()));
            data.put("computerName", "Steven's Computer");
            data.put("computerIp", "8.8.8.8");
            data.put("deadline", String.valueOf((System.currentTimeMillis() / 1000) + 120));
            data.put("permissionUid", null);
            intent.putExtra("data", data);
            startActivity(intent);
        }
    }

    private void setUserHeader(FirebaseUser currentUser) {
        View header = navigationView.getHeaderView(0);

        final ImageView userImage = (ImageView) header.findViewById(R.id.user_icon);
        TextView userDisplayName = (TextView) header.findViewById(R.id.user_name);
        TextView userEmail = (TextView) header.findViewById(R.id.user_email);

        Glide.with(this)
                .load(currentUser.getPhotoUrl())
                .asBitmap()
                .centerCrop()
                .into(new BitmapImageViewTarget(userImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getApplicationContext().getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        userImage.setImageDrawable(circularBitmapDrawable);
                    }
                });
        userDisplayName.setText(currentUser.getDisplayName());
        userEmail.setText(currentUser.getEmail());
    }

    private void setContent(Fragment fragment) {
        getFragmentManager().beginTransaction().replace(R.id.content, fragment).commit();
    }

    private void signOut() {
        Common.removeToken();

        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            finish();
                        } else {
                            showSnackbar(R.string.sign_out_failed);
                        }
                    }
                });
    }

    @MainThread
    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(findViewById(R.id.drawer_layout), errorMessageRes, Snackbar.LENGTH_LONG)
                .show();
    }
}
