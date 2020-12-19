package com.dietmanager.chef.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.chef.BuildConfigure;
import com.dietmanager.chef.R;
import com.dietmanager.chef.activities.fcm_chat.ChatActivity;
import com.dietmanager.chef.adapter.GroceryIngredientAdapter;
import com.dietmanager.chef.adapter.IngredientsInvoiceAdapter;
import com.dietmanager.chef.api.ApiClient;
import com.dietmanager.chef.api.ApiInterface;
import com.dietmanager.chef.helper.CustomDialog;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.helper.SharedHelper;
import com.dietmanager.chef.model.orderrequest.OrderRequestItem;
import com.dietmanager.chef.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderRequestDetailActivity extends AppCompatActivity {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.tv_payment_mode)
    TextView tv_payment_mode;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    @BindView(R.id.tv_user_address)
    TextView tvUserAddress;
    @BindView(R.id.food_item_name)
    TextView food_item_name;
    @BindView(R.id.food_item_price)
    TextView food_item_price;
    @BindView(R.id.item_total)
    TextView item_total;
    @BindView(R.id.service_tax)
    TextView service_tax;
    @BindView(R.id.llAssignChef)
    LinearLayout llAssignChef;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.back_img)
    ImageView backImg;
    @BindView(R.id.user_img)
    ImageView userImg;
    @BindView(R.id.start_btn)
    Button start_btn;
    @BindView(R.id.llContactUser)
    LinearLayout llContactUser;
    @BindView(R.id.tv_order_id)
    TextView tvOrderId;
    @BindView(R.id.ingredients_rv)
    RecyclerView ingredients_rv;
    Context context;
    private IngredientsInvoiceAdapter ingredientsAdapter;
    Activity activity;
    private OrderRequestItem userRequestItem = null;
    CustomDialog customDialog;
    ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
    String TAG = "OrderRequestDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_request_detailed);
        ButterKnife.bind(this);
        setUp();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setUp() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userRequestItem = (OrderRequestItem) bundle.getSerializable("userRequestItem");
            title.setText(getString(R.string.order_request));
        }
        if (userRequestItem != null) {
            tvOrderId.setText("#"+userRequestItem.getId());
            tvUserName.setText(Utils.toFirstCharUpperAll(userRequestItem.getUser().getName()));
            if (userRequestItem.getFood().getAvatar() != null)
                Glide.with(this).load(BuildConfigure.BASE_URL + userRequestItem.getFood().getAvatar())
                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.man).error(R.drawable.man).dontAnimate()).into(userImg);
            if (userRequestItem.getCustomerAddress() != null) {
                if (userRequestItem.getCustomerAddress().getMapAddress() != null) {
                    tvUserAddress.setText((userRequestItem.getCustomerAddress().getBuilding() != null ? userRequestItem.getCustomerAddress().getBuilding() + ", " : "") +
                            userRequestItem.getCustomerAddress().getMapAddress());
                }
            }


            if(userRequestItem.getIsScheduled()==1&&userRequestItem.getScheduleAt()!=null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date dateObj = null;
                try {
                    dateObj = sdf.parse(userRequestItem.getScheduleAt());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(!DateUtils.isToday(dateObj.getTime())){
                    llAssignChef.setVisibility(View.GONE);
                    llContactUser.setVisibility(View.GONE);
                }
            }

            if(userRequestItem.getStatus().equalsIgnoreCase("COMPLETED") ||userRequestItem.getStatus().equalsIgnoreCase("CANCELLED")){
                llAssignChef.setVisibility(View.GONE);
                llContactUser.setVisibility(View.GONE);
            }
            food_item_name.setText(userRequestItem.getFood().getName());
            food_item_price.setText(GlobalData.profile.getCurrency() + userRequestItem.getFood().getPrice());
            item_total.setText(GlobalData.profile.getCurrency() + userRequestItem.getPayable());
            service_tax.setText(GlobalData.profile.getCurrency() + userRequestItem.getTax());
            total.setText(GlobalData.profile.getCurrency() + userRequestItem.getTotal());
            tv_payment_mode.setText(Utils.toFirstCharUpperAll(userRequestItem.getPaymentMode().toLowerCase()));
        }
        context = OrderRequestDetailActivity.this;
        activity = OrderRequestDetailActivity.this;
        customDialog = new CustomDialog(context);
        setupAdapter();
    }

    private void setupAdapter() {
        ingredientsAdapter = new IngredientsInvoiceAdapter(userRequestItem.getOrderingredient(), context);
        ingredients_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        ingredients_rv.setHasFixedSize(true);
        ingredients_rv.setAdapter(ingredientsAdapter);
        ingredientsAdapter.notifyDataSetChanged();
    }
    ImageView imageViewPurchase;
    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0;

    public void purchasedAlert() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(OrderRequestDetailActivity.this);
            final FrameLayout frameView = new FrameLayout(OrderRequestDetailActivity.this);
            builder.setView(frameView);
            final AlertDialog purchasedDialog = builder.create();
            purchasedDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            purchasedDialog.setCancelable(true);
            LayoutInflater inflater = purchasedDialog.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.purchased_popup, frameView);
            Button purchasedBtn = dialogView.findViewById(R.id.purchased_btn);
            RecyclerView ingredients_rv=dialogView.findViewById(R.id.ingredients_rv);
            GroceryIngredientAdapter ingredientsAdapter = new GroceryIngredientAdapter(userRequestItem.getOrderingredient(), context);
            ingredients_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            ingredients_rv.setHasFixedSize(true);
            ingredients_rv.setAdapter(ingredientsAdapter);
            ingredientsAdapter.notifyDataSetChanged();
            imageViewPurchase=dialogView.findViewById(R.id.imgPurchase);
            dialogView.findViewById(R.id.imgPurchase).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(OrderRequestDetailActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(OrderRequestDetailActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            goToImageIntent();
                        } else {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                        }
                    } else {
                        goToImageIntent();
                    }
                }
            });
            purchasedBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    purchasedDialog.dismiss();
                    purchaseOrder();
                }
            });
            purchasedDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void purchaseOrder(){
        if(imgFile==null){
            Toast.makeText(OrderRequestDetailActivity.this, getResources().getString(R.string.please_upload_image), Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String, RequestBody> map = new HashMap<>();
            map.put("_method",RequestBody.create(MediaType.parse("text/plain"),"PATCH"));
            map.put("status", RequestBody.create(MediaType.parse("text/plain"),"ASSIGNED"));
            updateOrder(userRequestItem.getId(),map);
        }
    }

    public void updateOrder(int id, HashMap<String, RequestBody> map) {
        customDialog.show();
        MultipartBody.Part filePart = null;

        if (imgFile != null)
            filePart = MultipartBody.Part.createFormData("image", imgFile.getName(),
                    RequestBody.create(MediaType.parse("image/*"), imgFile));

        String header = SharedHelper.getKey(OrderRequestDetailActivity.this, "token_type") + " "
                + SharedHelper.getKey(OrderRequestDetailActivity.this, "access_token");
        Call<OrderRequestItem> call = apiInterface.updateOrderWithImage(header,id,map,filePart);
        call.enqueue(new Callback<OrderRequestItem>() {
            @Override
            public void onResponse(@NonNull Call<OrderRequestItem> call, @NonNull Response<OrderRequestItem> response) {
                customDialog.dismiss();
                if (response.isSuccessful()) {
                        GlobalData.selectedOrder = response.body();
                        Intent intent = new Intent(OrderRequestDetailActivity.this, OrderFlowActivity.class);
                        intent.putExtra("userRequestItem", (Serializable)response.body());
                        startActivity(intent);
                }

            }

            @Override
            public void onFailure(@NonNull Call<OrderRequestItem> call, @NonNull Throwable t) {
                customDialog.dismiss();
                Toast.makeText(OrderRequestDetailActivity.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();
            }
        });

    }



    File imgFile;

    private int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            // Get the cursor
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();
if(imageViewPurchase!=null) {
    Glide.with(this)
            .load(imgDecodableString)
            .apply(new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.man)
                    .error(R.drawable.man))
            .into(imageViewPurchase);
//            imgFile = new File(imgDecodableString);
}
            try {
                imgFile = new id.zelory.compressor.Compressor(OrderRequestDetailActivity.this).compressToFile(new File(imgDecodableString));
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (resultCode == Activity.RESULT_CANCELED)
            Toast.makeText(this, getResources().getString(R.string.dont_pick_image),
                    Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if(userRequestItem!=null)
            getOrderById(userRequestItem.getId());
    }
    public void goToImageIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void getOrderById(int id) {
        String header = SharedHelper.getKey(this, "token_type") + " " + SharedHelper.getKey(this, "access_token");
        System.out.println("getProfile Header " + header);
        Call<OrderRequestItem> call = GlobalData.api.getOrderDetailById(header, id);
        call.enqueue(new Callback<OrderRequestItem>() {
            @Override
            public void onResponse(@NonNull Call<OrderRequestItem> call, @NonNull Response<OrderRequestItem> response) {
                if (response.isSuccessful()) {
                    GlobalData.selectedOrder = response.body();
                    userRequestItem=response.body();
                }
            }

            @Override
            public void onFailure(@NonNull Call<OrderRequestItem> call, @NonNull Throwable t) {
                Toast.makeText(OrderRequestDetailActivity.this, R.string.something_went_wrong, Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick({R.id.back_img, R.id.call_img, R.id.start_btn, R.id.message_img})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back_img:
                onBackPressed();
                break;

            case R.id.message_img:
                Intent intentMessage = new Intent(this, ChatActivity.class);
                intentMessage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intentMessage.putExtra("channel_name", "" + userRequestItem.getId());
                intentMessage.putExtra("channel_sender_id", "" + userRequestItem.getChefId());
                intentMessage.putExtra("is_push", false);
                startActivity(intentMessage);
                break;
            case R.id.start_btn:
                if(userRequestItem.getOrderingredient().size()>0 && userRequestItem.getIngredientImage()==null)
                    purchasedAlert();
                else {
                    GlobalData.selectedOrder = userRequestItem;
                    Intent intent = new Intent(context, OrderFlowActivity.class);
                    intent.putExtra("userRequestItem", (Serializable)userRequestItem);
                    context.startActivity(intent);
                }
                break;
            case R.id.call_img:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userRequestItem.getUser().getPhone()));
                if (dialIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(dialIntent);
                else
                    Utils.displayMessage(this, "Call feature not supported");
                break;
        }
    }
}