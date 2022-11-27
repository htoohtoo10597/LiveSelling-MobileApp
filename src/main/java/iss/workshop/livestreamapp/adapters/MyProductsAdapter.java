package iss.workshop.livestreamapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;

import java.util.List;

import iss.workshop.livestreamapp.AddProductActivity;
import iss.workshop.livestreamapp.R;
import iss.workshop.livestreamapp.models.ChannelStream;
import iss.workshop.livestreamapp.models.Product;
import iss.workshop.livestreamapp.models.User;
import iss.workshop.livestreamapp.services.ProductsApi;
import iss.workshop.livestreamapp.services.RetroFitService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyProductsAdapter extends BaseAdapter {

    private Context context;
    private List<Product> products;
    private Button updateBtn;
    private Button deleteBtn;
    private ActivityResultLauncher<Intent> rlUpdateProduct;
    private User user;
    private ChannelStream channel;

    public MyProductsAdapter(Context context, List<Product> products, ActivityResultLauncher<Intent> rlUpdateProduct){
        this.context = context;
        this.products = products;
        this.rlUpdateProduct = rlUpdateProduct;
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
            view = inflater.inflate(R.layout.product_row, //change to the row before testing
                    viewGroup, false);
        }

        Product product= products.get(i);

        TextView txtName = view.findViewById(R.id.product_name);
        txtName.setText(product.getName());

        TextView txtDesc = view.findViewById(R.id.product_description);
        txtDesc.setText(product.getDescription());

        TextView txtPrice = view.findViewById(R.id.product_price);
        txtPrice.setText(Double.toString(product.getPrice()));
        //name, price, SKU, buttons (update/delete)

        updateBtn = view.findViewById(R.id.update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddProductActivity.class);
                intent.setAction("edit-product");
                intent.putExtra("user", user);
                intent.putExtra("channel", channel);
                intent.putExtra("product", product);
                rlUpdateProduct.launch(intent);
                Toast.makeText(context, product.getName(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
