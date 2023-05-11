package com.example.mybmi;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mybmi.roomdata.BmiData;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {
    private TextView tvBmi;
    private EditText etName, etHeight, etWeight;
    private Button btnClear, btnBmi, btnDeleteAll, btnDeleteBySelected;
    private RecyclerView rvCategory;
    private CategoryAdapter categoryAdapter;
    private BmiViewModel bmiViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        int b = 0;
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        etInit();
        tvBmi = findViewById(R.id.tv_bmi);
        rvCategory = findViewById(R.id.rv_member_bmi);
        btnInit();
        setupViewModel();
        categoryAdapter = new CategoryAdapter(bmiViewModel);
        rvCategory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvCategory.setAdapter(categoryAdapter);
    }

    private void etInit() {
        etName = findViewById(R.id.et_name);
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);

        etName.addTextChangedListener(textWatcher);
        etHeight.addTextChangedListener(textWatcher);
        etWeight.addTextChangedListener(textWatcher);
    }

    private void btnInit() {
        btnClear = findViewById(R.id.btn_clear);
        btnBmi = findViewById(R.id.btn_bmi);
        btnDeleteAll = findViewById(R.id.btn_delete_all);
        btnDeleteBySelected = findViewById(R.id.btn_delete_by_selected);

        btnClear.setOnClickListener(clearClick);
        btnBmi.setOnClickListener(bmiClick);
        btnDeleteBySelected.setOnClickListener(deleteByCheckedClick);
        btnDeleteAll.setOnClickListener(deleteAllClick);
        checkBtnClearEnable();
    }

    private void setupViewModel() {
        bmiViewModel = new ViewModelProvider(this).get(BmiViewModel.class);
        bmiViewModel.setSelectChangeCallback(new BmiViewModel.SelectedChangeCallback() {
            @Override
            public void result(int count) {
                btnDeleteBySelected.setEnabled(count > 0);
            }
        });
        bmiViewModel.setTotalCountCallback(new BmiViewModel.TotalCountCallback() {
            @Override
            public void result(int count) {
                btnDeleteAll.setEnabled(count > 0);
            }
        });
    }

    private final View.OnClickListener clearClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            etName.setText("");
            etHeight.setText("");
            etWeight.setText("");
            tvBmi.setText("");
        }
    };

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkBtnClearEnable();
        }
    };

    private void checkBtnClearEnable() {
        int check = etName.length() + etHeight.length() + etWeight.length();
        btnClear.setEnabled(check > 0);
    }

    private final View.OnClickListener bmiClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            BmiData bmiData = checkInputValid();
            if (bmiData != null) {
                bmiViewModel.sendBmiData(bmiData, new BmiViewModel.SendBmiCallBack() {
                        @Override
                        public void result(Category category, BmiData data) {
                            categoryAdapter.updateData(category);
                            DecimalFormat df = new DecimalFormat("0.##");
                            String bmiText = df.format(data.getBmi());
                            tvBmi.setText(bmiText);
                        }
                });
            }
        }
    };

    private final View.OnClickListener deleteByCheckedClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            bmiViewModel.deleteBmiBySelectedList(new BmiViewModel.SuccessCallback() {
                @Override
                public void result(boolean success) {
                    categoryAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private final View.OnClickListener deleteAllClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            bmiViewModel.deleteAll(new BmiViewModel.SuccessCallback() {
                @Override
                public void result(boolean success) {
                    categoryAdapter.notifyDataSetChanged();
                }
            });
        }
    };

    private BmiData checkInputValid() {
        BmiData result = null;

        if (checkInputNotEmpty(etName) && checkInputNotEmpty(etHeight) && checkInputNotEmpty(etWeight)) {
            result = changeToBmiData();
        }
        else {
            Toast.makeText(MainActivity.this,  getResources().getText(R.string.input_have_empty), Toast.LENGTH_SHORT).show();
        }
        return  result;
    }

    private boolean checkInputNotEmpty(EditText et) {
        return et.getText().toString().trim().length() != 0;
    }

    private BmiData changeToBmiData() {
        BmiData result = null;
        try {
            double height = Double.parseDouble(etHeight.getText().toString());
            double weight = Double.parseDouble(etWeight.getText().toString());
            if (height !=0 && weight != 0) {
                result = new BmiData();
                result.setName(etName.getText().toString());
                result.setHeight(height);
                result.setWeight(weight);
            }
            else {
                Toast.makeText(MainActivity.this,  getResources().getText(R.string.input_no_zero), Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            Toast.makeText(this, getResources().getText(R.string.input_error), Toast.LENGTH_SHORT).show();
        }

        return result;
    }
}
