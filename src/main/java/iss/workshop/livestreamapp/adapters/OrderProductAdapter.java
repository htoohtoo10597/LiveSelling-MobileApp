package iss.workshop.livestreamapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import iss.workshop.livestreamapp.R;
import iss.workshop.livestreamapp.models.OrderProduct;
import iss.workshop.livestreamapp.models.Orders;

public class OrderProductAdapter extends BaseAdapter {

    private Context context;
    private List<OrderProduct> oProducts;

    public OrderProductAdapter(Context context, List<OrderProduct> orderProducts){
        this.context = context;
        this.oProducts = orderProducts;
    }
    @Override
    public int getCount() {
        return oProducts.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.order_product_row, viewGroup, false);
        }
        OrderProduct orderProduct = oProducts.get(i);

        TextView productName = view.findViewById(R.id.product_Name);
        productName.setText(orderProduct.getProduct().getName());

        TextView productQty = view.findViewById(R.id.product_Qty);
        productQty.setText(Integer.toString(orderProduct.getQuantity()));

        TextView productDesc = view.findViewById(R.id.product_desc);
        productDesc.setText(orderProduct.getProduct().getDescription());

        TextView productPrice = view.findViewById(R.id.product_price);
        productPrice.setText(Double.toString(orderProduct.getProduct().getPrice()));

        return view;
    }
}
