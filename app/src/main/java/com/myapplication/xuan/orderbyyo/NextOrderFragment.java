package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ListView;
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
    private View view,vCheckOrder;
    private ListView listView_next;
    private ArrayAdapter adapter_next;
    private Button btnNextOK,btnNextC;
    public String choose1,choose2,OLG;
    private List nextList,myorderList,priceList,checklist,orderList_next;
    private int total=0,saveTotal=0;
    private TextView tvTotal;
    MainActivity activity;
    boolean bl,firstOD,ODcount;




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
        ref = FirebaseDatabase.getInstance().getReference("OrderList");
        view = inflater.inflate(R.layout.fragment_next_order, container, false);
        vCheckOrder = inflater.inflate(R.layout.orderchecklist,container,false);
        listView_next = (ListView)view.findViewById(R.id.listView_next);
        btnNextOK = (Button)view.findViewById(R.id.btnNextOK);
        btnNextC = (Button)view.findViewById(R.id.btnNextCancel);


        nextList = new ArrayList();
        myorderList =new ArrayList();
        priceList = new ArrayList();
        checklist = new ArrayList();
        orderList_next = new ArrayList();



        btnNextOK.setOnClickListener(btnNextListener);
        btnNextC.setOnClickListener(btnNextListener);

        return view;
    }


    @Override
    public void onResume() {
        SetList();
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
                case R.id.btnNextOK:
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

                        ListView lvCO =
                                (ListView) vCheckOrder.findViewById(R.id.lv_CheckOrder);
                        tvTotal = (TextView)vCheckOrder.findViewById(R.id.tvNextTotal);
                        tvTotal.setText("Total:"+total+"$");
                        final ArrayAdapter adapterCO =
                                new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, myorderList);
                        lvCO.setAdapter(adapterCO);

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

                                        //如果是第一次點此單,建立菜單
                                        if(!firstOD){
                                            ref.child(choose1).child(choose2).child("List")
                                                    .child(activity.user).child("total").setValue(0);
                                            int count =0;
                                            for(Object ob:nextList){
                                                Log.d("AAAAA",checklist.get(count).toString());
                                                if(checklist.get(count).equals(false)){
                                                    ref.child(choose1).child(choose2).child("List")
                                                            .child(activity.user).child(ob.toString()).setValue(0);
                                                }else if(checklist.get(count).equals(true)){
                                                    ref.child(choose1).child(choose2).child("List")
                                                            .child(activity.user).child(ob.toString()).setValue(1);
                                                }
                                                count++;
                                            }
                                            firstOD = true;
                                        }

                                        if(!bl) {
                                            if(!ODcount) {
                                                saveTotal=0;
                                                for (int i = 0; i < myorderList.size(); i++) {
                                                    if (i == (myorderList.size() - 1)) {
                                                        //設置自己訂單的價格
                                                        ref.child(choose1).child(choose2)
                                                                .child("List").child(activity.user).child("total")
                                                                .setValue(total);
                                                        saveTotal = total;
                                                        //設置總訂單價格
                                                        int t = 0;
                                                        t = Integer.parseInt(dataSnapshot.child(choose1).child(choose2).child("Total").getValue().toString());
                                                        t += total;
                                                        ref.child(choose1).child(choose2).child("Total").setValue(t);
                                                    }
                                                }


                                                }

                                                ODcount=true;
                                            }else{
                                                for (int i = 0; i < myorderList.size(); i++) {
                                                    if (i == (myorderList.size() - 1)) {
                                                        //設置自己訂單的價格
                                                        ref.child(choose1).child(choose2)
                                                                .child("List").child(activity.user).child("total")
                                                                .setValue(total);

                                                        //設置總訂單價格
                                                        int t = 0;
                                                        t = Integer.parseInt(dataSnapshot.child(choose1).child(choose2).child("Total").getValue().toString());
                                                        t -= saveTotal;
                                                        t += total;
                                                        saveTotal =0;
                                                        ref.child(choose1).child(choose2).child("Total").setValue(t);
                                                    }
                                                }

                                            }

                                            //=====================================================================================//

                                            //判斷是不是第一次點餐
                                        for(int i=0;i<nextList.size();i++) {
                                            int n = 0,choosen=0;
                                            //取此選項現在的數量

                                            n = Integer.parseInt(dataSnapshot
                                                    .child(choose1).child(choose2)
                                                    .child("List").child("All").child(nextList.get(i).toString())
                                                    .getValue().toString());

                                            if(checklist.get(i).equals(true)){
                                                ref.child(choose1).child(choose2)
                                                        .child("List").child(activity.user).child(nextList.get(i).toString())
                                                        .setValue(1);
                                                choosen =1;
                                            }else{
                                                ref.child(choose1).child(choose2)
                                                        .child("List").child(activity.user).child(nextList.get(i).toString())
                                                        .setValue(0);
                                                choosen =0;
                                            }
                                            n+=choosen;
                                            //設置此選項加點後的數量
                                            ref.child(choose1).child(choose2)
                                                    .child("List").child("All").child(nextList.get(i).toString())
                                                    .setValue(n);
                                                bl=true;
                                                SetList();
                                        }

                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                                activity.CloseAddOrder();

                            }
                        });
                        builder.show();
                    }
                    break;
                case R.id.btnNextCancel:
                    ((MainActivity)getActivity()).CloseNextOrder();
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
                total=0;
                choose1 = activity.nextChoose1;
                choose2 = activity.nextChoose2;
                Log.d("AASSS", choose1 + "=" + choose2);
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
                        android.R.layout.simple_list_item_checked,nextList);
                listView_next.setAdapter(adapter_next);
                listView_next.setOnItemClickListener(ODcheckListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
