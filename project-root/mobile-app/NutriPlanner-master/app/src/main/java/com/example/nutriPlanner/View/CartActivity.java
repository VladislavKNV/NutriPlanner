package com.example.nutriPlanner.View;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nutriPlanner.DataBase.DbHelper;
import com.example.nutriPlanner.Model.CartModel;
import com.example.nutriPlanner.R;
import com.example.nutriPlanner.ProgressActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity { //не реализовано

    private BottomNavigationView bottomNavigationView;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        bind();


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.menu_home) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.menu_progress) {
                Intent intent = new Intent(this, ProgressActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.menu_cart) {
                return true;
            } else if (item.getItemId() == R.id.menu_profile) {
                Intent intent = new Intent(this, AccountActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            } else {
                return false;
            }
        });
    }

    private void bind(){
        db = new DbHelper(getApplicationContext()).getReadableDatabase();
        bottomNavigationView = findViewById(R.id.bottom_navigationCart);
        bottomNavigationView.setSelectedItemId(R.id.menu_cart);
    }

    private class CartListAdapter extends BaseAdapter {
        private Context context;
        private List<CartModel> cartList;

        CartListAdapter(Context context, List<CartModel> cart) {
            this.context = context;
            this.cartList = new ArrayList<>(cart);
        }

        @Override
        public int getCount() {
            return cartList.size();
        }

        @Override
        public Object getItem(int position) {
            return cartList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        void updateFoods(List<CartModel> cart) {
            this.cartList.clear();
            this.cartList.addAll(cart);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.cart_card_view, null);
            TextView tvIngredient = view.findViewById(R.id.tvIngredient);
            ImageButton ibCart = view.findViewById(R.id.ibCart);


            CartModel cart = cartList.get(position);
            tvIngredient.setText(cart.getIngredients());


            view.setOnClickListener(v -> {

            });

            return view;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}