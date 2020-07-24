package com.oyola.deliveryboy.activities;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chaos.view.PinView;
import com.oyola.deliveryboy.Application;
import com.oyola.deliveryboy.R;
import com.oyola.deliveryboy.adapter.ProductAdapter1;
import com.oyola.deliveryboy.api.APIError;
import com.oyola.deliveryboy.api.ErrorUtils;
import com.oyola.deliveryboy.helper.ConnectionHelper;
import com.oyola.deliveryboy.helper.CustomDialog;
import com.oyola.deliveryboy.helper.GlobalData;
import com.oyola.deliveryboy.helper.OnlySeekableSeekBar;
import com.oyola.deliveryboy.helper.SharedHelper;
import com.oyola.deliveryboy.model.Address;
import com.oyola.deliveryboy.model.Invoice;
import com.oyola.deliveryboy.model.Item;
import com.oyola.deliveryboy.model.Message;
import com.oyola.deliveryboy.model.Order;
import com.oyola.deliveryboy.model.Ordertiming;
import com.oyola.deliveryboy.model.Shop;
import com.oyola.deliveryboy.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFlow extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    public static int rating = 5;
    @BindView(R.id.time_left)
    TextView timeLeft;
    @BindView(R.id.shop_avatar)
    ImageView shopAvatar;
    @BindView(R.id.shop_name)
    TextView shopName;
    @BindView(R.id.shop_distance)
    TextView shopDistance;
    @BindView(R.id.shop_address)
    TextView shopAddress;
    @BindView(R.id.shop_call)
    ImageView shopCall;
    @BindView(R.id.shop_navigation)
    ImageView shopNavigation;
    @BindView(R.id.order_id)
    TextView orderId;
    @BindView(R.id.order_created_at)
    TextView orderCreatedAt;
    @BindView(R.id.product_items_rv)
    RecyclerView productItemsRv;
    RadioGroup rateRadioGroup;
    CustomDialog customDialog;
    ProductAdapter1 productAdapter1;
    List<Item> items;
    ConnectionHelper connectionHelper;
    HashMap<String, String> map = new HashMap<>();
    @BindView(R.id.item_total)
    TextView itemTotal;
    @BindView(R.id.service_tax)
    TextView serviceTax;
    @BindView(R.id.delivery_charges)
    TextView deliveryCharges;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.shop_details_layout)
    RelativeLayout shopDetailsLayout;
    @BindView(R.id.user_avatar)
    ImageView userAvatar;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_distance)
    TextView userDistance;
    @BindView(R.id.user_address)
    TextView userAddress;
    @BindView(R.id.user_call)
    ImageView userCall;
    @BindView(R.id.user_navigation)
    ImageView userNavigation;
    @BindView(R.id.user_details_layout)
    RelativeLayout userDetailsLayout;
    @BindView(R.id.icon_started_towards_restaurant)
    ImageView iconStartedTowardsRestaurant;
    @BindView(R.id.icon_reached_restaurant)
    ImageView iconReachedRestaurant;
    @BindView(R.id.icon_order_picked_up)
    ImageView iconOrderPickedUp;
    @BindView(R.id.icon_order_delivered)
    ImageView iconOrderDelivered;
    @BindView(R.id.icon_payment_received)
    ImageView iconPaymentReceived;
    @BindView(R.id.user_name_ll)
    LinearLayout userNameLl;
    @BindView(R.id.promocodeLayout)
    LinearLayout promocodeLayout;
    @BindView(R.id.discount)
    TextView discount;
    @BindView(R.id.promocode_amount)
    TextView promocodeAmount;
    @BindView(R.id.payment_mode)
    TextView paymentMode;
    String numberFormat;
    @BindView(R.id.shop_name_ll)
    LinearLayout shopNameLl;
    @BindView(R.id.shift_status)
    OnlySeekableSeekBar shiftStatus;
    @BindView(R.id.status_label)
    TextView statusLabel;
    @BindView(R.id.shift_status_layout)
    RelativeLayout shiftStatusLayout;
    boolean paymentOnce = true;
    Handler handler;
    Runnable runnable;
    String previousStatus = "";
    AlertDialog alertDialog;
    @BindView(R.id.wallet_detection)
    TextView walletDetection;
    private String isPinView;
    private double bal = 0;
    private double mRoundedAmt = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_flow);
        ButterKnife.bind(this);
        numberFormat = Application.getNumberFormat();
        Toolbar toolbar = findViewById(R.id.toolbar);
        assert toolbar != null;
        toolbar.setTitle(getString(R.string.live_tasks));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        connectionHelper = new ConnectionHelper(this);
        customDialog = new CustomDialog(this);

        items = new ArrayList<>();
        productAdapter1 = new ProductAdapter1(items, this);
        productItemsRv.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        productItemsRv.setItemAnimator(new DefaultItemAnimator());
        productItemsRv.setAdapter(productAdapter1);

        shiftStatus.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 50) {
                    seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.MULTIPLY);
                } else {
                    seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorGray),
                            PorterDuff.Mode.MULTIPLY);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (seekBar.getProgress() > 95) {
                    updateStatus();
                    seekBar.setProgress(4);
                } else {
                    seekBar.setProgress(4);
                }
            }
        });
        initOrder();
    }

    private void initOrder() {
        if (GlobalData.shift == null) {
            startActivity(new Intent(this, ShiftStatus.class));
            finish();
            return;
        }
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                getOrder();
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);
        if (GlobalData.order != null && GlobalData.order.getInvoice() != null) {
            initShop();
            initUser();
            Invoice invoice = GlobalData.order.getInvoice();
            orderId.setText(getString(R.string.order_message, invoice.getOrderId()));
            orderCreatedAt.setText(GlobalData.getTimeFromString(invoice.getCreatedAt()));
            itemTotal.setText(numberFormat + invoice.getGross());
            serviceTax.setText(numberFormat + invoice.getTax());
            deliveryCharges.setText(numberFormat + invoice.getDeliveryCharge());
            discount.setText(numberFormat + invoice.getDiscount());
            if (invoice.getPromocode_amount() > 0) {
                promocodeLayout.setVisibility(View.VISIBLE);
                promocodeAmount.setText(numberFormat + invoice.getPromocode_amount());
            } else {
                promocodeLayout.setVisibility(View.GONE);
            }
            walletDetection.setText(numberFormat + invoice.getWalletAmount());
            total.setText(numberFormat + invoice.getPayable());
            if (invoice.getPaymentMode().equalsIgnoreCase("stripe")) {
                paymentMode.setText(getString(R.string.payment_mode) + "Card");
            } else {
                paymentMode.setText(getString(R.string.payment_mode) + invoice.getPaymentMode());
            }

            items.clear();
            items.addAll(GlobalData.order.getItems());
            productAdapter1.notifyDataSetChanged();
            previousStatus = GlobalData.order.getStatus();
            updateFlowUI(GlobalData.order.getStatus());
        } else {
            getOrder();
        }
    }

    private void getOrder() {
        if (!connectionHelper.isConnectingToInternet())
            return;
        String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this,
                "access_token");
        Call<List<Order>> call = GlobalData.api.getOrder(header);
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call,
                                   @NonNull Response<List<Order>> response) {
                if (response.isSuccessful()) {
                    Log.i("getOrder", response.body().toString());
                    if (response.body() != null && response.body().size() > 0) {
                        GlobalData.order = response.body().get(0);
                        if (!previousStatus.equalsIgnoreCase(GlobalData.order.getStatus())) {
                            previousStatus = GlobalData.order.getStatus();
                            updateFlowUI(GlobalData.order.getStatus());
                        }
//                        handler.removeCallbacks(runnable);
                        /*handler.postDelayed(runnable, 3000);*/
                    } else {
                        //If order status is completed then show feed back
                        if (!previousStatus.equalsIgnoreCase("COMPLETED") && !previousStatus.equalsIgnoreCase("ARRIVED") && !previousStatus.equalsIgnoreCase("PICKEDUP")) {
                            alertDialog();
                        }
                    }
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(ServiceFlow.this, error.getError(), Toast.LENGTH_SHORT).show();
                    if (response.code() == 401) {
                        SharedHelper.putKey(ServiceFlow.this, "logged_in", "0");
                        startActivity(new Intent(ServiceFlow.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                Toast.makeText(ServiceFlow.this, "Something wrong - getOrder",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void alertDialog() {
        handler.removeCallbacks(runnable);
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceFlow.this);
        builder.setMessage(getString(R.string.the_order_has_been_cancelled_by_user))
                .setPositiveButton(getString(R.string.okay),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
//                        handler.removeCallbacks(runnable);
                                startActivity(new Intent(ServiceFlow.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                                finish();

                            }
                        });
        AlertDialog alert = builder.create();
        alert.setCancelable(false);
        alert.show();
        Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);
        pbutton.setTextColor(ContextCompat.getColor(ServiceFlow.this, R.color.colorAccent));
        pbutton.setTypeface(pbutton.getTypeface(), Typeface.BOLD);
        /*if (!((ServiceFlow) this).isFinishing()) {
            alert.show();
        }*/
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service :
                manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void updateFlowUI(String value) {
        initFlowIcons();
        switch (value) {
            case "ASSIGNED":
                statusLabel.setText(getString(R.string.started_restaurant));
                shopDetailsLayout.setVisibility(View.VISIBLE);
                userDetailsLayout.setVisibility(View.GONE);
                paymentMode.setVisibility(View.GONE);
                map = new HashMap<>();
                map.put("status", GlobalData.getNextOrderStatus("ASSIGNED"));
                break;
            case "PROCESSING":
                shiftStatus.setThumb(getResources().getDrawable(R.drawable.ic_thumb_reached_restaurant));
                statusLabel.setText(getString(R.string.reached_restaurant));
                shopDetailsLayout.setVisibility(View.VISIBLE);
                userDetailsLayout.setVisibility(View.GONE);
                paymentMode.setVisibility(View.GONE);
                map = new HashMap<>();
                map.put("status", GlobalData.getNextOrderStatus("PROCESSING"));
                break;
            case "REACHED":
                shiftStatus.setThumb(getResources().getDrawable(R.drawable.ic_thumb_order_picked_up));
                statusLabel.setText(getString(R.string.order_picked_up));
                shopDetailsLayout.setVisibility(View.VISIBLE);
                userDetailsLayout.setVisibility(View.GONE);
                paymentMode.setVisibility(View.VISIBLE);
                map = new HashMap<>();
                map.put("status", GlobalData.getNextOrderStatus("REACHED"));
                break;
            case "PICKEDUP":
                shiftStatus.setThumb(getResources().getDrawable(R.drawable.ic_thumb_order_delivered));
                statusLabel.setText(getString(R.string.order_delivered));
                userDetailsLayout.setVisibility(View.VISIBLE);
                shopDetailsLayout.setVisibility(View.GONE);
                paymentMode.setVisibility(View.VISIBLE);
                map = new HashMap<>();
                map.put("status", GlobalData.getNextOrderStatus("PICKEDUP"));
                break;
            case "ARRIVED":
                shiftStatus.setThumb(getResources().getDrawable(R.drawable.ic_thumb_payment_received));
                statusLabel.setText(getString(R.string.payment_received));
                userDetailsLayout.setVisibility(View.VISIBLE);
                shopDetailsLayout.setVisibility(View.GONE);
                shiftStatusLayout.setVisibility(View.GONE);
                paymentMode.setVisibility(View.VISIBLE);
                map = new HashMap<>();
                map.put("status", GlobalData.getNextOrderStatus("ARRIVED"));
                map.put("total_pay", GlobalData.order.getInvoice().getPayable().toString());
                map.put("tender_pay", String.valueOf(bal));
                map.put("payment_mode", GlobalData.order.getInvoice().getPaymentMode());
                map.put("payment_status", "success");
//                updateStatus();
                if (paymentOnce)
                    paymentPopupWindow(GlobalData.getNextOrderStatus("ARRIVED"));
                break;
            case "COMPLETED":
                userDetailsLayout.setVisibility(View.VISIBLE);
                shopDetailsLayout.setVisibility(View.GONE);
                shiftStatusLayout.setVisibility(View.GONE);
                rate();
                /*HashMap<String, String> map = new HashMap<>();
                map.put("order_id", String.valueOf(GlobalData.order.getId()));
                map.put("rating", "1");
                map.put("comment", "");
                rateUser(map);*/
//                finish();
                break;
            default:
                break;
        }
    }

    private void updateStatus() {
        if (!connectionHelper.isConnectingToInternet() || GlobalData.order == null)
            return;
        String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this,
                "access_token");
        Call<Order> call = GlobalData.api.updateStatus(GlobalData.order.getId(), map, header);
        call.enqueue(new Callback<Order>() {
            @Override
            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                if (response.isSuccessful()) {
                    GlobalData.order = response.body();
                    // Toast.makeText(ServiceFlow.this, "Amount Paid Successfully !", Toast
                    // .LENGTH_SHORT).show();
                    updateFlowUI(GlobalData.order.getStatus());
                } else {
                    APIError error = ErrorUtils.parseError(response);
                    Toast.makeText(ServiceFlow.this, error.getError(), Toast.LENGTH_SHORT).show();
                    if (response.code() == 401) {
                        SharedHelper.putKey(ServiceFlow.this, "logged_in", "0");
                        startActivity(new Intent(ServiceFlow.this, Login.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                Toast.makeText(ServiceFlow.this, "Something wrong - updateStatus",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void paymentPopupWindow(final String status) {
        if (GlobalData.order == null || GlobalData.order.getInvoice() == null) {
            return;
        }
        final Invoice invoice = GlobalData.order.getInvoice();
        final Order order = GlobalData.order;
        if (invoice.getPaid() != 1) {
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(ServiceFlow.this);
                final FrameLayout frameView = new FrameLayout(ServiceFlow.this);
                builder.setView(frameView);
                alertDialog = builder.create();
                LayoutInflater inflater = alertDialog.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.money_popup, frameView);
                alertDialog.setCancelable(false);
                if (paymentOnce) {
                    paymentOnce = false;
                    final Button paid = dialogView.findViewById(R.id.paid);
                    final PinView pinView = dialogView.findViewById(R.id.pinView);
                    TextView amount_paid_currency_symbol =
                            dialogView.findViewById(R.id.amount_paid_currency_symbol);
                    amount_paid_currency_symbol.setText(numberFormat/*.getCurrency().getSymbol()*/);
                    TextView balance_currency_symbol =
                            dialogView.findViewById(R.id.balance_currency_symbol);
                    balance_currency_symbol.setText(numberFormat/*.getCurrency().getSymbol()*/);
                    final TextView amount_to_pay = dialogView.findViewById(R.id.amount_to_pay);
                    final EditText amount_paid = dialogView.findViewById(R.id.amount_paid);
                    final TextView balance = dialogView.findViewById(R.id.balance);
                    mRoundedAmt = GlobalData.roundoff(invoice.getPayable());
                    amount_to_pay.setText(numberFormat + invoice.getPayable());
                    amount_paid.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!s.toString().equals("")) {
                                double amountPaid = Double.parseDouble(s.toString());
                                bal = amountPaid - mRoundedAmt;
                                balance.setText(/*String.format("%.2f", */bal + "");
                                if (bal >= 0) {
                                    paid.setEnabled(true);
                                    paid.getBackground().setAlpha(255);
                                } else {
                                    // paid.setEnabled(false);
                                    paid.getBackground().setAlpha(128);
                                }
                            } else {
                                balance.setText("0");
                                // paid.setEnabled(false);
                                paid.getBackground().setAlpha(128);
                            }
                        }
                    });
                    paid.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (pinView.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), getString(R.string
                                        .enter_otp), Toast.LENGTH_SHORT).show();
                            } else if (!pinView.getText().toString().equals("") || !pinView
                                    .getText().toString().equals("null")) {
                                if (order != null) {
                                    if (!String.valueOf(order.getOrderOtp()).equals(pinView
                                            .getText().toString())) {
                                        Toast.makeText(getApplicationContext(), getString(R
                                                .string.invalid_otp), Toast.LENGTH_SHORT).show();
                                    } else {
                                        /*if (!amount_paid.getText().toString().equalsIgnoreCase("")) {*/
                                        Double amount = Double.parseDouble(amount_paid.getText().toString());
                                        if (amount >= invoice.getPayable()) {
                                            map = new HashMap<>();
                                            map.put("status", status);
                                            map.put("total_pay", amount_paid.getText().toString());
                                            map.put("tender_pay", String.valueOf(bal));
                                            map.put("payment_mode", invoice.getPaymentMode());
                                            map.put("payment_status", "success");
                                            updateStatus();
                                            alertDialog.dismiss();
                                        } else {
                                            Toast.makeText(getApplicationContext(),
                                                    getString(R.string.full_amount), Toast.LENGTH_SHORT).show();
                                        }
                                        /*} else {
                                            Toast.makeText(getApplicationContext(),
                                                    getString(R.string.enter_the_amount_paid),
                                                    Toast.LENGTH_SHORT).show();
                                        }*/
                                    }
                                }
                            }
                            //service_flow.setText("PAYMENT RECEIVED");
                            //img_5.setColorFilter(ContextCompat.getColor(ServiceFlow.this, R
                            // .color.colorAccent), android.graphics.PorterDuff.Mode.MULTIPLY);
                            //startActivity(new Intent(ServiceFlow.this,Home.class));
                        }
                    });
                    if (alertDialog.getWindow() != null)
                        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alertDialog.show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            otpValidation(order, status);
//            map = new HashMap<>();
//            map.put("status", status);
//            updateStatus();
        }
    }

    private void otpValidation(final Order order, final String status) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceFlow.this);
        final FrameLayout frameView = new FrameLayout(ServiceFlow.this);
        builder.setView(frameView);
        alertDialog = builder.create();
        LayoutInflater inflater = alertDialog.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.layout_otp, frameView);
        alertDialog.setCancelable(false);
        if (paymentOnce) {
            paymentOnce = false;
            final Button paid = dialogView.findViewById(R.id.paid);
            final PinView pinView = dialogView.findViewById(R.id.pinView);
            paid.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pinView.getText().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), getString(R.string
                                .enter_otp), Toast.LENGTH_SHORT).show();
                    } else if (!pinView.getText().toString().equals("") || !pinView
                            .getText().toString().equals("null")) {
                        if (order != null) {
                            if (!String.valueOf(order.getOrderOtp()).equals(pinView
                                    .getText().toString())) {
                                Toast.makeText(getApplicationContext(), getString(R
                                        .string.invalid_otp), Toast.LENGTH_SHORT).show();
                            } else {
                                map = new HashMap<>();
                                map.put("status", status);
                                updateStatus();
                            }
                        }
                    }
                }
            });
            if (alertDialog.getWindow() != null)
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            alertDialog.show();
        }
    }

    private void rate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ServiceFlow.this);
        final FrameLayout frameView = new FrameLayout(ServiceFlow.this);
        builder.setView(frameView);
        final AlertDialog rateDialog = builder.create();
        final LayoutInflater inflater = rateDialog.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.feedback_popup, frameView);
        rating = 5;
        rateRadioGroup = dialogView.findViewById(R.id.rate_radiogroup);
        rateRadioGroup.clearCheck();
        ((RadioButton) rateRadioGroup.getChildAt(4)).setChecked(true);
        rateRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                //rating = i;
                if (i == R.id.one) {
                    //do work when radioButton1 is active
                    rating = 1;
                } else if (i == R.id.two) {
                    //do work when radioButton2 is active
                    rating = 2;
                } else if (i == R.id.three) {
                    //do work when radioButton3 is active
                    rating = 3;
                } else if (i == R.id.four) {
                    //do work when radioButton3 is active
                    rating = 4;
                } else if (i == R.id.five) {
                    //do work when radioButton3 is active
                    rating = 5;
                }
                Log.d("gfgfgf", "onCheckedChanged: " + rating);
            }
        });
        final EditText comment = dialogView.findViewById(R.id.comment);
        Button feedbackSubmit = dialogView.findViewById(R.id.feedback_submit);
        feedbackSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (GlobalData.order != null && GlobalData.order.getId() != null) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("order_id", String.valueOf(GlobalData.order.getId()));
                    map.put("rating", String.valueOf(rating));
                    map.put("comment", comment.getText().toString());
                    rateUser(map);
                    rateDialog.dismiss();
                }
            }
        });
        if (rateDialog.getWindow() != null)
            rateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        rateDialog.show();
    }

    private void rateUser(HashMap<String, String> map) {
        System.out.println(map.toString());
        if (!connectionHelper.isConnectingToInternet())
            return;
        String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this,
                "access_token");
        Call<Message> call = GlobalData.api.rateUser(header, map);
        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(@NonNull Call<Message> call, @NonNull Response<Message> response) {
                if (response.isSuccessful()) {
                    Message message = response.body();
                    Toast.makeText(ServiceFlow.this, message.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(ServiceFlow.this, "Something wrong", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Message> call, @NonNull Throwable t) {
                Toast.makeText(ServiceFlow.this, "Something wrong - rateTranspoter",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getOrder();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_dispute) {
            startActivity(new Intent(ServiceFlow.this, Dispute.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initFlowIcons() {
        if (GlobalData.order != null && GlobalData.order.getOrdertiming() != null) {
            System.out.println("Current Status " + GlobalData.order.getStatus());
            String nextStatus = GlobalData.getNextOrderStatus(GlobalData.order.getStatus());
            System.out.println("nextStatus " + nextStatus);
            for (Ordertiming obj : GlobalData.order.getOrdertiming()) {
                if (obj.getStatus().equals("PROCESSING") || GlobalData.order.getStatus().equals(
                        "PROCESSING")) {
                    iconStartedTowardsRestaurant.setBackgroundResource(R.drawable.round_accent);
                    iconStartedTowardsRestaurant.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    iconReachedRestaurant.setBackgroundResource(R.drawable.round_grey);
                    iconReachedRestaurant.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                }
                if (obj.getStatus().equals("REACHED") || GlobalData.order.getStatus().equals("REACHED")) {
                    iconReachedRestaurant.setBackgroundResource(R.drawable.round_accent);
                    iconReachedRestaurant.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                    iconOrderPickedUp.setBackgroundResource(R.drawable.round_grey);
                    iconOrderPickedUp.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                }
                if (obj.getStatus().equals("PICKEDUP") || GlobalData.order.getStatus().equals("PICKEDUP")) {
                    iconOrderPickedUp.setBackgroundResource(R.drawable.round_accent);
                    iconOrderPickedUp.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    iconOrderDelivered.setBackgroundResource(R.drawable.round_grey);
                    iconOrderDelivered.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                }
                if (obj.getStatus().equals("ARRIVED") || GlobalData.order.getStatus().equals("ARRIVED")) {
                    iconOrderDelivered.setBackgroundResource(R.drawable.round_accent);
                    iconOrderDelivered.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                    iconPaymentReceived.setBackgroundResource(R.drawable.round_grey);
                    iconPaymentReceived.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_IN);
                }
                if (obj.getStatus().equals("COMPLETED") || GlobalData.order.getStatus().equals("COMPLETED")) {
                    iconPaymentReceived.setBackgroundResource(R.drawable.round_accent);
                    iconPaymentReceived.setColorFilter(ContextCompat.getColor(ServiceFlow.this,
                            R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
                }
            }
        }
    }

    private void callPhone(String phone) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                if (phone != null && !phone.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + phone));
                    startActivity(intent);
                }
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CALL_PHONE},
                        MY_PERMISSIONS_REQUEST_CALL_PHONE);
            }
        } else if (phone != null && !phone.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + phone));
            startActivity(intent);
        }
    }

    private void navigation(String s_address, String d_address) {
        Uri naviUri2 = Uri.parse("http://maps.google.com/maps?f=d&hl=en&saddr=" + s_address +
                "&daddr=" + d_address);
//      Uri naviUri2 = Uri.parse("https://www.google.com/maps/search/?api=1&query="+s_address+",
//      "+d_address);
        Intent intent = new Intent(Intent.ACTION_VIEW, naviUri2);
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    private void initShop() {
        if (GlobalData.order != null && GlobalData.order.getShop() != null) {
            Shop shop = GlobalData.order.getShop();
            shopName.setText(shop.getName());
            shopAddress.setText(shop.getAddress());
            Glide.with(getApplicationContext())
                    .load(shop.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.burg)
                            .error(R.drawable.burg))
                    .into(shopAvatar);
        }
    }

    private void initUser() {
        if (GlobalData.order != null && GlobalData.order.getUser() != null) {
            User user = GlobalData.order.getUser();
            userName.setText(user.getName());
            Address address = GlobalData.order.getAddress();
            userAddress.setText((address.getBuilding() != null ? address.getBuilding() + ", " : "") +
                    address.getMapAddress());
            Glide.with(getApplicationContext())
                    .load(user.getAvatar())
                    .apply(new RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.burg)
                            .error(R.drawable.burg))
                    .into(shopAvatar);
        }
    }

    @OnClick({R.id.shop_call, R.id.shop_navigation, R.id.user_call, R.id.user_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.shop_call:
                if (GlobalData.order != null && GlobalData.order.getShop() != null && !GlobalData.order.getShop().getPhone().isEmpty())
                    callPhone(GlobalData.order.getShop().getPhone());
                break;
            case R.id.shop_navigation:
                if (GlobalData.order.getShop() != null) {
                    String s_address =
                            GlobalData.CURRENT_LOCATION.getLatitude() + "," + GlobalData.CURRENT_LOCATION.getLongitude();
                    String d_address =
                            GlobalData.order.getShop().getLatitude() + "," + GlobalData.order.getShop().getLongitude();
                    navigation(s_address, d_address);
                }
                break;
            case R.id.user_call:
                if (GlobalData.order != null && GlobalData.order.getUser() != null && !GlobalData.order.getUser().getPhone().isEmpty())
                    callPhone(GlobalData.order.getUser().getPhone());
                break;
            case R.id.user_navigation:
                if (GlobalData.order.getShop() != null) {
                    String s_address =
                            GlobalData.CURRENT_LOCATION.getLatitude() + "," + GlobalData.CURRENT_LOCATION.getLongitude();
                    String d_address =
                            GlobalData.order.getAddress().getLatitude() + "," + GlobalData.order.getAddress().getLongitude();
                    navigation(s_address, d_address);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);

    }

    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}