package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NextOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NextOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NextOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ///////////////////////////////////////////////////////////////
    private DatabaseReference ref;
    private View view,vCheckOrder,vAddItem,vSP;
    private ListView listView_next;
    private ArrayAdapter adapter_next;
    private Button btnNextOK,btnNextC,btnNextAddItem;
    public String choose1="",choose2="",OLG;
    List suger,ice,save;
    private List nextList,myorderList,priceList,checklist,orderList_next,addItemSaveList,chchList;
    private int total=0,saveTotal=0,saveChoose=0;
    private TextView tvTotal;
    MainActivity activity;
    boolean bl,firstOD,ODcount,bladd;




    ///////////////////////////////////////////////////////////////

    private OnFragmentInteractionListener mListener;

    public NextOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NextOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NextOrderFragment newInstance(String param1, String param2) {
        NextOrderFragment fragment = new NextOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        activity = (MainActivity)getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //路徑設定
        ref = FirebaseDatabase.getInstance().getReference("OrderList");
        view = inflater.inflate(R.layout.fragment_next_order, container, false);
        vCheckOrder = inflater.inflate(R.layout.orderchecklist,container,false);
        vAddItem = inflater.inflate(R.layout.next_add_item,container,false);
        vSP = inflater.inflate(R.layout.drink_list,container,false);
        //元件設定
        listView_next = (ListView)view.findViewById(R.id.listView_next);
        btnNextOK = (Button)view.findViewById(R.id.btnNextOK);
        btnNextC = (Button)view.findViewById(R.id.btnNextCancel);
        btnNextAddItem = (Button)view.findViewById(R.id.btnNextAddItem);

        //List設定
        nextList = new ArrayList();
        myorderList =new ArrayList();
        priceList = new ArrayList();
        checklist = new ArrayList();
        orderList_next = new ArrayList();
        addItemSaveList = new ArrayList();
        chchList = new ArrayList();
        suger = new ArrayList();
        ice = new ArrayList();

        //按鈕監聽器設定
        btnNextOK.setOnClickListener(btnNextListener);
        btnNextC.setOnClickListener(btnNextListener);
        btnNextAddItem.setOnClickListener(btnNextListener);

        return view;
    }


    @Override
    public void onResume() {
        SetList();
        //取SharePrefrernces資料
        super.onResume();
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    View.OnClickListener btnNextListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.btnNextOK://如果是按OK鍵的話
                    Total();
                    bl =false;
                    if(total==0){
                        AlertDialog.Builder builder0 = new AlertDialog.Builder(getActivity());
                        builder0.setTitle("No Order(沒有點任何餐)");
                        builder0.setMessage("您沒有點任何餐!!(Order nothing)");
                        builder0.setPositiveButton("取消", null);
                        builder0.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                activity.CloseNextOrder();
                            }
                        });
                        builder0.show();
                    }else {
                        final NextDrinkAdapter nextDrinkAdapter = new NextDrinkAdapter(getActivity(),myorderList);
                        ListView lvCO =
                                (ListView) vCheckOrder.findViewById(R.id.lv_CheckOrder);
                        tvTotal = (TextView)vCheckOrder.findViewById(R.id.tvNextTotal);
                        tvTotal.setText("Total:"+total+"$");
                        //判斷是點哪類而設定Adapter
                        if(choose1.toString().equals("Food")){
                            ArrayAdapter adapterCO =
                                    new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, myorderList);
                            lvCO.setAdapter(adapterCO);
                        }else if(choose1.toString().equals("Drink")){

                            lvCO.setAdapter(nextDrinkAdapter);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Order List(您的餐點)");
                        //
                        if (vCheckOrder.getParent() != null) {
                            ((ViewGroup) vCheckOrder.getParent()).removeView(vCheckOrder);
                        }
                        //
                        builder.setView(vCheckOrder);
                        builder.setPositiveButton("取消", null);
                        builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        SetList();
                                    if(!bl) {
                                        //建立/建立點單
                                        ref.child(choose1).child(choose2).child("List")
                                                .child(activity.user).child("total").setValue(total);
                                        int count = 0;
                                        suger = nextDrinkAdapter.getSuger();
                                        ice = nextDrinkAdapter.getIce();
                                        for (Object ob : nextList) {
                                            if(choose1.equals("Drink")){
                                                nextList.set(count,
                                                        nextList.get(count)+","+suger.get(count)+","+ice.get(count));
                                            }
                                            if (checklist.get(count).equals(false)) {
                                                ref.child(choose1).child(choose2).child("List")
                                                        .child(activity.user).child(ob.toString()).setValue(0);
                                            } else if (checklist.get(count).equals(true)) {
                                                ref.child(choose1).child(choose2).child("List")
                                                        .child(activity.user).child(ob.toString()).setValue(1);
                                            }
                                            count++;
                                        }
                                        //=====================================================================================//
                                        bl=true;
                                     }//bl
                                    }//onData


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                activity.CloseNextOrder();//點完餐後關閉點單

                            }
                        });
                        builder.show();
                    }
                    break;
                case R.id.btnNextCancel://如果是按取消鍵的話
                    ((MainActivity)getActivity()).CloseNextOrder();
                    break;
                case R.id.btnNextAddItem://如果按新增選項鈕
                    BtnAddItem();
                    break;
            }
        }
    };

    AdapterView.OnItemClickListener ODcheckListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(checklist.get(position).equals(false)){
                ((CheckedTextView) view).setChecked(true);
                checklist.set(position,true);
            }else{
                ((CheckedTextView) view).setChecked(false);
                checklist.set(position,false);
            }
        }
    };

    public void Total(){
        myorderList.clear();
        total = 0;
        for(int i=0;i<nextList.size();i++){
            if(checklist.get(i).equals(true)){
                myorderList.add(nextList.get(i));
                total +=  Integer.parseInt(priceList.get(i).toString());
            }
        }
    }

    public void SetList(){
        //取按下選項的詳細資料
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nextList.clear();
                checklist.clear();
                priceList.clear();
                total=0;
                    choose1 = activity.nextChoose1;
                    choose2 = activity.nextChoose2;
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().toString().equals(choose1)) {
                        for (DataSnapshot s : ds.child(choose2).getChildren()) {
                            if(s.getKey().toString().equals("group")){
                                OLG = s.getValue().toString();    //取出此訂單是哪個群的
                            }else
                                if(s.getKey().toString().equals("menu")){
                                for(DataSnapshot ss:s.getChildren()) {
                                    nextList.add(ss.getKey().toString());
                                    priceList.add(ss.getValue());
                                }
                            }
                        }
                    }
                }
                for(Object ob:nextList){
                    checklist.add(false);
                }

                    adapter_next = new ArrayAdapter(getActivity(),
                            android.R.layout.simple_list_item_checked, nextList);
                    listView_next.setAdapter(adapter_next);
                    listView_next.setOnItemClickListener(ODcheckListener);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void BtnAddItem(){
        final EditText etNAIN = (EditText)vAddItem.findViewById(R.id.etNtAdItName);
        final EditText etNAIP = (EditText)vAddItem.findViewById(R.id.etNtAdItPrice);
        AlertDialog.Builder builderA = new AlertDialog.Builder(getActivity());
        builderA.setTitle("Order List(您的餐點)");
        //
        if (vAddItem.getParent() != null) {
            ((ViewGroup) vAddItem.getParent()).removeView(vAddItem);
        }
        //
        bladd =false;
        builderA.setView(vAddItem);
        builderA.setPositiveButton("取消", null);
        builderA.setNegativeButton("確定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //若二者不為空白，新增選項到menu,List-ALL
                if(!etNAIN.getText().toString().equals(null)&&
                        !etNAIP.getText().toString().equals(null) ) {
                    String etAdName = etNAIN.getText().toString();
                    int etPrice = Integer.parseInt(etNAIP.getText().toString());
                    //新增menu的選項
                    ref.child(choose1).child(choose2).child("menu")
                            .child(etAdName).setValue(etPrice);
                    //新增All的選項
                    ref.child(choose1).child(choose2).child("List")
                            .child("All").child(etAdName).setValue(0);
                    //清空兩個欄位
                    etNAIN.setText("");
                    etNAIP.setText("");
                }
                //即時監聽器
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //判斷有沒有點餐過
                        for(DataSnapshot ds:dataSnapshot.child(choose1)
                                .child(choose2).child("List").getChildren()){
                            if (ds.getKey().toString().equals(activity.user)) {
                                firstOD=true;
                                ODcount =true;
                            }else{
                                firstOD = false;
                                ODcount = false;
                            }
                        }
                    if(!bladd) {
                        //如果點餐過的話
                        if (firstOD) {
                            //取出暫存的以點過的菜單對應
                            orderList_next.clear();
                            for (int o = 0; o < saveChoose; o++) {
                                orderList_next.add(activity.sharedPreferences.getString("Order" + o, null));
                            }
                            //取出以點菜單的項目名稱
                            addItemSaveList.clear();
                            for (int r = 0; r < orderList_next.size(); r++) {
                                if (orderList_next.size() > 0) {
                                    if (orderList_next.get(r).toString().equals("true")) {
                                        addItemSaveList.add(nextList.get(r).toString());
                                    }
                                }
                            }
                            SetList();//重新設定選單
                            chchList.clear();//轉接的List清除
                            for (Object oj : checklist) {
                                chchList.add(false);//全部放入false
                            }
                            //將舊的點單的名字 對應 現在menu的位置
                            //並將對應的位置存入舊的選取紀錄
                            for (int k = 0; k < checklist.size(); k++) {
                                if (addItemSaveList.size() > 0) {
                                    for (int c = 0; c < addItemSaveList.size(); c++) {
                                        if (nextList.get(k).toString().equals(addItemSaveList.get(c).toString())) {
                                            chchList.set(k, orderList_next.get(c).toString());
                                        }
                                    }
                                }
                            }
                            //將更新完的選取紀錄儲存
                            for (int x = 0; x < checklist.size(); x++) {
                                activity.editor.putString("Order" + x, chchList.get(x).toString());
                            }

                            activity.editor.commit();//sharedPreferences儲存資料
                            saveChoose = checklist.size();//這次的size儲存
                        }
                       // ListReset();
                        bladd=true;
                    }
                        Total();//價格List重新設定

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });//ref
            }//onClick
        });//NegativeButton
        builderA.show();
    }

}
