package com.example.proyecto;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.proyecto.models.Producto;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "InventarioRopa.db";
    private static final int DATABASE_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // Llamada correcta a super

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       createTables(db);
       datosPrueba(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS productos");
        db.execSQL("DROP TABLE IF EXISTS movimientos");
        db.execSQL("DROP TABLE IF EXISTS clientes");
        db.execSQL("DROP TABLE IF EXISTS ventas");
        db.execSQL("DROP TABLE IF EXISTS detalle_venta");
        db.execSQL("DROP TABLE IF EXISTS pagos");
        onCreate(db);
    }
    public void updateProducto(Producto producto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", producto.getNombre());
        values.put("talla", producto.getTalla());
        values.put("color", producto.getColor());
        values.put("precio", producto.getPrecio());
        values.put("cantidad", producto.getCantidad());
        values.put("categoria", producto.getCategoria());

        db.update("productos", values, "id = ?", new String[]{String.valueOf(producto.getId())});
    }

    public void insertProducto(Producto producto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", producto.getNombre());
        values.put("talla", producto.getTalla());
        values.put("color", producto.getColor());
        values.put("precio", producto.getPrecio());
        values.put("cantidad", producto.getCantidad());
        values.put("categoria", producto.getCategoria());
        values.put("codigoBarras", producto.getCodigoBarras());

        db.insert("productos", null, values);
    }


    private void createTables(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE usuarios (" +
                        "id_usuario INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "usuario TEXT NOT NULL UNIQUE, " +
                        "contrasena TEXT NOT NULL" +
                        ");"
        );
        db.execSQL(
                "CREATE TABLE productos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT, " +
                        "talla TEXT  NOT NULL, " +
                        "color TEXT  NOT NULL, " +
                        "precio REAL, " +
                        "cantidad INTEGER  NOT NULL, " +
                        "categoria TEXT  NOT NULL, " +
                        "codigoBarras TEXT  NOT NULL)"
        );

        db.execSQL(
                "CREATE TABLE movimientos (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "idProducto INTEGER, " +
                        "tipo TEXT, " +
                        "cantidad INTEGER, " +
                        "fecha DATETIME DEFAULT CURRENT_TIMESTAMP, "+
                        "FOREIGN KEY(idProducto) REFERENCES productos(id))"
        );
        db.execSQL(
                "CREATE TABLE clientes (" +
                        "id_cliente INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nombre TEXT NOT NULL, " +
                        "telefono TEXT, " +
                        "direccion TEXT, " +
                        "deuda REAL NOT NULL DEFAULT 0" +
                        ");"
        );
        db.execSQL(
                "CREATE TABLE ventas (" +
                        "id_venta INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_cliente INTEGER, " +
                        "fecha DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "total REAL NOT NULL, " +
                        "FOREIGN KEY(id_cliente) REFERENCES clientes(id_cliente)" +
                        ");"
        );
        db.execSQL(
                "CREATE TABLE detalle_venta (" +
                        "id_detalle INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_venta INTEGER NOT NULL, " +
                        "id_producto INTEGER NOT NULL, " +
                        "cantidad INTEGER NOT NULL, " +
                        "precio_unitario REAL NOT NULL, " +
                        "subtotal REAL NOT NULL, " +
                        "FOREIGN KEY(id_venta) REFERENCES ventas(id_venta), " +
                        "FOREIGN KEY(id_producto) REFERENCES productos(id)" +
                        ");"
        );
        db.execSQL(
                "CREATE TABLE pagos (" +
                        "id_pago INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_cliente INTEGER NOT NULL, " +
                        "monto REAL NOT NULL, " +
                        "fecha DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(id_cliente) REFERENCES clientes(id_cliente)" +
                        ");"
        );
        db.execSQL("CREATE INDEX idx_codigo_barras ON productos(codigoBarras);");
        db.execSQL("CREATE INDEX idx_cliente_nombre ON clientes(nombre);");

    }
    private void datosPrueba(SQLiteDatabase db){
        db.execSQL("INSERT INTO usuarios (usuario, contrasena) VALUES " +
                "('admin', 'admin123')," +
                "('vendedor1', 'venta2024')," +
                "('vendedor2', 'clave456');");

        db.execSQL("INSERT INTO productos (nombre, talla, color, precio, cantidad, categoria, codigoBarras) VALUES " +
                "('Playera Basica', 'M', 'Blanco', 150.00, 50, 'Ropa', '1234567890123')," +
                "('Jeans Slim Fit', '32', 'Azul', 450.00, 30, 'Ropa', '2234567890123')," +
                "('Sudadera Hoodie', 'L', 'Negro', 550.00, 20, 'Ropa', '3234567890123');");

        db.execSQL("INSERT INTO movimientos (idProducto, tipo, cantidad, fecha) VALUES " +
                "(1, 'ENTRADA', 50, '2025-04-09 10:00:00')," +
                "(2, 'ENTRADA', 30, '2025-04-09 10:05:00')," +
                "(3, 'ENTRADA', 20, '2025-04-09 10:10:00');");

        db.execSQL("INSERT INTO clientes (nombre, telefono, direccion, deuda) VALUES " +
                "('Juan Perez', '555-1234', 'Calle Falsa 123', 500.00)," +
                "('Ana Martinez', '555-5678', 'Av. Reforma 456', 0.00)," +
                "('Carlos Gomez', '555-9876', 'Boulevard Central 789', 250.00);");

        db.execSQL("INSERT INTO ventas (id_cliente, total, fecha) VALUES " +
                "(1, 600.00, '2025-04-09 12:30:00')," +
                "(2, 450.00, '2025-04-09 13:00:00');");

        db.execSQL("INSERT INTO detalle_venta (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES " +
                "(1, 1, 2, 150.00, 300.00)," +
                "(1, 2, 1, 300.00, 300.00)," +
                "(2, 2, 1, 450.00, 450.00);");

        db.execSQL("INSERT INTO pagos (id_cliente, monto, fecha) VALUES " +
                "(1, 100.00, '2025-04-09 15:00:00')," +
                "(3, 250.00, '2025-04-09 16:30:00');");
    }
}
