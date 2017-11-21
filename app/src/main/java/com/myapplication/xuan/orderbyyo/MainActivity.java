package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    FragmentManager fm;
    FragmentTransaction ft;
    OrderFragment orderFragment;
    GroupFragment groupFragment;
    AddOrderFragment addOrderFragment;
    GroupAddFragment groupAddFragment;
    NextOrderFragment nextOrderFragment;

    DatabaseReference ref;

    Boolean blSignIn;
    Boolean blGroup=false;
    String user;
    public String nextChoose1,nextChoose2;
    public List mygroup;
    TextView navname,navtitle;
    View header;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");
        //設置Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //設置Fragment
        fm = getSupportFragmentManager();
        orderFragment = (OrderFragment) fm.findFragmentById(R.id.fragment_Order) ;
        groupFragment = (GroupFragment) fm.findFragmentById(R.id.fragment_Group);
        addOrderFragment = (AddOrderFragment) fm.findFragmentById(R.id.fragment_AddOrder);
        groupAddFragment = (GroupAddFragment) fm.findFragmentById(R.id.fragment_GroupAdd);
        nextOrderFragment = (NextOrderFragment) fm.findFragmentById(R.id.fragment_NextOrder);


        mygroup = new ArrayList();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //設置側選單
        header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navname = (TextView) header.findViewById(R.id.nav_tvName);
        navtitle = (TextView) header.findViewById(R.id.nav_tvTitle);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //取SharePrefrernces資料
        sharedPreferences = getSharedPreferences("userList", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        blSignIn = sharedPreferences.getBoolean("SignIn",false);
        //判斷是否登入
        if(!blSignIn){
            Intent signIn = new Intent();
            signIn.setClass(MainActivity.this,SignIn.class);
            startActivity(signIn);
        }

        //取自己有的群組
        user = sharedPreferences.getString("User",null);
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mygroup.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().toString().equals(user)) {
                        for(DataSnapshot s: ds.child("group").getChildren()){
                            mygroup.add(s.getKey().toString());
                        }
                    }
                }
                if(mygroup.size()>0) {
                    navtitle.setText("Group:" + mygroup.toString());
                }else{
                    navtitle.setText("您尚未加入任何群組!");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //設置側選單使用者名稱
        navname.setText(sharedPreferences.getString("User",null));

        Log.d("NETTT",""+sharedPreferences.getString("User",null));


        //隱藏所有的Fragment
        ft = fm.beginTransaction();
        ft.hide(orderFragment)
                .hide(groupFragment)
                .hide(addOrderFragment)
                .hide(groupAddFragment)
                .hide(nextOrderFragment)
                .commit();

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    /////////右上角選單設定///////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else
        if (id == R.id.action_add) {
            return true;
        }else
        if (id == R.id.action_delete) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    ////////////////////側選單設置///////////////
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        ft = fm.beginTransaction();

        if (id == R.id.nav_home) {
            // Handle the camera action
            ft.hide(orderFragment).hide(groupFragment).commit();

        } else if (id == R.id.nav_order) {
            ft.show(orderFragment).hide(groupFragment).commit();
            setTitle("Order System");

        } else if (id == R.id.nav_group) {
            ft.show(groupFragment).hide(orderFragment).commit();
            setTitle("Group Item");

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout(登出)");
            builder.setMessage("確定要登出嗎?\nAre you sure you want to Logout?");
            builder.setPositiveButton("取消",null);
            builder.setNegativeButton("登出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.putBoolean("SignIn",false).commit();
                    Intent signIn = new Intent();
                    signIn.setClass(MainActivity.this,SignIn.class);
                    startActivity(signIn);
                    ft.hide(orderFragment)
                            .hide(groupFragment)
                            .hide(addOrderFragment)
                            .hide(groupFragment)
                            .commit();
                }
            });
            builder.show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //開啟新增點餐的Fragment
    public void ShowAddOrder(){
        ft = fm.beginTransaction();
        ft.hide(orderFragment).show(addOrderFragment).commit();
    }
    //關閉新增點餐的Fragment
    public void CloseAddOrder(){
        ft = fm.beginTransaction();
        ft.show(orderFragment).hide(addOrderFragment).commit();
    }

    //開啟新增群組的Fragment
    public void ShowAddGroup(){
        ft = fm.beginTransaction();
        ft.hide(groupFragment).show(groupAddFragment).commit();
    }
    //關閉新增群組的Fragment
    public void CloseAddGroup(){
        ft = fm.beginTransaction();
        ft.show(groupFragment).hide(groupAddFragment).commit();
    }

    public void ShowNextOrder(){
        ft = fm.beginTransaction();
        ft.show(nextOrderFragment).hide(orderFragment).commit();
        nextOrderFragment.SetList();
    }

    public void CloseNextOrder(){
        ft = fm.beginTransaction();
        ft.hide(nextOrderFragment).show(orderFragment).commit();
    }



}
