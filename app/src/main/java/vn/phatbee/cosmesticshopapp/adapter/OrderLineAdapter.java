package vn.phatbee.cosmesticshopapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
import java.util.Map;

import vn.phatbee.cosmesticshopapp.R;
import vn.phatbee.cosmesticshopapp.model.OrderLine;

public class OrderLineAdapter extends RecyclerView.Adapter<OrderLineAdapter.OrderLineViewHolder> {

    private Context context;
    private List<OrderLine> orderLines;

    public OrderLineAdapter(Context context, List<OrderLine> orderLines) {
        this.context = context;
        this.orderLines = orderLines;
    }

    @NonNull
    @Override
    public OrderLineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_order_product_detail, parent, false);
        return new OrderLineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderLineViewHolder holder, int position) {
        OrderLine orderLine = orderLines.get(position);
        Map<String, Object> productSnapshot = orderLine.getProductSnapshot();

        if (productSnapshot != null) {
            // Hiển thị tên sản phẩm
            holder.tvProductName.setText(productSnapshot.get("productName").toString());

            // Hiển thị số lượng
            holder.tvQuantity.setText("Số lượng: " + orderLine.getQuantity());

            // Hiển thị giá tiền
            if (productSnapshot.containsKey("price")) {
                double price = Double.parseDouble(productSnapshot.get("price").toString());
                holder.tvPrice.setText("Giá: " + String.format("%,.0f VND", price));
            } else {
                holder.tvPrice.setText("Giá: N/A");
            }

            // Hiển thị hình ảnh
            String imageUrl = productSnapshot.get("image") != null ? productSnapshot.get("image").toString() : "";
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivProductImage);
        }
    }

    @Override
    public int getItemCount() {
        return orderLines.size();
    }

    static class OrderLineViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvQuantity, tvPrice; // Thêm tvPrice

        public OrderLineViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            tvPrice = itemView.findViewById(R.id.tvPrice); // Khởi tạo tvPrice
        }
    }
}