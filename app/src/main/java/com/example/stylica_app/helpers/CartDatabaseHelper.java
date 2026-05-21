package com.example.stylica_app.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.stylica_app.models.CartModel;

import java.util.ArrayList;
import java.util.List;

public class CartDatabaseHelper extends SQLiteOpenHelper {


    private static final String DB_NAME    = "stylica_cart.db";
    private static final int DB_VERSION = 4;


    private static final String TABLE_CART        = "cart";
    private static final String COL_ID            = "id";
    private static final String COL_PRODUCT_ID    = "productId";
    private static final String COL_PRODUCT_NAME  = "productName";
    private static final String COL_PRODUCT_IMAGE = "productImage";
    private static final String COL_PRODUCT_PRICE = "productPrice";
    private static final String COL_CATEGORY      = "category";
    private static final String COL_QUANTITY      = "quantity";
    private static final String COL_STOCK_QUANTITY = "stockQuantity";
    private static final String COL_VENDOR_ID = "vendorId";

    private static CartDatabaseHelper instance;


    public static CartDatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new CartDatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private CartDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE = "CREATE TABLE " + TABLE_CART + " ("
                + COL_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_PRODUCT_ID    + " TEXT, "
                + COL_PRODUCT_NAME  + " TEXT, "
                + COL_PRODUCT_IMAGE + " TEXT, "
                + COL_PRODUCT_PRICE + " REAL, "
                + COL_CATEGORY      + " TEXT, "
                + COL_QUANTITY      + " INTEGER, "
                + COL_STOCK_QUANTITY + " INTEGER, "
                + COL_VENDOR_ID      + " TEXT"
                + ")";

        Log.d("CartDB", "Creating table: " + CREATE_TABLE);

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }

    //Add item to cart
    // If product already in cart -->increase quantity
    public void addToCart(CartModel item) {
        SQLiteDatabase db = this.getWritableDatabase();


        // Check if product already exists in cart
        Cursor cursor = db.query(TABLE_CART, null,
                COL_PRODUCT_ID + "=?",
                new String[]{item.getProductId()},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // Product exists --> update quantity
            int existingQty = cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY));
            int newQty = existingQty + item.getQuantity();

            ContentValues values = new ContentValues();
            values.put(COL_QUANTITY, newQty);

            db.update(TABLE_CART, values,
                    COL_PRODUCT_ID + "=?",
                    new String[]{item.getProductId()});
            cursor.close();
        } else {
            // Product not in cart --> insert new row
            ContentValues values = new ContentValues();

            values.put(COL_PRODUCT_ID,     item.getProductId());
            values.put(COL_PRODUCT_NAME,   item.getProductName());
            values.put(COL_PRODUCT_IMAGE,  item.getProductImage());
            values.put(COL_PRODUCT_PRICE,  item.getProductPrice());
            values.put(COL_CATEGORY,       item.getCategory());
            values.put(COL_QUANTITY,       item.getQuantity());
            values.put(COL_STOCK_QUANTITY, item.getStockQuantity());
            values.put(COL_VENDOR_ID, item.getVendorId());

            db.insert(TABLE_CART, null, values);
        }

        db.close();
    }

    //Get all cart items
    public List<CartModel> getAllCartItems() {
        List<CartModel> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_CART, null);

        if (cursor.moveToFirst()) {
            do {
                CartModel item = new CartModel();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                item.setProductId(cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_ID)));
                item.setProductName(cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_NAME)));
                item.setProductImage(cursor.getString(cursor.getColumnIndexOrThrow(COL_PRODUCT_IMAGE)));
                item.setProductPrice(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_PRODUCT_PRICE)));
                item.setCategory(cursor.getString(cursor.getColumnIndexOrThrow(COL_CATEGORY)));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_QUANTITY)));
                item.setStockQuantity(cursor.getInt(cursor.getColumnIndexOrThrow(COL_STOCK_QUANTITY)));
                item.setVendorId(cursor.getString(cursor.getColumnIndexOrThrow(COL_VENDOR_ID)));
                cartItems.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return cartItems;
    }

    //Update quantity of a cart item
    public void updateQuantity(int id, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_QUANTITY, newQuantity);
        db.update(TABLE_CART, values,
                COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Remove single item from cart
    public void removeItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CART, COL_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
    }

    // Clear entire cart (after order placed)
    public void clearCart() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_CART);
        db.close();
    }

    // Get total number of items in cart
    public int getCartCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_QUANTITY + ") FROM " + TABLE_CART, null);
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return count;
    }

    // Get total price of all cart items
    public double getCartTotal() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_PRODUCT_PRICE + " * " + COL_QUANTITY + ") FROM " + TABLE_CART,
                null);
        double total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getDouble(0);
        }
        cursor.close();
        db.close();
        return total;
    }

    // Check if product already in cart
    public boolean isInCart(String productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CART, null,
                COL_PRODUCT_ID + "=?",
                new String[]{productId},
                null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        db.close();
        return exists;
    }
}