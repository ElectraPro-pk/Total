package com.example.shellreviewapp;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.media.FaceDetector;
import android.media.Rating;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import nl.bryanderidder.themedtogglebuttongroup.ThemedButton;
import nl.bryanderidder.themedtogglebuttongroup.ThemedToggleButtonGroup;

public class MainActivity extends AppCompatActivity {
    private RatingBar ratingBar;
    private TextView sent;
    private Button btn;
    private EditText  fname,vehicle,email,address;
    private MaskedEditText phone,cnic;
    private ThemedToggleButtonGroup services;
    SQLiteDatabase db;
    private static final int STORAGE_PERMISSION_CODE = 101;
    private static final String TABLE_NAME = "data";
    private static final String KEY_NAME = "name";
    private static final String KEY_VEHICLE = "veh";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_CNIC = "cnic";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_SERV = "services";
    private static final String KEY_REM = "remarks";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_REW = "review";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_main);
            ratingBar = (RatingBar)findViewById(R.id.rating);
            sent = (TextView)findViewById(R.id.sentiment);
            btn = (Button) findViewById(R.id.subbtn);

            /*Input fields*/
            vehicle = (EditText) findViewById(R.id.veh);
            phone = (MaskedEditText) findViewById(R.id.phone);
            email = (EditText) findViewById(R.id.email);
            fname = (EditText) findViewById(R.id.fname);
            cnic = (MaskedEditText) findViewById(R.id.cnic);
            address = (EditText) findViewById(R.id.address);
            /*Services*/
            RequestPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    STORAGE_PERMISSION_CODE);

            services = (ThemedToggleButtonGroup) findViewById(R.id.services);
           //
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                    switch ((int) ratingBar.getRating()) {
                        case 1:
                            sent.setText("Bad");
                            break;
                        case 2:
                            sent.setText("Need some improvement");
                            break;
                        case 3:
                            sent.setText("Good");
                            break;
                        case 4:
                            sent.setText("Very Good");
                            break;
                        case 5:
                            sent.setText("Excellent");
                            break;
                        default:
                            sent.setText("");
                    }
                }
            });
            db=openOrCreateDatabase("unuploaded", Context.MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS data(name TEXT , veh TEXT, phone TEXT, cnic TEXT,email TEXT, service TEXT,rem TEXT,address TEXT, rew TEXT,status TEXT);");
            backService();

            TextView _slogan = (TextView)findViewById(R.id.slogan);
            Shader myShader = new LinearGradient(
                    0, 0, 0, 100,
                    R.color.theme_red, R.color.theme_orange,
                    Shader.TileMode.CLAMP );
            _slogan.getPaint().setShader( myShader );


        }
        catch(Exception e){
            toast(e.getMessage());
        }
    }

public void RequestPermissions(String permission,int requestCode){
    if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
            == PackageManager.PERMISSION_DENIED) {

        // Requesting the permission
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[] { permission },
                requestCode);
    }
}
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode,
                        permissions,
                        grantResults);

    if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(MainActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

public void askCredential(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setTitle("Restricted Action");
    builder.setIcon(R.mipmap.ic_logo_round);
    builder.setMessage("Enter Admin Password To Exit ");
    EditText input = new EditText(this);
    builder.setView(input);
    builder.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            String pass = input.getText().toString();
            if(pass.trim().length() > 0){
                if(pass.equals("admin")){
                    finish();
                }
                else{
                    toast("Invalid Password");
                }
            }
            else{
                toast("Invalid Password");
            }
        }
    });

    builder.show();
    }

    @Override
    public void onBackPressed() {
        toast("Action Restricted");
        askCredential();

    }
public void thankyou(){
    Intent intent = new Intent(MainActivity.this, thankyou.class);
    startActivity(intent);
    finish();
}
    public void handleClick(View view){
try {
    String contact = phone.getRawText().toString();
    if (contact.length() > 0) {
        List<ThemedButton> themedButton = services.getSelectedButtons();
        ThemedButton _tmp;
       String _tmp_text = " ";
        if(themedButton.size() > 0){
            _tmp = themedButton.get(0);
       _tmp_text = _tmp.getText().toString();
       }
        else{
            _tmp_text = "";
        }
        //toast(_tmp_text+ " \n"+ contact);
         addItemToSheet(_tmp_text);

       /* final ProgressDialog loading = ProgressDialog.show(this,"Info","Please Wait");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                thankyou();
            }
        },2000);
*/


    } else {
        phone.setError("Required");
    }

}catch(Exception e){
    toast(e.getMessage());
    Log.d("E144",e.getMessage());
}

}

void _Reset_Form(){
    try {
        fname.setText("");
        cnic.setText("");
        phone.setText("");
        email.setText("");
        address.setText("");
        ratingBar.setRating(0);
        vehicle.setText("");
    }catch(Exception e){
        Log.d("E191",e.getMessage());
        toast(e.getMessage());
    }

}

    public void  addItemToSheet(String servi) {

        final ProgressDialog loading = ProgressDialog.show(this,"Info","Please Wait");

        try {

            String URL = "https://script.google.com/macros/s/AKfycbyzGHbdVUrAUTN4FwVvvA0p5dkv0exGDDHfRmjififpqYhOXd8/exec";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                           // toast(response);
                            Log.i("R134", response);
                          // thankyou();

                            sendSMS(phone.getRawText().toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            toast("Data Saved");

                            String name =  fname.getText().toString();
                            String veh =  vehicle.getText().toString();
                            String mobile =  phone.getRawText().toString();
                            String _cnic =  cnic.getRawText().toString();
                            String _email =  email.getText().toString();
                            List<ThemedButton> themedButton = services.getSelectedButtons();
                            ThemedButton _tmp;
                            String _tmp_text = " ";
                            if(themedButton.size() > 0){
                                _tmp = themedButton.get(0);
                                _tmp_text = _tmp.getText().toString();
                            }
                            else{
                                _tmp_text = "";
                            }
                            String service =  _tmp_text;
                            String rem =  sent.getText().toString();
                            String _address = address.getText().toString();
                            String rew =  String.valueOf((int)ratingBar.getRating());
                             addData(name,veh,mobile,_cnic,_email,service,rem,_address,rew);
                            _Reset_Form();
                            loading.dismiss();
                            Log.d("E144", error.toString());

                        }
                    }
            ) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    //here we pass param//
                   // params.put("action", "addItem");
                    params.put("name", fname.getText().toString());
                    params.put("veh", vehicle.getText().toString());
                    params.put("mobile", phone.getRawText().toString());
                    params.put("cnic", cnic.getRawText().toString());
                    params.put("email", email.getText().toString());
                    params.put("service", servi.toString());
                    params.put("rem", sent.getText().toString());
                    params.put("address",address.getText().toString());
                    params.put("rew", String.valueOf((int)ratingBar.getRating()));
                    return params;
                }
            };
            int socketTimeOut = 5000;
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        catch(Exception e){
           // toast(e.getMessage());
            loading.dismiss();
        }

    }

    public void  sendSMS(String ph) {

        final ProgressDialog loading = ProgressDialog.show(this,"Info","Please Wait");

        try {
            final String API_KEY = "923228884333-27a10bd9-77e9-4e75-b49f-1d925fc6fe5c";
            final String BRAND_KEY = "TOTAL FUELS";
            final String MESSAGE = "Thankyou for your feedback";
            String URL = "https://sendpk.com/api/sms.php?api_key="+API_KEY+"&sender="+BRAND_KEY+"&mobile=92"+ph+"&message="+MESSAGE;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            loading.dismiss();
                            Log.i("R134", response);
                            thankyou();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                          //  toast("Data Saved");
                            loading.dismiss();
                          // _Reset_Form();
                            Log.d("E144", error.toString());

                        }
                    }
            ) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("api_key", API_KEY);
                    params.put("sender", BRAND_KEY);
                    params.put("mobile", ph);
                    params.put("message", MESSAGE);

                    return params;
                }
            };
            int socketTimeOut = 5000;
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        catch(Exception e){
           // toast(e.getMessage());
            loading.dismiss();
        }

    }



    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();;
    }


   public void  backService(){
        try {
            final long interval = 6000;

            Runnable runnable = new Runnable() {
                public void run() {
                    Looper.prepare();
                           uploadData();
                        try {

                            Thread.sleep(interval);
                        } catch (InterruptedException e) {
                            toast("E416 "+e.getMessage());
                            Log.d("E308",e.getMessage());
                        }

                    }

            };
            Thread thread = new Thread(runnable);
            thread.start();
        }catch (Exception e){
            toast("E425 "+e.getMessage());
            Log.d("E317",e.getMessage());

        }

   }

    void uploadData() {
        try {
            String query = "SELECT * FROM data where status = '0' ";
            Cursor cursor = db.rawQuery(query, null);
            if (cursor != null)
                cursor.moveToFirst();
            do {
                String name = cursor.getString(0);
                String _vehicle = cursor.getString(1);
                String _phone = cursor.getString(2);
                String _cnic = cursor.getString(3);
                String _email = cursor.getString(4);
                String _service = cursor.getString(5);
                String remarks =cursor.getString(6);
                String address = cursor.getString(7);
                String rew =cursor.getString(8);
                _addItemToSheet(name,_vehicle,_phone,_cnic,_email,_service,remarks,address,rew);
           //data(name TEXT , veh TEXT, phone TEXT, cnic TEXT,email TEXT, service TEXT,rem TEXT,address TEXT, rew TEXT,status TEXT);");
            }
            while (cursor.moveToNext());
        }catch(Exception e){

              toast("E473"+e.getMessage());
        }

    }
    public void  _addItemToSheet(String name,String veh,String _phone,String _cnic,String _email,String _service,String rem,String address,String rew) {
        try {

            String URL = "https://script.google.com/macros/s/AKfycbyzGHbdVUrAUTN4FwVvvA0p5dkv0exGDDHfRmjififpqYhOXd8/exec";
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                          //  toast("DATA UPLOADED");
                            updateData(_phone);
                            _sendSMS(_phone);

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                         //   toast("DATA NOT UPLOADED\n"+error.getMessage());
                            Log.d("E144", error.toString());

                        }
                    }
            ) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("name", name);
                    params.put("veh", veh);
                    params.put("mobile", _phone);
                    params.put("cnic", _cnic);
                    params.put("email", _email);
                    params.put("service", _service);
                    params.put("rem", rem);
                    params.put("address",address);
                    params.put("rew", rew);
                    return params;
                }
            };
            int socketTimeOut = 5000;
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        catch(Exception e){
        toast("E521\n"+e.getMessage());
        }

    }
    public void  _sendSMS(String ph) {

        try {
            final String API_KEY = "923228884333-27a10bd9-77e9-4e75-b49f-1d925fc6fe5c";
            final String BRAND_KEY = "TOTAL FUELS";
            final String MESSAGE = "Thankyou for your feedback";
            String URL = "https://sendpk.com/api/sms.php?api_key="+API_KEY+"&sender="+BRAND_KEY+"&mobile=92"+ph+"&message="+MESSAGE;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                         //   toast("SMS SENT");

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                         //   toast("SMS NOT SENT\n"+error.getMessage());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("api_key", API_KEY);
                    params.put("sender", BRAND_KEY);
                    params.put("mobile", ph);
                    params.put("message", MESSAGE);

                    return params;
                }
            };
            int socketTimeOut = 5000;
            RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            stringRequest.setRetryPolicy(retryPolicy);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(stringRequest);
        }
        catch(Exception e){
            toast("E565 : "+e.getMessage());
        }

    }
    void addData(String name,String veh,String _phone,String _cnic,String _email,String service,String rem,String _address,String rew) {
        try {
            //  data(name TEXT , veh TEXT, phone TEXT, cnic TEXT,email TEXT, service TEXT,rem TEXT,address TEXT, rew TEXT,status TEXT);");
            String sql = "INSERT INTO data VALUES('"+name+"','"+veh+"','"+_phone+"','"+_cnic+"','"+_email+"','"+service+"','"+rem+"','"+_address+"','"+rew+"','0')";
          db.execSQL(sql);
        //_Reset_Form();
        }catch(Exception e){
    toast("E560 : "+e.getMessage());
        }
    }
    void updateData(String _phone){
        try {
            String query = "UPDATE data SET status = '1' where phone = '"+_phone+"'";
            db.execSQL(query);

        }catch(Exception e){
            toast("E569 : "+e.getMessage());
        }
    }

}
