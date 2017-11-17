package com.myapplication.xuan.orderbyyo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Register extends AppCompatActivity {
    EditText etuser,etpassword,etname,etphone;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    List userlist;
    int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        FindId();
        ref = firebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    userlist.add(ds.getKey());
                    Log.d("NET",""+ds.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void FindId(){
        etuser = (EditText)findViewById(R.id.etUserR);
        etpassword = (EditText)findViewById(R.id.etPasswordR);
        etname = (EditText)findViewById(R.id.etNameR);
        etphone = (EditText)findViewById(R.id.etPhoneR);
        userlist = new ArrayList();
    }

    public void clickOK(View v){
        int OK=1;
        ref = firebaseDatabase.getInstance().getReference("Users");
        for(int i = 0;i<userlist.size();i++) {
            if (userlist.get(i).toString().equals(etuser.getText().toString())) {
                Toast.makeText(Register.this,"此帳號已被註冊",Toast.LENGTH_SHORT).show();
                OK = 0;
                break;
            }
        }
        if(OK == 1) {
            ref.child(etuser.getText().toString()).child("password").setValue(etpassword.getText().toString());
            ref.child(etuser.getText().toString()).child("name").setValue(etname.getText().toString());
            ref.child(etuser.getText().toString()).child("phone").setValue(etphone.getText().toString());
            Toast.makeText(Register.this,"註冊成功\n Register Successful",Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
