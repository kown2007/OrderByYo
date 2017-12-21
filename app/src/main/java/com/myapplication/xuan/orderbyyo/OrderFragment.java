package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //============================================//
    private TextView orderFood,orderDrink;
    private DatabaseReference ref;
    private View view;

    private Button btnAddOrder;
    private List foodlist,drinklist,ODclickList,groupList,order_group_food,order_group_drink;
    private ListView orderlistView;
    private ArrayAdapter adapterOrder;
    public String myChoose="",clickChoose="",OLG;
    int tvChoose=0;
    public boolean bl=false;
    MainActivity activity;

    //============================================//

    private OnFragmentInteractionListener mListener;

    public OrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
     * @return A new instance of fragment OrderFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static OrderFragment newInstance(String param1, String param2) {
//        OrderFragment fragment = new OrderFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        activity =((MainActivity) getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_order, container, false);
        //setHasOptionsMenu(true);

        ref = FirebaseDatabase.getInstance().getReference("OrderList");
        orderFood = (TextView)view.findViewById(R.id.tvOrderFood);
        orderDrink = (TextView)view.findViewById(R.id.tvOrderDrink);
        btnAddOrder = (Button) view.findViewById(R.id.btnOrderadd);
        orderlistView = (ListView) view.findViewById(R.id.listView_Order);


        foodlist = new ArrayList();
        drinklist = new ArrayList();
        ODclickList = new ArrayList();
        groupList = new ArrayList();
        order_group_food = new ArrayList();
        order_group_drink = new ArrayList();
        return view;

    }

    @Override
    public void onResume() {


        if(tvChoose==1){
            setListFood();
        }else if(tvChoose==2){
            setListDrink();
        }

        ref.addValueEventListener(OrderVEL);
        orderFood.setOnClickListener(ClickFood);
        orderDrink.setOnClickListener(ClickFood);
        btnAddOrder.setOnClickListener(ClickBtnAdd);
        orderlistView.setOnItemClickListener(OrderListListener);

        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main,menu);
        super.onCreateOptionsMenu(menu,inflater);
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

    public ValueEventListener OrderVEL = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if(!bl) {
                foodlist.clear();
                drinklist.clear();
                order_group_food.clear();
                order_group_drink.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if (ds.getKey().toString().equals("Food")) {
                        for (DataSnapshot s : ds.getChildren()) {
                            for (Object ob : groupList) {
                                if (s.child("group").getValue().toString().equals(ob.toString())) {
                                    if (s.child("open").getValue().toString().equals("1")
                                            || s.child("boss").getValue().toString().equals(activity.user)) {
                                        order_group_food.add(s.child("group").getValue());
                                        foodlist.add(s.getKey().toString());
                                    }
                                }
                            }
                        }

                    } else if (ds.getKey().toString().equals("Drink")) {
                        for (DataSnapshot s : ds.getChildren()) {
                            for (Object ob : groupList) {
                                if (s.child("group").getValue().toString().equals(ob.toString())) {
                                    if (s.child("open").getValue().toString().equals(1)
                                            || s.child("boss").getValue().toString().equals(activity.user)) {
                                        order_group_drink.add(s.child("group").getValue());
                                        drinklist.add(s.getKey().toString());
                                    }
                                }
                            }
                        }

                    }
                }
                bl = true;
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    private View.OnClickListener ClickFood = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            bl = false;
            ref.addValueEventListener(OrderVEL);
            switch (v.getId()){
                case R.id.tvOrderFood:
                    setListFood();
                    break;
                case R.id.tvOrderDrink:
                    setListDrink();
                    break;
            }
        }
    };

    private View.OnClickListener ClickBtnAdd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(activity.mygroup.size()==0){
                Toast.makeText(getActivity(),"有群組才可以建立訂單!\nPlease join group first!",Toast.LENGTH_SHORT).show();
            }else {
                ((MainActivity) getActivity()).ShowAddOrder();
            }
        }
    };



    private AdapterView.OnItemClickListener OrderListListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //判斷現在位於哪個選單進而判斷選單選項
                    if(myChoose.equals("Food")){
                        clickChoose = foodlist.get(position).toString();

                    }else
                        if(myChoose.equals("Drink")){
                           clickChoose = drinklist.get(position).toString();
                        }



                    //取按下選項的詳細資料
                    ref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ODclickList.clear();
                                for(DataSnapshot ds:dataSnapshot.getChildren()){
                                    if(ds.getKey().toString().equals(myChoose)) {
                                        for (DataSnapshot s : ds.child(clickChoose).getChildren()) {
                                            if(s.getKey().toString().equals("group")){
                                                OLG = s.getValue().toString();    //取出此訂單是哪個群的
                                            }else if(s.getKey().toString().equals("open")) {
                                                activity.nextOpen=s.getValue().toString();
                                            }
                                            else{
                                                    ODclickList.add(s.getKey().toString());
                                                }

                                        }
                                    }
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    activity.nextChoose1 = myChoose;
                    activity.nextChoose2 = clickChoose;

                    activity.ShowNextOrder();
                    activity.nextOrderFragment.SetList();
                }
            };



    public void getMyGroupList(){
        groupList.clear();
        bl =false;
        for(Object ob:activity.mygroup){
                   groupList.add(ob.toString());
               }

        ref.addValueEventListener(OrderVEL);
        orderlistView.setOnItemClickListener(OrderListListener);

    }
    public void setListFood(){
        orderFood.setBackgroundColor(Color.parseColor("#66FFE6"));
        orderDrink.setBackgroundColor(Color.WHITE);
        for(int i=0;i<foodlist.size();i++){

        }
        adapterOrder = new ArrayAdapter(
                getActivity(),android.R.layout.simple_list_item_1,foodlist);
        orderlistView.setAdapter(adapterOrder);
        myChoose = "Food";
        tvChoose = 1;
    }
    public void setListDrink(){
        orderFood.setBackgroundColor(Color.WHITE);
        orderDrink.setBackgroundColor(Color.parseColor("#66FFE6"));
        adapterOrder = new ArrayAdapter(
                getActivity(),android.R.layout.simple_list_item_1,drinklist);
        orderlistView.setAdapter(adapterOrder);
        myChoose = "Drink";
        tvChoose = 2;
    }
    public void ResetOrder(){
        orderDrink.setBackgroundColor(Color.WHITE);
        orderFood.setBackgroundColor(Color.WHITE);
        adapterOrder = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1);
        orderlistView.setAdapter(adapterOrder);
        orderFood.setOnClickListener(ClickFood);
        orderDrink.setOnClickListener(ClickFood);
    }


}
