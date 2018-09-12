package canali.meridian.cybraum.restaurant.Waiter.food_menu;

/**
 * Created by Ansal on 11/23/2017.
 */

public class View_model {
    String order_item_id,item_name,price,quantity;
    public void setorder_item_id(String order_item_id) {
        this.order_item_id = order_item_id;
    }

    public void setitem_name(String item_name) {
        this.item_name = item_name;
    }

    public void setprice(String price) {
        this.price = price;
    }

    public void setquantity(String quantity) {
        this.quantity = quantity;
    }

    public String getitem_name() {
        return item_name;
    }

    public String getprice() {
        return price;
    }

    public String getquantity() {
        return quantity;
    }

    public String getorder_item_id() {
        return order_item_id;
    }
}
