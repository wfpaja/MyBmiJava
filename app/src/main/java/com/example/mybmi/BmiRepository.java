package com.example.mybmi;

import android.content.Context;

import com.example.mybmi.roomdata.BmiData;
import com.example.mybmi.roomdata.BmiDataDao;
import com.example.mybmi.roomdata.BmiDatabase;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BmiRepository {
    private BmiDatabase database;
    private BmiDataDao dataDao;

    public BmiRepository(Context context) {
        database = BmiDatabase.getDataBase(context);
        dataDao = database.bmiDataDao();
    }

    public void addData(BmiData data, CompletableCallback callback) {
        data.setId(System.currentTimeMillis());
        dataDao.addData(data).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(createCompletableObserver(callback));
    }

    public void deleteByIdList(List<Long> idList, CompletableCallback callback) {
        dataDao.deleteByIds(idList).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(createCompletableObserver(callback));
    }

    public void deleteAll(CompletableCallback callback) {
        dataDao.deleteAll().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(createCompletableObserver(callback));
    }

    private CompletableObserver createCompletableObserver(CompletableCallback callback) {
        return new CompletableObserver() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onComplete() {
                callback.result(true);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                callback.result(false);
            }
        };
    }

    public void getDataListByCategory(Category category, GetDataCallback callback) {
        switch (category) {
            case THIN:
                dataDao.getThinList().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(createSingleObserver(callback));
                break;
            case FAT:
                dataDao.getFatList().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(createSingleObserver(callback));
                break;
            case NORMAL:
                dataDao.getNormalList().subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(createSingleObserver(callback));
                break;
        }
    }

    private SingleObserver<List<BmiData>> createSingleObserver(GetDataCallback callback) {
        return new SingleObserver<List<BmiData>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull List<BmiData> dataList) {
                callback.result(dataList);
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        };
    }

    public interface GetDataCallback {
        void result(List<BmiData> dataList);
    }

    public interface CompletableCallback {
        void result(boolean success);
    }
}
