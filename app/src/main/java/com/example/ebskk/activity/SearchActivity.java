package com.example.ebskk.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ebskk.R;
import com.example.ebskk.bean.Order;
import com.example.ebskk.bean.Product;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Spinner spinner;
    private RelativeLayout targetLine;
//    private List<MgRegion> list = new ArrayList<>();
//    private SpinnerAdapter spinnerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        spinner = findViewById(R.id.spinnerPort);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(SearchActivity.this,list.get(position).getName(),Toast.LENGTH_LONG).show();
                TextView spinnerItem = (TextView)view;
                String spinnerText = spinnerItem.getText().toString();
                Toast.makeText(SearchActivity.this,spinnerText,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        targetLine = findViewById(R.id.target_line);
        targetLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenBottomSheetDialogFragment();
            }
        });


    }

    private void clickOpenBottomSheetDialogFragment() {
        List<Product> listProduct = new ArrayList<>();
        listProduct.add(new Product("Bim Bim Viet Nam 1x5"));
        listProduct.add(new Product("Mi Hao Hao 1x5"));
        listProduct.add(new Product("Kho Ga La Chanh 1x5"));
        listProduct.add(new Product("Pessi 1x5"));
        listProduct.add(new Product("Xuc xich 1x5"));

//        Order order = new Order("200.000VND",listProduct,"Nguyen Co Thach,My Dinh,Nam Tu Liem,Ha Noi");
//        VNFullBottomSheetFragment sheetDialogFragment= VNFullBottomSheetFragment.newInstance(order);
//        sheetDialogFragment.show(getSupportFragmentManager(),sheetDialogFragment.getTag());
//        sheetDialogFragment.setCancelable(false);

    }
}