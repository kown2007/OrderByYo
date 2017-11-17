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
    Boolean blSignIn;
    Boolean blGroup=false;
    TextView navname,navtitle;
    View header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Home");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fm = getSupportFragmentManager();
        orderFragment = (OrderFragment) fm.findFragmentById(R.id.fragment_Order) ;
        groupFragment = (GroupFragment) fm.findFragmentById(R.id.fragment_Group);
        addOrderFragment = (AddOrderFragment) fm.findFragmentById(R.id.fragment_AddOrder);
        groupAddFragment = (GroupAddFragment) fm.findFragmentById(R.id.fragment_GroupAdd);
        ft = fm.beginTransaction();
        ft.hide(orderFragment).hide(groupFragment).hide(addOrderFragment).commit();


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

        header = navigationView.inflateHeaderView(R.layout.nav_header_main);
        navname = (TextView) header.findViewById(R.id.nav_tvName);
        navtitle = (TextView) header.findViewById(R.id.nav_tvTitle);

    }

    @Override
    protected void onResume() {
        super.onResume();
        sharedPreferences = getSharedPreferences("userList", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        blSignIn = sharedPreferences.getBoolean("SignIn",false);
        if(!blSignIn){
            Intent signIn = new Intent();
            signIn.setClass(MainActivity.this,SignIn.class);
            startActivity(signIn);
        }
        navname.setText(sharedPreferences.getString("User",null));
        Log.d("NETTT",""+sharedPreferences.getString("User",null));
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
                }
            });
            builder.show();
        }
//          else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void ShowAddOrder(){
        ft = fm.beginTransaction();
        ft.hide(orderFragment).show(addOrderFragment).commit();
    }

    public void CloseAddOrder(){
        ft = fm.beginTransaction();
        ft.show(orderFragment).hide(addOrderFragment).commit();
    }

    public void ShowAddGroup(){
        ft = fm.beginTransaction();
        ft.hide(groupFragment).show(groupAddFragment).commit();
    }

    public void CloseAddGroup(){
        ft = fm.beginTransaction();
        ft.show(groupFragment).hide(groupAddFragment).commit();
    }


}
