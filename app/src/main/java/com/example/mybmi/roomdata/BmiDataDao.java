package com.example.mybmi.roomdata;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


@Dao
public interface BmiDataDao {
    @Insert
    Completable addData(BmiData data);

    @Query("DELETE FROM BmiData WHERE id IN (:idList)")
    Completable deleteByIds(List<Long> idList);

    @Query("DELETE FROM BmiData")
    Completable deleteAll();

    @Query("SELECT * FROM BmiData WHERE bmi  >= 18.5 AND bmi < 24")
    Single<List<BmiData>> getNormalList();

    @Query("SELECT * FROM BmiData WHERE bmi < 18.5")
    Single<List<BmiData>> getThinList();

    @Query("SELECT * FROM BmiData WHERE bmi >= 24")
    Single<List<BmiData>> getFatList();

    @Query("SELECT * FROM BmiData")
    Single<List<BmiData>> getAll();
}
