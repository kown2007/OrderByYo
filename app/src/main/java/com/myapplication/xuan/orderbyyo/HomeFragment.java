package com.myapplication.xuan.orderbyyo;

import android.content.Context;
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
import android.widget.ImageButton;
import android.widget.ListView;

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
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    /////////////////////////////////////////////////////

    View home;
    ArrayAdapter adapter_bu;
    List selectvieworder,myorderall;
    ListView listViewHome;
    ArrayAdapter adapter;
    DatabaseReference mref;
    MainActivity activity;
    ImageButton imb;


    /////////////////////////////////////////////////////

    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        activity = (MainActivity)getActivity();
        View v =  inflater.inflate(R.layout.fragment_home, container, false);

        selectvieworder = new ArrayList();
        myorderall = new ArrayList();

        listViewHome = (ListView) v.findViewById(R.id.listView_Home);

        home = inflater.inflate(R.layout.home_select_view,null);
        imb=(ImageButton)v.findViewById(R.id.imgBtnHome);
        imb.setOnClickListener(imbListener);

        return v;
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


    public void SetHomeListView(){
        mref = FirebaseDatabase.getInstance().getReference("OrderList");
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                myorderall.clear();
                for(DataSnapshot ds1:dataSnapshot.getChildren()){//Food&Drink

                    for(DataSnapshot ds2:ds1.getChildren()){//店家名稱

                        for(DataSnapshot ds3:ds2.child("List").getChildren()){//List裡所有訂單

                            if(ds3.getKey().toString().equals(activity.user)){
                                myorderall.add(ds2.getKey().toString());
                            }
                        }
                    }
                }
                //==================第一次尋覽結束========================//
                adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,myorderall);
                listViewHome.setAdapter(adapter);

                listViewHome.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.d("nnnnn",""+position);
                        selectvieworder.clear();
                        int n=0;
                        for(DataSnapshot d:dataSnapshot.getChildren()){//d:Food,Drink
                            for(DataSnapshot d2:d.getChildren()){//d2:店家名稱
                                if(d2.getKey().toString().equals(myorderall.get(position).toString())){
                                    for(DataSnapshot d3:d2.child("List").child(activity.user).getChildren()){
                                        selectvieworder.add(d3.getKey().toString()+":"+
                                        d3.getValue());

                                    }
                                }
                            }
                            n++;
                        }
                        adapter_bu = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,selectvieworder);
                        ListView listView_bu =(ListView) home.findViewById(R.id.listView_homeSelect);
                        listView_bu.setAdapter(adapter_bu);

                        AlertDialog.Builder buSelect = new AlertDialog.Builder(getActivity());
                        buSelect.setTitle("訂單詳細");
                        if (home.getParent() != null) {
                            ((ViewGroup) home.getParent()).removeView(home);
                        }
                        buSelect.setView(home);
                        buSelect.setPositiveButton("確定", null);
                        buSelect.show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    View.OnClickListener imbListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SetHomeListView();
        }
    };
}
