package com.example.shellreviewapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class thankyou extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
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
    private void toast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();;
    }
    public void home(View v){
        Intent i = new Intent(thankyou.this,MainActivity.class);
        startActivity(i);
        finish();
    }
}