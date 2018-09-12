package canali.meridian.cybraum.restaurant.Waiter.food_menu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Anvin on 10/20/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    static final String DB_NAME="menu_db";
    static final int DB_VERSION=1;
    static final String TABLE_NAME="menu_details";
    static final String TABLE_MEMBER_ID="id";
    static final String TABLE_MEMBER_ITEM="item";
    static final String TABLE_MEMBER_SUB_CATEGORY="sub_category";
    static final String TABLE_MEMBER_IMAGE="image";
    static final String TABLE_MEMBER_PRICE="price";
    static final String TABLE_MEMBER_ITEM_ID="item_id";
    static final String TABLE_MEMBER_COUNT="count";




    static final String CREATE_TABLE="CREATE TABLE "+TABLE_NAME+" ("+TABLE_MEMBER_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+TABLE_MEMBER_ITEM+" VARCHAR,"+TABLE_MEMBER_SUB_CATEGORY+" VARCHAR,"+TABLE_MEMBER_IMAGE+" VARCHAR,"+TABLE_MEMBER_PRICE+" VARCHAR,"+TABLE_MEMBER_ITEM_ID+" VARCHAR,"+TABLE_MEMBER_COUNT+" VARCHAR )";
    static final String DELETE_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE);
        System.out.println("table created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int old_version, int new_version) {
        sqLiteDatabase.execSQL(DELETE_TABLE);
        System.out.println("table deleted");
    }

    public int insertData(String item,String sub_category,String image,String price,String item_id,String count){
        System.out.println("item : "+item);
        System.out.println("sub_category : "+sub_category);
        System.out.println("image : "+image);
        System.out.println("price : "+price);
        System.out.println("item_id : "+item_id);
        System.out.println("count : "+count);
        SQLiteDatabase db=this.getWritableDatabase();
        db.beginTransaction();
        ContentValues cv;
        int i=0;
        try{
            cv=new ContentValues();
            cv.put(TABLE_MEMBER_ITEM,item);
            cv.put(TABLE_MEMBER_SUB_CATEGORY,sub_category);
            cv.put(TABLE_MEMBER_IMAGE,image);
            cv.put(TABLE_MEMBER_PRICE,price);
            cv.put(TABLE_MEMBER_ITEM_ID,item_id);
            cv.put(TABLE_MEMBER_COUNT,count);

            db.insert(TABLE_NAME,null,cv);
            i=1;
            System.out.println("value of i : "+i);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            i=0;
        }
        finally {
            db.endTransaction();
            db.close();
        }
        return i;
    }
    public int updateData(String id,String count){
        SQLiteDatabase db=this.getWritableDatabase();
        db.beginTransaction();
        int i=0;
        try{
            String update_query="UPDATE "+TABLE_NAME+" SET "+TABLE_MEMBER_COUNT+"='"+count+" WHERE "+TABLE_MEMBER_ID+"='"+id+"'";
            System.out.println("update query : "+update_query);
            db.execSQL(update_query);
            db.setTransactionSuccessful();
            i=1;

        }catch (Exception e){
            e.printStackTrace();
            i=0;
        }finally {
            db.endTransaction();
            db.close();
        }
        return i;
    }

public ArrayList<MenuModel>getAllDatas(){
    ArrayList<MenuModel> passingArray=new ArrayList<>();
    MenuModel am=new MenuModel();
    SQLiteDatabase db=this.getReadableDatabase();
    try{
        db.beginTransaction();
        String select_query="SELECT * FROM " + TABLE_NAME;
        Cursor c=db.rawQuery(select_query,null);
        System.out.println("c.getCount() = " + c.getCount());
        if(c.getCount()>0){
            while (c.moveToNext()){
                am=new MenuModel();
                am.setTable_id(c.getString(c.getColumnIndex(TABLE_MEMBER_ID)));
                am.setItem(c.getString(c.getColumnIndex(TABLE_MEMBER_ITEM)));
                am.setSub_category(c.getString(c.getColumnIndex(TABLE_MEMBER_SUB_CATEGORY)));
                am.setImage(c.getString(c.getColumnIndex(TABLE_MEMBER_IMAGE)));
                am.setPrice(c.getString(c.getColumnIndex(TABLE_MEMBER_PRICE)));
                am.setItem_id(c.getString(c.getColumnIndex(TABLE_MEMBER_ITEM_ID)));
                am.setCount(c.getString(c.getColumnIndex(TABLE_MEMBER_COUNT)));

                System.out.println("---------------------------------------------------------------------------------");
                System.out.println("ID = " + c.getString(c.getColumnIndex(TABLE_MEMBER_ID)));
                System.out.println("item = " + c.getString(c.getColumnIndex(TABLE_MEMBER_ITEM)));
                  System.out.println("---------------------------------------------------------------------------------");

                passingArray.add(am);
            }
        }
    }catch (Exception e){
        e.printStackTrace();
    }
    finally {
        db.endTransaction();
        db.close();
    }
    return passingArray;
}

public void DeleteRow(String id){
    System.out.println("delete id : "+id);
    SQLiteDatabase db=this.getWritableDatabase();
    try{
        db.beginTransaction();


        String update_query="DELETE FROM "+TABLE_NAME+" WHERE "+TABLE_MEMBER_ID+"='"+id+"'";
        System.out.println("delete query : "+update_query);
        db.execSQL(update_query);
        db.setTransactionSuccessful();
//        db.delete(TABLE_NAME,TABLE_MEMBER_ID+" = ?",new String[]{id});
//        db.setTransactionSuccessful();
    }
    catch (SQLiteException e){
        e.printStackTrace();
    }catch (Exception e){
        e.printStackTrace();
    }
    finally {
        db.endTransaction();
        db.close();
    }
}
    public void clearDatabase() {
        String clearDBQuery = "DELETE FROM "+TABLE_NAME;
        SQLiteDatabase db=this.getWritableDatabase();
        try {
            db.beginTransaction();

            db.execSQL(clearDBQuery);
            db.setTransactionSuccessful();
        }catch (SQLiteException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
            db.close();
        }
    }

}
