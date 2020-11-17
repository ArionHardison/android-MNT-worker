package com.dietmanager.chef.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.dietmanager.chef.BuildConfigure;
import com.dietmanager.chef.R;
import com.dietmanager.chef.activities.OrderRequestActivity;
import com.dietmanager.chef.helper.AppConstants;
import com.dietmanager.chef.helper.GlobalData;
import com.dietmanager.chef.model.orderrequest.OrderRequestItem;
import com.dietmanager.chef.utils.Utils;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<OrderRequestItem> list;

    public HistoryAdapter(List<OrderRequestItem> list, Context con) {
        this.list = list;
        this.context = con;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_history, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        OrderRequestItem order = list.get(position);
     /*   if (order.getAddress() != null) {
            if (order.getAddress().getMapAddress() != null) {
                holder.address.setText((order.getAddress().getBuilding() != null ? order.getAddress().getBuilding() + ", " : "") +
                        order.getAddress().getMapAddress());
            }
        } else {
            if (order.getShop().getMapsAddress() != null) {
                holder.address.setText(order.getShop().getMapsAddress());
            }
        }*/
        holder.price.setText(/*context.getString(R.string.currency_value)*/GlobalData.profile.getCurrency() + "" + order.getPayable());

        if (order.getUser() != null) {
            String name = Utils.toFirstCharUpperAll(order.getUser().getName());
            holder.userName.setText(name);
            Glide.with(context).load(BuildConfigure.BASE_URL+order.getUser().getAvatar())
                    .apply(new RequestOptions().placeholder(R.drawable.man).error(R.drawable.man).dontAnimate()).into(holder.userImg);
        }
        String payment_mode="Cash";
        /*if (order.getInvoice().getPaymentMode().equalsIgnoreCase("stripe")) {
            payment_mode = context.getString(R.string.credit_card);
        } else {
            payment_mode = Utils.toFirstCharUpperAll(order.getInvoice().getPaymentMode());
        }*/
        holder.paymentMode.setText(payment_mode);
        holder.itemLayout.setOnClickListener(v -> {
            GlobalData.selectedOrder = list.get(position);
            context.startActivity(new Intent(context, OrderRequestActivity.class));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void add(OrderRequestItem item, int position) {
        list.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(OrderRequestItem item) {
        int position = list.indexOf(item);
        list.remove(position);
        notifyItemRemoved(position);
        notifyDataSetChanged();
    }

    public void setList(List<OrderRequestItem> list) {
        this.list = list;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView userName, address, paymentMode, price;
        CardView itemLayout;
        ImageView userImg;

        public MyViewHolder(View view) {
            super(view);
            userName = view.findViewById(R.id.user_name);
            price = view.findViewById(R.id.price);
            address = view.findViewById(R.id.address);
            paymentMode = view.findViewById(R.id.payment_mode);
            itemLayout = view.findViewById(R.id.item_layout);
            userImg = view.findViewById(R.id.user_img);
        }
    }
}