package com.example.clientmanagerapp.database.entity;

import androidx.room.TypeConverter;

import com.example.clientmanagerapp.models.Sex;

import java.util.Date;

public class Converters {

    // Date <-> Long
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    // Enum <-> String
    @TypeConverter
    public static String fromSex(Sex sex) {
        return sex == null ? null : sex.name();
    }

    @TypeConverter
    public static Sex toSex(String value) {
        return value == null ? null : Sex.valueOf(value);
    }
}
