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
    private String choose1,choose2,OLG;
    private List nextList,myorderList,priceList,checklist;
    private int total;




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

        btnNextOK.setOnClickListener(btnNextListener);
        btnNextC.setOnClickListener(btnNextListener);

        return view;
    }


    @Override
    public void onResume() {


        //取按下選項的詳細資料
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nextList.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().toString().equals(choose1)) {
                        for (DataSnapshot s : ds.child(choose2).getChildren()) {
                            if(s.getKey().toString().equals("group")){
                                OLG = s.getValue().toString();    //取出此訂單是哪個群的
                            }else {
                                nextList.add(s.getKey().toString()+" : "+s.getValue()+"$");
                                priceList.add(s.getValue());
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
                    ListView lvCO =
                            (ListView) vCheckOrder.findViewById(R.id.lv_CheckOrder);
                    ArrayAdapter adapterCO =
                            new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,myorderList);
                    lvCO.setAdapter(adapterCO);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Order List(您的餐點)");
                    //
                    if(vCheckOrder.getParent() != null){
                        ((ViewGroup)vCheckOrder.getParent()).removeView(vCheckOrder);
                    }
                    //
                    builder.setView(vCheckOrder);
                    builder.setPositiveButton("取消",null);
                    builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.show();

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
        for(int i=0;i<nextList.size();i++){
            if(checklist.get(i).equals(true)){
                myorderList.add(nextList.get(i));
                total +=  Integer.parseInt(priceList.get(i).toString());
            }
        }
    }

}
