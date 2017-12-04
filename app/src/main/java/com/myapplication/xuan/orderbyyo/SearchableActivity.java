package com.myapplication.xuan.orderbyyo;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity {
    TextView text_Name;
    DatabaseReference gpref,mref;
    SharedPreferences sharedPreferences;
    ArrayList boss;
    ListView listView;
    View vpasscheck;
    EditText etPC;
    String query,user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        text_Name = (TextView) findViewById(R.id.tv_SA_name);
        gpref  = FirebaseDatabase.getInstance().getReference("Group");
        mref = FirebaseDatabase.getInstance().getReference("Users");
        boss = new ArrayList();
        vpasscheck = LayoutInflater.from(SearchableActivity.this).inflate(R.layout.passcheck,null);
        etPC = (EditText) vpasscheck.findViewById(R.id.etCheckPassword);
        sharedPreferences = getSharedPreferences("userList", Context.MODE_PRIVATE);
        user = sharedPreferences.getString("User",null);
        // 注意這一行指令
        handleIntent(getIntent());
        // 返回
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    protected void onNewIntent(Intent intent)
    {
        handleIntent(intent);
    }


    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction()))
        {
            query = intent.getStringExtra(SearchManager.QUERY);
            //text_Name.setText("傳遞的查詢字串為 "+query.toString());
            gpref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        if(ds.getKey().toString().equals(query.toString())){
                            boss.clear();
                            text_Name.setText(query);
                            boss.add("群組創辦人 : ");
                            boss.add(ds.child("boss").getValue().toString());
                            listView = (ListView)findViewById(R.id.listView_SearchActivity);
                            ArrayAdapter adapter = new ArrayAdapter(SearchableActivity.this,android.R.layout.simple_list_item_1,boss);
                            listView.setAdapter(adapter);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    public void clickJoin(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("要申請加入群組嗎?");
        builder.setMessage("Do you want to join this group?");
        builder.setPositiveButton("No",null);
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog.Builder builder2 = new AlertDialog.Builder(SearchableActivity.this);
                //////////////////////////////////////////////////////////////
                if(vpasscheck.getParent() != null){
                    ((ViewGroup)vpasscheck.getParent()).removeView(vpasscheck);
                }
                //////////////////////////////////////////////////////////////
                builder2.setView(vpasscheck);
                builder2.setTitle("請輸入群組密碼");
                builder2.setPositiveButton("取消",null);
                builder2.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gpref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child(query).child("password").getValue().toString().equals(etPC.getText().toString())){
                                    mref.child(user).child("group")
                                            .child(query)
                                            .setValue(query);
                                    Toast.makeText(SearchableActivity.this,"成功!!Successful!!",Toast.LENGTH_SHORT).show();
                                    finish();
                                }else {
                                    Toast.makeText(SearchableActivity.this,"密碼錯誤!!Wrong!!",Toast.LENGTH_SHORT).show();
                                }

                                etPC.setText("");
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
                builder2.show();
            }
        });
        builder.show();
    }

    public void clickNo(View v){
        finish();
    }
}
