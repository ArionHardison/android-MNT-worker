package com.tringtwentyfour.deliveryboy.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.tringtwentyfour.deliveryboy.Application;
import com.tringtwentyfour.deliveryboy.R;
import com.tringtwentyfour.deliveryboy.activities.OrderDetail;
import com.tringtwentyfour.deliveryboy.helper.GlobalData;
import com.tringtwentyfour.deliveryboy.model.Invoice;
import com.tringtwentyfour.deliveryboy.model.Order;
import com.tringtwentyfour.deliveryboy.model.Shop;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 28-09-2017.
 */

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.MyViewHolder> {

    private List<Order> list;
    private Context context;
    String numberFormat;
    public OrderHistoryAdapter(List<Order> list, Context con) {
        this.list = list;
        this.context = con;
        this.numberFormat = Application.getNumberFormat();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_order, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order obj = list.get(position);

        holder.orderId.setText("#"+obj.getId());
        holder.orderStatus.setText(obj.getStatus());
        Shop shop = obj.getShop();
        holder.shopName.setText(shop.getName());
        Glide.with(context)
                .load(shop.getAvatar())
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.cutlery_64)
                        .error(R.drawable.cutlery_64))
                .into(holder.shopAvatar);
        Invoice invoice = obj.getInvoice();
        holder.orderDeliveryTime.setText(GlobalData.getTimeFromString(invoice.getCreatedAt()));
        holder.quantity.setText(obj.getItems().size() + " items");
//        holder.quantity.setText(String.valueOf(invoice.getQuantity())+ " items");
        holder.amount.setText(numberFormat + invoice.getNet());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CardView historyCard;
        private TextView orderStatus, shopName, orderId, orderDeliveryTime, quantity, amount;
        private ImageView shopAvatar;

        private MyViewHolder(View view) {
            super(view);
            historyCard = view.findViewById(R.id.history_card);
            orderStatus = view.findViewById(R.id.order_status);
            shopName = view.findViewById(R.id.shop_name);
            orderId = view.findViewById(R.id.order_id);
            orderDeliveryTime = view.findViewById(R.id.order_delivery_time);
            quantity = view.findViewById(R.id.quantity);
            amount = view.findViewById(R.id.amount);
            shopAvatar = view.findViewById(R.id.shop_avatar);
            historyCard.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (v.getId() == historyCard.getId()) {
                GlobalData.order = list.get(position);
                context.startActivity(new Intent(context, OrderDetail.class));
                //Toast.makeText(v.getContext(), "CARD PRESSED = " + list.get(position).getId(), Toast.LENGTH_SHORT).show();
            }
        }

    }

}
