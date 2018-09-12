package canali.meridian.cybraum.restaurant.Splsh_Login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import canali.meridian.cybraum.restaurant.Kitchen.Kitchen_orderlist;
import canali.meridian.cybraum.restaurant.R;
import canali.meridian.cybraum.restaurant.Waiter.category.Select_catogory;

/**
 * Created by Ansal on 11/17/2017.
 */

public class LoginSelection  extends AppCompatActivity {
LinearLayout Kitchen,Waiter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_selection);
        Kitchen=(LinearLayout)findViewById(R.id.kichen);
        Waiter=(LinearLayout)findViewById(R.id.waiter);

        SharedPreferences preferences =getApplicationContext().getSharedPreferences("login_method",MODE_PRIVATE);
        final String signin=preferences.getString("signin", null);
        final String login=preferences.getString("login", null);
        Waiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signin==null){


                    Intent i=new Intent(LoginSelection.this,Login.class);
                    i.putExtra("login","waiter");
                    startActivity(i);
                    finish();
                }
                else {
                    Intent i=new Intent(LoginSelection.this,Select_catogory.class);
                    startActivity(i);
                    finish();
                }

            }
        });
        Kitchen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (login==null) {
                    Intent i = new Intent(LoginSelection.this, Login.class);
                    i.putExtra("login", "kitchen");
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(LoginSelection.this, Kitchen_orderlist.class);
                    startActivity(i);
                    finish();
                }
            }
        });

    }
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(LoginSelection.this)

                .setMessage("Exit from the app?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        finish();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
// do nothing
                    }
                })

                .show();
    }

}
