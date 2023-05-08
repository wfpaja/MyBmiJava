package com.example.mybmi;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.mybmi.roomdata.BmiData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class BmiViewModel extends AndroidViewModel {
    private Category[] categories;
    private String[] titleTexts;
    private List<CategoryData> categoryDataList;
    private BmiRepository bmiRepository;
    private HashMap<Category, List<BmiData>> bmiMap;
    private SelectedChangeCallback selectedChangeCallback;
    private TotalCountCallback totalCountCallback;
    private HashSet<Long> selectedSet;
    public static final float BMI_THIN = 18.5f;
    public static final float BMI_FAT = 24;

    public BmiViewModel(@NonNull Application application) {
        super(application);
        bmiRepository = new BmiRepository(getApplication());
        dataInit();
    }

    public List<CategoryData> getCategoryDataList() {
        return categoryDataList;
    }

    private void dataInit() {
        bmiMap = new HashMap<>();
        selectedSet = new HashSet<>();
        categories = new Category[]{Category.THIN, Category.NORMAL, Category.FAT};
        titleTexts = new String[] {
                getApplication().getString(R.string.thin_format),
                getApplication().getString(R.string.normal_format),
                getApplication().getString(R.string.fat_format),
        };

        categoryDataList = new ArrayList<>();
        for (int i = 0; i < Math.min(categories.length, titleTexts.length); i++) {
            CategoryData item = new CategoryData();
            item.setCategory(categories[i]);
            item.setTitleText(titleTexts[i]);
            categoryDataList.add(item);
//            bmiMap.put(categories[i], null);
            getDataListByCategory(categories[i], null);
        }
    }

    public synchronized void getDataListByCategory(Category category, GetDataCallback callback) {
        List<BmiData> dataList = bmiMap.get(category);

        if (dataList == null) {
            bmiRepository.getDataListByCategory(category, new BmiRepository.GetDataCallback() {
                @Override
                public void result(List<BmiData> dataList) {
                    bmiMap.put(category, dataList);
                    if (callback != null) {
                        callback.result(dataList);
                    }
                    returnTotalCount();
                }
            });
        }
        else {
            callback.result(dataList);
        }
    }

    public void sendBmiData(BmiData data, SendBmiCallBack callBack) {
        calculateBmi(data);
        addBmiData(data, callBack);
    }

    private void calculateBmi(BmiData data) {
        double bmi = data.getWeight() / Math.pow((data.getHeight() / 100), 2);
        data.setBmi(bmi);
    }

    private void addBmiData(BmiData data, SendBmiCallBack callBack) {
        bmiRepository.addData(data, new BmiRepository.CompletableCallback() {
            @Override
            public void result(boolean success) {
                Category category = getCategory(data.getBmi());
                if (bmiMap.get(category) == null) {
                    getDataListByCategory(category, new GetDataCallback() {
                        @Override
                        public void result(List<BmiData> dataList) {
                            callBack.result(category, data);
                        }
                    });
                }
                else {
                    bmiMap.get(category).add(data);
                    callBack.result(category, data);
                    returnTotalCount();
                }
            }
        });
    }
    private Category getCategory(double bmi) {
        Category result;

        if (bmi < BMI_THIN ) {
            result = Category.THIN;
        }
        else if (bmi >= BMI_FAT) {
            result = Category.FAT;
        }
        else {
            result = Category.NORMAL;
        }
        return result;
    }

    public void setSelectChangeCallback(SelectedChangeCallback callback) {
        selectedChangeCallback = callback;
    }

    public void setTotalCountCallback(TotalCountCallback totalCountCallback) {
        this.totalCountCallback = totalCountCallback;
    }

    public boolean processingSelect(long id) {
        boolean result;
        if (!selectedSet.contains(id)) {
            selectedSet.add(id);
            result = true;
        }
        else {
            selectedSet.remove(id);
            result = false;
        }
        selectedChangeCallback.result(selectedSet.size());
        return result;
    }

    public boolean checkSelected(long id) {
        return selectedSet.contains(id);
    }

    public void deleteBmiBySelectedList(SuccessCallback callback ) {
        List<Long> idList = new ArrayList<>(selectedSet);
        bmiRepository.deleteByIdList(idList, new BmiRepository.CompletableCallback() {
            @Override
            public void result(boolean success) {
                for (List<BmiData> item : bmiMap.values())
                {
                    if(item != null) {
                        for (int i = item.size() - 1; i >= 0; i--) {
                            BmiData temp = item.get(i);
                            if (selectedSet.contains(temp.getId())) {
                                item.remove(i);
                            }
                        }
                    }
                }
                selectedSet.clear();
                callback.result(success);
                selectedChangeCallback.result(selectedSet.size());
                returnTotalCount();
            }
        });
    }

    public void deleteAll(SuccessCallback callback) {
        bmiRepository.deleteAll(new BmiRepository.CompletableCallback() {
            @Override
            public void result(boolean success) {
                for (List<BmiData> item : bmiMap.values()) {
                    if (item != null) {
                        item.clear();
                    }
                }
                selectedSet.clear();
                callback.result(success);
                returnTotalCount();
            }
        });
    }

    private void returnTotalCount() {
        if (totalCountCallback != null) {
            int totalCount = 0;
            for (List<BmiData> item : bmiMap.values()) {
                if (item != null) {
                    totalCount += item.size();
                }
            }
            totalCountCallback.result(totalCount);
        }
    }

    public interface SendBmiCallBack {
        void result(Category category, BmiData data);
    }

    public interface GetDataCallback {
        void result(List<BmiData> dataList);
    }

    public interface SelectedChangeCallback {
        void result(int count);
    }

    public interface SuccessCallback {
        void result(boolean success);
    }

    public interface TotalCountCallback {
        void result(int count);
    }
}
