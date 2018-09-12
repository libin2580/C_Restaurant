package canali.meridian.cybraum.restaurant.Splsh_Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import canali.meridian.cybraum.restaurant.Kitchen.Kitchen_orderlist;
import canali.meridian.cybraum.restaurant.R;
import canali.meridian.cybraum.restaurant.Waiter.category.Select_catogory;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SharedPreferences preferences =getApplicationContext().getSharedPreferences("login_method",MODE_PRIVATE);
        final String signin=preferences.getString("signin", null);
        final String login=preferences.getString("login", null);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(signin==null&&login==null){

                    Intent ir=new Intent(Splash.this, LoginSelection.class);
                    startActivity(ir);
                    finish();
                }
                else if(signin!=null&&login==null){
                    Intent i=new Intent(Splash.this,Select_catogory.class);
                    startActivity(i);
                    finish();
                }

                else if(signin==null&&login!=null){
                    Intent i=new Intent(Splash.this,Kitchen_orderlist.class);
                    startActivity(i);
                    finish();
                }

            }
        }, 2500);

    }
}
