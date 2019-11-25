package ydkim2110.com.androidbarberbooking;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ydkim2110.com.androidbarberbooking.Adapter.MyCartAdapter;
import ydkim2110.com.androidbarberbooking.Database.CartDatabase;
import ydkim2110.com.androidbarberbooking.Database.CartItem;
import ydkim2110.com.androidbarberbooking.Database.DatabaseUtils;
import ydkim2110.com.androidbarberbooking.Fragments.BookingStep4Fragment;
import ydkim2110.com.androidbarberbooking.Interface.ICartItemLoadLitener;
import ydkim2110.com.androidbarberbooking.Interface.ICartItemUpdateListener;
import ydkim2110.com.androidbarberbooking.Interface.ISumCartListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

public class CartActivity extends AppCompatActivity implements ICartItemLoadLitener, ICartItemUpdateListener, ISumCartListener {

    private static final String TAG = "CartActivity";

    @BindView(R.id.recycler_cart)
    RecyclerView recycler_cart;
    @BindView(R.id.txt_total_price)
    EditText txt_total_price;
    @BindView(R.id.btn_clear_cart)
    Button btn_clear_cart;

    @OnClick(R.id.btn_clear_cart)
    void clearCart() {
        DatabaseUtils.clearCart(mCartDatabase);

        // Update Adapter
        DatabaseUtils.getAllCart(mCartDatabase, this);
    }

    private CartDatabase mCartDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Log.d(TAG, "onCreate: started!!");

        ButterKnife.bind(CartActivity.this);

        mCartDatabase = CartDatabase.getInstance(this);

        DatabaseUtils.getAllCart(mCartDatabase, this);

        // View
        recycler_cart.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_cart.setLayoutManager(linearLayoutManager);
        recycler_cart.addItemDecoration(new DividerItemDecoration(this, linearLayoutManager.getOrientation()));


       // BookingStep4Fragment fragment = BookingStep4Fragment.newInstance( String.valueOf(txt_total_price.getText()));

      //  getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    @Override
    public void onGetAllItemFromCartSuccess(List<CartItem> cartItemList) {
        Log.d(TAG, "onGetAllItemFromCartSuccess: called!!");
        // Here, after we get all cart item from DB
        // We will display by Recycler View
        MyCartAdapter adapter = new MyCartAdapter(this, cartItemList, this);
        recycler_cart.setAdapter(adapter);
    }

    @Override
    public void onCartItemUpdateSuccess() {
        Log.d(TAG, "onCartItemUpdateSuccess: called!!");
        DatabaseUtils.sumCart(mCartDatabase, this);
    }

    @Override
    public void onSumCartSuccess(long value) {
        Log.d(TAG, "onSumCartSuccess: called!!");
       // txt_total_price.append(value);

            //Do something else

//            SharedPreferences preferences = getSharedPreferences("MyData", Context.MODE_PRIVATE);
//            SharedPreferences.Editor edit = preferences.edit();
//            edit.putString("name", txt_total_price.getText().toString());
//            edit.commit();
//            Toast.makeText(this, "Date saved successfully ", Toast.LENGTH_LONG).show();

        txt_total_price.setText(new StringBuilder("").append(value));

        // txt_total_price.setText((int) value);
       // txt_total_price.append(value);


    }

}
