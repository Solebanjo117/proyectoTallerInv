package com.example.proyecto;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InventarioRopa.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // Llamada correcta a super
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE productos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT, " +
                        "talla TEXT, " +
                        "color TEXT, " +
                        "precio REAL, " +
                        "cantidad INTEGER, " +
                        "categoria TEXT, " +
                        "codigoBarras TEXT)"
        );

        db.execSQL(
                "CREATE TABLE movimientos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idProducto INTEGER, " +
                        "tipo TEXT, " +
                        "cantidad INTEGER, " +
                        "fecha TEXT, " +
                        "FOREIGN KEY(idProducto) REFERENCES productos(id))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        onCreate(db);
    }
}
