package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by auser on 2017/12/1.
 */

public class MyAdapter extends BaseAdapter {
    List list;
    LayoutInflater layoutInflater;
    Context context;
    public MyAdapter(Context c,List list){
        this.layoutInflater = LayoutInflater.from(c);
        this.list = list;
        this.context = c;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.my_delete_list,null);
        TextView tv = (TextView)convertView.findViewById(R.id.tv_DeleteList);
        RadioButton rb = (RadioButton)convertView.findViewById(R.id.rb_DeleteList);
        //RadioGroup rg = (RadioGroup)convertView.findViewById(R.id.rg_DeleteList);
        tv.setText(list.get(position).toString());
        rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(MainActivity.rb_n != -1){
                    if(buttonView != null){
                        buttonView.setChecked(false);
                    }
                }
                MainActivity.rb_n = position;
            }
        });

        if(MainActivity.rb_n == position){
            rb.setChecked(true);
        }else{
            rb.setChecked(false);
        }

        return convertView;
    }
}
