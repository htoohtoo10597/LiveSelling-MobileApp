package iss.workshop.livestreamapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.livestreamapp.R;
import iss.workshop.livestreamapp.models.Product;
import lombok.Data;

@Data
public class ProductsStreamAdapter extends BaseAdapter {

    private Context context;
    private List<Product> products;
    private List<Integer> productQty;

    public ProductsStreamAdapter (Context context, List<Product> products){
        this.context = context;
        this.products = products;
        this.productQty = new ArrayList<>();

        for (Product product : products){
            productQty.add(0);
        }
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    public Integer getItemQty(int i){
        return productQty.get(i);
    }

    public void addItemQty(int i, TextView txtQty){
        Integer currentStock = products.get(i).getQuantity();
        Integer qty = productQty.get(i);
        if (currentStock >= qty + 1){
            productQty.set(i, qty + 1);
            txtQty.setText(Integer.toString(Integer.parseInt(txtQty.getText().toString()) + 1));
        } else {
            Toast.makeText(context, "No more stock for this product. Try another one", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean subtractItemQty(int i){
        Integer qty = productQty.get(i);
        if (qty - 1 >= 0){
            productQty.set(i, qty - 1);
            return true;
        } else {
            Toast.makeText(context, "Cannot subtract any more", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.product_stream_row, viewGroup, false); //change the product row layout
        }
        Product product = products.get(i);

        //should view the following things: product name, product price, qty, and + - button
        TextView txtName = view.findViewById(R.id.product_name);
        txtName.setText(product.getName());

        TextView txtPrice = view.findViewById(R.id.product_price);
        txtPrice.setText(Double.toString(product.getPrice()));

        TextView txtQty = view.findViewById(R.id.product_qty_menu)
                .findViewById(R.id.product_qty);
        txtQty.setText(Integer.toString(productQty.get(i)));

        Button addProd = view.findViewById(R.id.product_qty_menu)
                .findViewById(R.id.add_product_btn);
        addProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemQty(i, txtQty);
            }
        });

        //for add btn: onClickListener(addItemQty i)

        Button subProd = view.findViewById(R.id.product_qty_menu)
                .findViewById(R.id.subtract_product_btn);
        subProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(subtractItemQty(i)){
                    txtQty.setText(Integer.toString(Integer.parseInt(txtQty.getText().toString()) - 1));
                };
            }
        });
        //for subtract btn: onClickListener(subtractItemQty i)
        return view;
    }
}
