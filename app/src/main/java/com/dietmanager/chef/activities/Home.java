package com.dietmanager.chef.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.chef.adapter.ViewPagerAdapter;
import com.dietmanager.chef.fragment.CancelOrderFragment;
import com.dietmanager.chef.fragment.PastVisitFragment;
import com.dietmanager.chef.fragment.UpcomingVisitFragment;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.dietmanager.chef.R;
import com.dietmanager.chef.adapter.TaskAdapter;
import com.dietmanager.chef.api.APIError;
import com.dietmanager.chef.api.ErrorUtils;
import com.dietmanager.chef.helper.ConnectionHelper;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.LocaleUtils;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.Message;
import com.dietmanager.chef.model.Order;
import com.dietmanager.chef.model.Profile;
import com.dietmanager.chef.model.Shift;
import com.dietmanager.chef.receiver.NetworkChangeReceiver;
import com.dietmanager.chef.service.GPSTrackerService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;
import static com.dietmanager.chef.receiver.NetworkChangeReceiver.IS_NETWORK_AVAILABLE;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static CustomDialog customDialog;
    @BindView(R.id.new_task_rv)
    RecyclerView newTaskRv;

    TaskAdapter newTaskAdapter;
    List<Order> orders;
    TaskAdapter completedTaskAdapter;
    List<Order> completedOrders;
    Activity activity = this;
    Handler handler;
    Runnable runnable;
    ConnectionHelper connectionHelper;
    public static LinearLayout errorLayout;
    @BindView(R.id.error_message)
    TextView errorMessage;
    @BindView(R.id.error_img)
    ImageView errorImg;
    @BindView(R.id.new_task_label)
    TextView newTaskLabel;
    @BindView(R.id.completed_task_label)
    TextView completedTaskLabel;
    @BindView(R.id.completed_task_rv)
    RecyclerView completedTaskRv;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    CircleImageView userAvatar;
    TextView name;
    TextView userId;
    @BindView(R.id.completed_task_rootview)
    LinearLayout completedTaskRootview;
    @BindView(R.id.internet_error_layout)
    LinearLayout internetErrorLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private SkeletonScreen skeletonScreen;
    String device_token, device_UDID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        getDeviceToken();

        Toolbar toolbar = findViewById(R.id.toolbar);
        errorLayout = findViewById(R.id.error_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        String dd = SharedHelper.getKey(this, "language");
        switch (dd) {
            case "English":
                LocaleUtils.setLocale(this, "en");
                break;
            case "Japanese":
                LocaleUtils.setLocale(this, "ja");
                break;
            default:
                LocaleUtils.setLocale(this, "en");
                break;
        }

        getSupportActionBar().setTitle(getResources().getString(R.string.live_tasks));
        customDialog = new CustomDialog(this);
        connectionHelper = new ConnectionHelper(this);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, Home.this.getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        toggle.syncState();

        boolean serviceRunningStatus = isServiceRunning(GPSTrackerService.class);

        if (serviceRunningStatus) {
            Intent serviceIntent = new Intent(activity, GPSTrackerService.class);
            stopService(serviceIntent);
        }
        if (!serviceRunningStatus) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                startService(new Intent(activity, GPSTrackerService.class));
            } else {
                Intent serviceIntent = new Intent(activity, GPSTrackerService.class);
                ContextCompat.startForegroundService(activity, serviceIntent);
            }
        }

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //View headerView = navigationView.inflateHeaderView(R.layout.nav_header_home);
        userAvatar = navigationView.getHeaderView(0).findViewById(R.id.user_avatar);
        LinearLayout nav_header = navigationView.getHeaderView(0).findViewById(R.id.nav_header);
        nav_header.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Home.this, ProfileActivity.class));
            }
        });
        name = navigationView.getHeaderView(0).findViewById(R.id.name);
        userId = navigationView.getHeaderView(0).findViewById(R.id.user_id);
        navigationView.setNavigationItemSelectedListener(this);

/*        orders = new ArrayList<>();
        newTaskAdapter = new TaskAdapter(orders, this, true);
        newTaskRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        newTaskRv.setItemAnimator(new DefaultItemAnimator());
        newTaskRv.setAdapter(newTaskAdapter);

        completedOrders = new ArrayList<>();
        completedTaskAdapter = new TaskAdapter(completedOrders, this, false);
        completedTaskRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        //completedTaskRv.setItemAnimator(new DefaultItemAnimator());
        completedTaskRv.setAdapter(completedTaskAdapter);

//        startService(new Intent(this, GPSTracker.class));



        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value1 = extras.getString("Notification");
            if (value1 != null) {
                getProfile();
                getOrder();
            }
        }*/
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
            }
        };
        IntentFilter intentFilter = new IntentFilter(getApplication().getPackageName());
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "We are back !!!" : "Could not Connect to internet";
                if (!isNetworkAvailable) {
                    internetErrorLayout.setVisibility(View.VISIBLE);
                } else {
                    internetErrorLayout.setVisibility(View.GONE);
                    onResume();
                }
            }
        }, intentFilter);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new UpcomingVisitFragment(), getString(R.string.ongoing_order));
        adapter.addFragment(new PastVisitFragment(), getString(R.string.past_order));
        adapter.addFragment(new CancelOrderFragment(), getString(R.string.cancelled_order));
        viewPager.setAdapter(adapter);
        //set ViewPager
        tabLayout.setupWithViewPager(viewPager);
        changeTabsFont();
        viewPager.setOffscreenPageLimit(3);
        customDialog = new CustomDialog(this);

    }

    public static void showDialog() {
        if (customDialog != null && !customDialog.isShowing()) {
            customDialog.setCancelable(false);
            customDialog.show();
        }
    }
    private void changeTabsFont() {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    Typeface custom_font = ResourcesCompat.getFont(getApplicationContext(), R.font.nunito_semibold);
                    ((TextView) tabViewChild).setTypeface(custom_font);
                }
            }
        }
    }
    public static void dismissDialog() {
        if (customDialog != null & customDialog.isShowing())
            customDialog.dismiss();
    }
    @Override
    public void onResume() {
        super.onResume();
        initProfileView();
/*        orders.clear();
        newTaskAdapter.notifyDataSetChanged();
        completedOrders.clear();
        completedTaskAdapter.notifyDataSetChanged();*/
        /*if (GlobalData.shift != null) {
            getOrder();
            getCompletedOrder();
        } else getShift();
        getProfile();*/

        //errorLayout.setVisibility(View.GONE);
        //newTaskRv.setVisibility(View.VISIBLE);




        getNetworkStatus();
    }

    private boolean getNetworkStatus() {
        if (NetworkChangeReceiver.isConnectedToInternet(getApplicationContext())) {
            internetErrorLayout.animate().alpha(0.0f).setDuration(1000);
            internetErrorLayout.setVisibility(View.GONE);
            return true;
        } else {
            internetErrorLayout.animate().alpha(1.0f).setDuration(1000);
            internetErrorLayout.setVisibility(View.VISIBLE);
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void getShift() {

        if (!getNetworkStatus())
            return;

        String header = SharedHelper.getKey(Home.this, "token_type") + " " + SharedHelper.getKey(Home.this, "access_token");
        System.out.println(header);
        Call<List<Shift>> call = GlobalData.api.getShift(header);
        call.enqueue(new Callback<List<Shift>>() {
            @Override
            public void onResponse(@NonNull Call<List<Shift>> call, @NonNull Response<List<Shift>> response) {
                if (response.isSuccessful()) {
                    List<Shift> list = response.body();
                    if (list != null && list.size() > 0) {
                        GlobalData.shift = list.get(0);
                        getOrder();
                    } else {
                        /*startActivity(new Intent(Home.this, ShiftStatus.class));
                        finish();*/
                        errorImg.setImageResource(R.drawable.purchase);
                        errorMessage.setText(getString(R.string.please_start_shift));
                        errorLayout.setVisibility(View.VISIBLE);
                        handler.removeCallbacks(runnable);
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Home.this, error.getError(), Toast.LENGTH_SHORT).show();
                    if (response.code() == 401) {
                        SharedHelper.putKey(Home.this, "logged_in", "0");
                        startActivity(new Intent(Home.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Shift>> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, "Something wrong - getShift", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getOrder() {

        if (!getNetworkStatus())
            return;

        String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this, "access_token");
        Call<List<Order>> call = GlobalData.api.getOrder(header);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                Log.d("getOrder", response.toString());
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        orders.clear();
                        orders.addAll(response.body());
                        newTaskAdapter.notifyDataSetChanged();
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 5000);
                        errorLayout.setVisibility(View.GONE);
                    } else {
                        orders.clear();
                        handler.removeCallbacks(runnable);
                        handler.postDelayed(runnable, 5000);
                        errorLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    if (response.code() == 401) {
                        SharedHelper.putKey(Home.this, "logged_in", "0");
                        startActivity(new Intent(Home.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, "Something wrong.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getProfile() {
        if (!getNetworkStatus()) {
            return;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("device_type", "android");
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this, "access_token");
        System.out.println("getProfile Header " + header);
        Call<Profile> call = GlobalData.api.getProfile(header, map);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                if (response.isSuccessful()) {
                    GlobalData.profile = response.body();
                    setErrorLayout();
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Home.this, error.getError(), Toast.LENGTH_SHORT).show();
                    if (response.code() == 401) {
                        SharedHelper.putKey(Home.this, "logged_in", "0");
                        startActivity(new Intent(Home.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Toast.makeText(Home.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getCompletedOrder() {

        if (!getNetworkStatus())
            return;

        if (skeletonScreen == null) {
            skeletonScreen = Skeleton.bind(completedTaskRootview)
                    .load(R.layout.skeloton_list_item_new_task)
                    .angle(0)
                    .show();
        } else {
            skeletonScreen.hide();
            skeletonScreen.show();
        }

        String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this, "access_token");
        Call<List<Order>> call = GlobalData.api.getCompletedOrder(header, "today");
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                Log.d("getCompletedOrder", response.toString());
                skeletonScreen.hide();
                if (response.isSuccessful()) {
                    if (response.body().size() > 0) {
                        completedOrders.clear();
                        completedOrders.addAll(response.body());
                        completedTaskAdapter.notifyDataSetChanged();
                        completedTaskLabel.setText(Home.this.getResources().getString(R.string.completed_tasks));
                        completedTaskLabel.setVisibility(View.VISIBLE);
                    } else {
                        completedOrders.clear();
                        completedTaskAdapter.notifyDataSetChanged();
                        completedTaskLabel.setVisibility(View.GONE);
                        completedTaskLabel.setText("");
                    }

                } else {
                    skeletonScreen.hide();
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Home.this, error.getError(), Toast.LENGTH_SHORT).show();
                    if (response.code() == 401) {
                        SharedHelper.putKey(Home.this, "logged_in", "0");
                        startActivity(new Intent(Home.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                skeletonScreen.hide();
//                Toast.makeText(Home.this, "Something wrong - getCompletedOrder", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initProfileView() {
        if (GlobalData.profile != null) {
            name.setText(GlobalData.profile.getName());
            userId.setText(String.valueOf(GlobalData.profile.getId()));
            Glide.with(this)
                    .load(GlobalData.profile.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.man)
                            .error(R.drawable.man))
                    .into(userAvatar);
        }
    }

    private void setErrorLayout() {
        if (GlobalData.profile != null) {
            String value = GlobalData.profile.getStatus();
            System.out.println(value);
            switch (value) {
                case "online":
                    if (orders.size() <= 0) errorLayout.setVisibility(View.VISIBLE);
                    errorImg.setImageResource(R.drawable.bg_waiting);
                    errorMessage.setText(Home.this.getResources().getString(R.string.waiting_for_new_task));
                    //navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
                    break;
                case "unsettled":
                    errorImg.setImageResource(R.drawable.coins);
                    errorMessage.setText(Home.this.getResources().getString(R.string.please_settle_the_amount_to_the_respective_restaurant));
                    errorLayout.setVisibility(View.VISIBLE);
                    //navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                    break;
                case "offline":
                    errorImg.setImageResource(R.drawable.purchase);
                    errorMessage.setText(Home.this.getResources().getString(R.string.turn_on_start_shift));
                    errorLayout.setVisibility(View.VISIBLE);
                    handler.removeCallbacks(runnable);
                    /*if (GlobalData.shift != null) {
                        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
                    } else {
                        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
                    }*/
                    break;
                default:
                    break;
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            startActivity(new Intent(Home.this, ProfileActivity.class));
        } else if (id == R.id.nav_shift_status) {
            startActivity(new Intent(Home.this, ShiftStatus.class));
        } else if (id == R.id.nav_notice_board) {
            startActivity(new Intent(Home.this, NoticeBoard.class));
        } else if (id == R.id.nav_order_history) {
            startActivity(new Intent(Home.this, OrderRequestActivity.class));
        } else if (id == R.id.nav_home) {
            onBackPressed();
        } else if (id == R.id.nav_order_request) {
            startActivity(new Intent(Home.this, OrderRequestActivity.class));
        } else if (id == R.id.nav_earning) {
        } else if (id == R.id.nav_terms_conditions) {
            startActivity(new Intent(Home.this, TermsAndConditions.class));
        } else if (id == R.id.nav_language) {
            changeLanguage();
        } else if (id == R.id.nav_logout) {
            logout();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void changeLanguage() {

        List<String> languages = Arrays.asList(Home.this.getResources().getStringArray(R.array.languages));
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.language_dialog, null);
        alertDialog.setView(convertView);
        alertDialog.setCancelable(true);
        alertDialog.setTitle(getResources().getString(R.string.change_language));
        final android.app.AlertDialog alert = alertDialog.create();
        final RadioGroup chooseLanguage = convertView.findViewById(R.id.choose_language);
        final RadioButton english = convertView.findViewById(R.id.english);
        final RadioButton japnese = convertView.findViewById(R.id.japnese);

        String dd = LocaleUtils.getLanguage(this);
        switch (dd) {
            case "en":
                english.setChecked(true);
                break;
            case "ja":
                japnese.setChecked(true);
                break;
            default:
                english.setChecked(true);
                break;
        }

        chooseLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.english:
                        setLanguage("English");
                        alert.dismiss();
                        break;
                    case R.id.japnese:
                        setLanguage("Japanese");
                        alert.dismiss();
                        break;

                }
            }
        });
        alert.show();
    }

    public void getDeviceToken() {
        String TAG = "FCM";
        try {
            if (SharedHelper.getKey(this, "device_token").equals("") && SharedHelper.getKey(this, "device_token") == null) {
                SharedHelper.putKey(this, "device_token", FirebaseInstanceId.getInstance().getToken());
                Log.d(TAG, "Failed to complete token refresh: " + FirebaseInstanceId.getInstance().getToken());
            }
        } catch (Exception e) {
            Log.d(TAG, "COULD NOT GET FCM TOKEN");
        }

        try {
            String device_UDID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
            SharedHelper.putKey(this, "device_id", device_UDID);
            Log.d(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "COULD NOT GET UDID");
        }
    }

    private void logout() {
        if (customDialog != null)
            customDialog.show();

        String header = SharedHelper.getKey(Home.this, "token_type") + " " + SharedHelper.getKey(Home.this, "access_token");
        Call<Message> call = GlobalData.api.logout(header);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                customDialog.cancel();
                if (response.isSuccessful()) {
                    Toast.makeText(Home.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    SharedHelper.putKey(Home.this, "logged_in", "0");
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(Home.this, error.getError(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                customDialog.cancel();
                Toast.makeText(Home.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setLanguage(String value) {
        SharedHelper.putKey(Home.this, "language", value);
        switch (value) {
            case "English":
                LocaleUtils.setLocale(this, "en");
                recreate();
                break;
            case "Japanese":
                LocaleUtils.setLocale(this, "ja");
                recreate();
                break;
            default:
                LocaleUtils.setLocale(this, "en");
                recreate();
                break;
        }
    }

    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @OnClick(R.id.internet_error_layout)
    public void onViewClicked() {
        getNetworkStatus();
    }

    public void incommingReq(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            final FrameLayout frameView = new FrameLayout(Home.this);
            builder.setView(frameView);
            final AlertDialog rateDialog = builder.create();
            rateDialog.setCancelable(false);
            LayoutInflater inflater = rateDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.incoming_popup, frameView);
            Button acceptBtn = dialogView.findViewById(R.id.accept_btn);
            Button rejectBtn = dialogView.findViewById(R.id.reject_btn);
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    orderDetailsAlert();
                        rateDialog.dismiss();

                }
            });
            rejectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        rateDialog.dismiss();

                }
            });
            rateDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void orderDetailsAlert(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            final FrameLayout frameView = new FrameLayout(Home.this);
            builder.setView(frameView);
            final AlertDialog orderDetailsDialog = builder.create();
            orderDetailsDialog.setCancelable(false);
            LayoutInflater inflater = orderDetailsDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.orderdetails_popup, frameView);
            Button purchaseBtn = dialogView.findViewById(R.id.purchase_btn);
            purchaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchasedAlert();
                    orderDetailsDialog.dismiss();

                }
            });
            orderDetailsDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void purchasedAlert(){
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
            final FrameLayout frameView = new FrameLayout(Home.this);
            builder.setView(frameView);
            final AlertDialog purchasedDialog = builder.create();
            purchasedDialog.setCancelable(false);
            LayoutInflater inflater = purchasedDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.purchased_popup, frameView);
            Button purchasedBtn = dialogView.findViewById(R.id.purchased_btn);
            purchasedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchasedDialog.dismiss();

                }
            });
            purchasedDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
