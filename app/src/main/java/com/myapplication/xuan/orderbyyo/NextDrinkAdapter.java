package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by auser on 2017/11/27.
 */

public class NextDrinkAdapter extends BaseAdapter {
    LayoutInflater layoutInflater;
    public static List list,suger,ice;
    Context context;
    public NextDrinkAdapter(Context c, List list){
        layoutInflater = LayoutInflater.from(c);
        this.context =c;
        this.list = list;
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
    public View getView(int position, View view, ViewGroup parent) {
        view = layoutInflater.inflate(R.layout.drink_list,null);
        suger = new ArrayList();
        ice = new ArrayList();
        for(Object ob : list){
            suger.add("正常(10)");
            ice.add("正常");
        }
        TextView textView_DK = (TextView) view.findViewById(R.id.tvDKname);
        textView_DK.setText(list.get(position).toString());

        Spinner spSuger = (Spinner) view.findViewById(R.id.spSuger);
        ArrayAdapter adapter_Suger = ArrayAdapter.createFromResource(context,
                R.array.suger,android.R.layout.simple_spinner_dropdown_item);
        spSuger.setAdapter(adapter_Suger);
        spSuger.setOnItemSelectedListener(sugerListener);
        Spinner spIce = (Spinner) view.findViewById(R.id.spIce);
        ArrayAdapter adapter_Ice = ArrayAdapter.createFromResource(context,
                R.array.Ice,android.R.layout.simple_spinner_dropdown_item);
        spIce.setAdapter(adapter_Ice);
        spIce.setOnItemSelectedListener(iceListener);
        return view;
    }

    Spinner.OnItemSelectedListener sugerListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            suger.set(position,parent.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
    Spinner.OnItemSelectedListener iceListener = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            ice.set(position,parent.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    public List getSuger(){
        return  suger;
    }
    public List getIce(){
        return ice;
    }
}
