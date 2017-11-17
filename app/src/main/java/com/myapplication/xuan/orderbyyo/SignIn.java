package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.Intent;
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

public class SignIn extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    EditText etuser,etpassword;
    List userlist;
    String user,pass;
    Boolean blus=false,blps=false;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        setTitle("登入(Sign In)");
        FindId();


    }

    @Override
    protected void onResume() {
        super.onResume();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userlist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    userlist.add(ds.getKey());//取出所有帳號名單
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void FindId(){
        etuser = (EditText) findViewById(R.id.etUserR);
        etpassword = (EditText) findViewById(R.id.etPassword);
        sharedPreferences = getSharedPreferences("userList", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        userlist = new ArrayList();

    }

    public void clickSignIn(View v){
            if (etuser.getText().toString().equals("") ||
                    etpassword.getText().toString().equals("")) {
                Toast.makeText(SignIn.this,
                        "欄位請勿空白！！\nDon't blank the field",
                        Toast.LENGTH_SHORT).show();
            }////判斷帳號密碼欄位是否空白
            else {
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(int i=0;i<userlist.size();i++) {
                            blps = false;
                            blus = false;
                            if(etuser.getText().toString().equals(userlist.get(i).toString())){
                                blus = true;
                                user = etuser.getText().toString();
                                break;
                            }////判斷帳號是否存在資料庫
                        }
                        //////////////for結束////////////////
                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                            if(ds.getKey().toString()
                                    .equals(user)) {
                                pass = ds.child("password").getValue().toString();
                                Log.d("NETT", pass);
                            }
                            Log.d("NET",pass+" : "+etpassword.getText().toString());
                        }///////////////判斷/取完資料庫密碼

                        if(etpassword.getText().toString().
                                equals(pass) ){
                            blps = true;
                        }//////判斷密碼是否正確
                        Log.d("NET",pass+" -- "+etpassword.getText().toString());
                        if(!blus){
                            Toast.makeText(SignIn.this,"查無此帳號",Toast.LENGTH_SHORT).show();
                        }//////////////判斷帳號存不存在
                        else if(blps){
                            editor.putString("User", etuser.getText().toString())
                                    .putString("Password", etpassword.getText().toString())
                                    .putBoolean("SignIn", true);
                            editor.commit();
                            finish();
                        }else{
                            Toast.makeText(SignIn.this,"密碼錯誤",Toast.LENGTH_SHORT).show();
                        }/////////////////判斷密碼存不存在

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });





                /////////////////////////////////////////////

            }

    }

    //////////按下註冊按鍵////////////
    public void clickRegister(View v){
        Intent inRegister = new Intent();
        inRegister.setClass(SignIn.this,Register.class);
        startActivity(inRegister);
    }
}
