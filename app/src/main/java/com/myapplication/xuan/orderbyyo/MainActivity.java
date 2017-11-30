package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

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
    HomeFragment homeFragment;

    DatabaseReference ref,odref,gpref;

    Boolean blSignIn,bl_DGP;
    String user;
    public String nextChoose1,nextChoose2,nextBoss,groupBoss;
    public List mygroup,mydataList,searchList,search_n;
    TextView navname,navtitle;
    View header,vmydata,vSearch,vDeleteGP;
    int total=0;
    ListView listView_DGP;
    ArrayAdapter adapter_DGP;





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
        homeFragment = (HomeFragment) fm.findFragmentById(R.id.fragment_Home);


        mygroup = new ArrayList();
        mydataList = new ArrayList();
        searchList = new ArrayList();
        search_n = new ArrayList();
        vmydata = LayoutInflater.from(MainActivity.this).inflate(R.layout.mydata,null);
        vSearch = LayoutInflater.from(MainActivity.this).inflate(R.layout.searchlist,null);
        vDeleteGP = LayoutInflater.from(MainActivity.this).inflate(R.layout.deletegroup,null);




//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

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
        SearchGroup();


        //設置側選單使用者名稱
        navname.setText(sharedPreferences.getString("User",null));



        //隱藏所有的Fragment
        ft = fm.beginTransaction();
        ft.hide(orderFragment)
                .hide(groupFragment)
                .hide(addOrderFragment)
                .hide(groupAddFragment)
                .hide(nextOrderFragment)
                .commit();

        //

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
        if (id == R.id.action_mydata) {
            ListView listViewAL = (ListView)vmydata.findViewById(R.id.listView_mydata);
            ArrayAdapter adapterAL = new ArrayAdapter(this,android.R.layout.simple_list_item_1,mydataList);
            listViewAL.setAdapter(adapterAL);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("我的資料");
            if (vmydata.getParent() != null) {
                ((ViewGroup) vmydata.getParent()).removeView(vmydata);
            }
            builder.setView(vmydata);
            builder.setPositiveButton("確定", null);
            builder.show();
            return true;
//        }else
//        if (id == R.id.action_add) {
//            Toast.makeText(this,"MAin",Toast.LENGTH_SHORT).show();
//            return true;
        }else
        if (id == R.id.action_delete) {
            DeleteGroup();

            return true;
        }
        if (id == R.id.action_search) {
            searchList.clear();
            search_n.clear();
            total =0;
            odref = FirebaseDatabase.getInstance().getReference("OrderList");
            odref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot ds : dataSnapshot.child(nextChoose1).child(nextChoose2)
                                .child("List").getChildren()) {

                            for (DataSnapshot dd : ds.getChildren()) {//尋覽有訂餐的使用者

                                if (dd.getKey().equals("total")) {
                                    total += Integer.parseInt(dd.getValue().toString());
                                    Log.d("nnnn", "Total:" + total);
                                } else {
                                    boolean bl_search = false;
                                    for (int i = 0; i < searchList.size(); i++) {
                                        if (dd.getKey().toString().equals(searchList.get(i).toString())) {
                                            bl_search = true;
                                            search_n.set(i, Integer.parseInt(search_n.get(i).toString()) + 1);
                                        }
                                    }
                                    if (!bl_search) {
                                        searchList.add(dd.getKey());
                                        search_n.add(1);

                                    }
                                }
                            }


                        }
                        for (int j = 0; j < searchList.size(); j++) {
                            searchList.set(j, searchList.get(j) + ":" + search_n.get(j));
                        }

                        ListView listView_search = (ListView) vSearch.findViewById(R.id.listView_search);
                        ArrayAdapter adapter_search = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, searchList);
                        listView_search.setAdapter(adapter_search);
                        TextView tv_search = (TextView) vSearch.findViewById(R.id.tvSearch);
                        tv_search.setText("Total:" + total);
                        AlertDialog.Builder search = new AlertDialog.Builder(MainActivity.this);
                        search.setTitle("總單(All List)");
                        if (vSearch.getParent() != null) {
                            ((ViewGroup) vSearch.getParent()).removeView(vSearch);
                        }
                        search.setView(vSearch);
                        search.setPositiveButton("確定", null);
                        search.show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return true;
        }
        if (id == R.id.action_deleteorder) {
            if(nextBoss.equals(user)){
                odref = FirebaseDatabase.getInstance().getReference("OrderList");
                odref.child(nextChoose1).child(nextChoose2).removeValue();
                CloseNextOrder();
                orderFragment.onResume();
                Toast.makeText(this,"OK",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this,"非創建人無法刪除\nInsufficient permissions",Toast.LENGTH_SHORT).show();
            }
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
            ft.show(homeFragment)
                    .hide(orderFragment)
                    .hide(groupFragment)
                    .hide(addOrderFragment)
                    .hide(groupAddFragment)
                    .hide(nextOrderFragment)
                    .commit();

        } else if (id == R.id.nav_order) {
            ft.show(orderFragment).hide(groupFragment).hide(homeFragment).commit();
            setTitle("Order System");
            orderFragment.getMyGroupList();
        } else if (id == R.id.nav_group) {
            ft.show(groupFragment).hide(orderFragment).hide(homeFragment).commit();
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
                            .hide(homeFragment)
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
        addOrderFragment.setSpinner();
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

    //跳出餐點menu
    public void ShowNextOrder(){
        ft = fm.beginTransaction();
        ft.show(nextOrderFragment).hide(orderFragment).commit();
        nextOrderFragment.SetList();
    }
    //關閉點單
    public void CloseNextOrder(){
        ft = fm.beginTransaction();
        ft.hide(nextOrderFragment).show(orderFragment).commit();
    }

    public void SearchGroup(){
        //取自己有的群組
        user = sharedPreferences.getString("User",null);
        ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mygroup.clear();
                mydataList.clear();
                mydataList.add("User:"+user);
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().toString().equals(user)) {
                        for(DataSnapshot s: ds.child("group").getChildren()){
                            mygroup.add(s.getKey().toString());
                        }
                        for(DataSnapshot ss:ds.getChildren()){
                            if(!ss.getKey().toString().equals("password")) {
                                if(ss.getKey().toString().equals("group")){
                                    String str="" ;
                                    for(DataSnapshot sss:ss.getChildren()){
                                        str += (" " + sss.getKey());
                                    }
                                    mydataList.add(ss.getKey()+":"+str);

                                }else {
                                    mydataList.add(ss.getKey().toString() + ":" + ss.getValue());
                                }
                            }
                        }
                    }
                }

                //設置側選單群組列
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

    }

    public void ResetOrder(){
        orderFragment.onResume();
    }

    public void DeleteGroup(){
        bl_DGP = false;
        listView_DGP = (ListView)vDeleteGP.findViewById(R.id.listView_DeleteGroup);
        adapter_DGP = new ArrayAdapter(this,android.R.layout.simple_list_item_1,mygroup);
        listView_DGP.setAdapter(adapter_DGP);
        listView_DGP.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                gpref = FirebaseDatabase.getInstance().getReference("Group");
                gpref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(!bl_DGP) {
                            Log.d("nnnn", mygroup.toString());
                            groupBoss = dataSnapshot.child(mygroup.get(position).toString()).child("boss")
                                    .getValue().toString();

                            if (groupBoss.equals(user)) {
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(MainActivity.this);
                                builder2.setTitle("確定刪除群組嗎?Are you sure?");
                                builder2.setMessage("刪除後無法復原哦!\nCan not be restored after deleted!!");
                                builder2.setPositiveButton("取消", null);
                                builder2.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        gpref.child(mygroup.get(position).toString()).removeValue();
                                        Toast.makeText(MainActivity.this, "刪除成功!\nSuccessful!", Toast.LENGTH_SHORT).show();
                                        ref = FirebaseDatabase.getInstance().getReference("Users");
                                        ref.child(user).child("group").child(mygroup.get(position).toString()).removeValue();

                                    }
                                });
                                builder2.show();
                                SearchGroup();
                                adapter_DGP = new ArrayAdapter(MainActivity.this,android.R.layout.simple_list_item_1,mygroup);
                                listView_DGP.setAdapter(adapter_DGP);
                                bl_DGP =true;

                            } else {
                                Toast.makeText(MainActivity.this, "非創建人無法刪除\nInsufficient permissions", Toast.LENGTH_SHORT).show();
                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });



        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("選擇群組");
        if (vDeleteGP.getParent() != null) {
            ((ViewGroup) vDeleteGP.getParent()).removeView(vDeleteGP);
        }
        builder.setView(vDeleteGP);
        builder.setPositiveButton("取消",null);
        builder.show();






    }
}
