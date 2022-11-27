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
import iss.workshop.livestreamapp.models.Product;
import iss.workshop.livestreamapp.models.Stream;

public class ProductsListAdapter extends BaseAdapter {

    private Context context;
    protected List<Product> products;

    public ProductsListAdapter(Context context, List<Product> products){
        this.context = context;
        this.products = products;
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int i) {
        return products.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.product_row, viewGroup, false);
        }

        Product currProduct = products.get(i);

        TextView txtProductName = view.findViewById(R.id.product_name);
        txtProductName.setText(currProduct.getName());

        return view;
    }
}
