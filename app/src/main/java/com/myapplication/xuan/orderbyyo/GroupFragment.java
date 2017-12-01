package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.widget.EditText;
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
 * {@link GroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    ///////////////////////////
    private TextView GroupMy,GroupAll;
    private DatabaseReference ref;
    private DatabaseReference mref;
    private View view,vpasscheck;
    private ListView listView;
    private List grouplist,mygroup;
    private ArrayAdapter adapter;
    private String user;
    private SharedPreferences sharedPreferences;
    private Button btnAddGroup;
    private EditText etPC;
    MainActivity activity;
    MenuInflater menuInflater;
    int gpChoose =0;




    ///////////////////////////
    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
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
        view = inflater.inflate(R.layout.fragment_group, container, false);
        vpasscheck = inflater.inflate(R.layout.passcheck,container,false);
        setHasOptionsMenu(true);
        ref = FirebaseDatabase.getInstance().getReference("Group");
        mref = FirebaseDatabase.getInstance().getReference("Users");
        GroupMy = (TextView)view.findViewById(R.id.tvGroupMy);
        GroupAll = (TextView) view.findViewById(R.id.tvGroupAll);
        listView = (ListView) view.findViewById(R.id.listView_Group);
        btnAddGroup = (Button) view.findViewById(R.id.btnGroupAdd);


        grouplist = new ArrayList();
        mygroup = new ArrayList();
        etPC = (EditText) vpasscheck.findViewById(R.id.etCheckPassword);

        activity = (MainActivity)getActivity();



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences =this.getActivity().getSharedPreferences("userList", Context.MODE_PRIVATE);
        user = sharedPreferences.getString("User",null);
        activity.SearchGroup();
        //取出使用者群組
        mygroup = ((MainActivity)getActivity()).mygroup;


        //尋覽所有群組
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                grouplist.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    grouplist.add(ds.getKey().toString());
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Log.d("nnnn",""+gpChoose+":"+mygroup);
        if(gpChoose ==1){
            setMyGroupList();
        }else if(gpChoose ==2){
            setAllGroupList();
        }

        GroupMy.setOnClickListener(tvListener);
        GroupAll.setOnClickListener(tvListener);
        btnAddGroup.setOnClickListener(btnListener);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.group,menu);
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

    View.OnClickListener tvListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tvGroupMy://選取我的群組時
                    setMyGroupList();

                    break;
                case R.id.tvGroupAll://選取所有群組時
                    setAllGroupList();

                    break;
            }

        }
    };

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((MainActivity)getActivity()).ShowAddGroup();
        }
    };

    public void setMyGroupList(){
        GroupMy.setBackgroundColor(Color.parseColor("#66FFE6"));
        GroupAll.setBackgroundColor(Color.WHITE);
        adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,mygroup);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder buout = new AlertDialog.Builder(getActivity());
                buout.setTitle("要退出群組嗎?");
                buout.setMessage("退出後將無法再訂此群組的單哦!");
                buout.setPositiveButton("No",null);
                buout.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder buout2 = new AlertDialog.Builder(getActivity());
                        buout2.setTitle("確定退出群組?");
                        buout2.setPositiveButton("No",null);
                        buout2.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mref.child(activity.user).child("group").child(mygroup.get(position).toString())
                                        .removeValue();
                                activity.SearchGroup();
                                adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,mygroup);
                                listView.setAdapter(adapter);
                            }
                        });
                        buout2.show();
                    }
                });
                buout.show();
            }
        });
        gpChoose=1;
    }

    public void setAllGroupList(){
        GroupMy.setBackgroundColor(Color.WHITE);
        GroupAll.setBackgroundColor(Color.parseColor("#66FFE6"));
        adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,grouplist);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override/////第一個對話視窗/////////////////////////////////////////////////////
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("要申請加入群組嗎?");
                builder.setMessage("Do you want to join this group?");
                builder.setPositiveButton("No",null);
                builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    @Override////第二個對話視窗/////////////////////////////////////////////
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
                        //////////////////////////////////////////////////////////////
                        if(vpasscheck.getParent() != null){
                            ((ViewGroup)vpasscheck.getParent()).removeView(vpasscheck);
                        }
                        //////////////////////////////////////////////////////////////
                        builder2.setView(vpasscheck);
                        builder2.setTitle("請輸入群組密碼");
                        builder2.setPositiveButton("取消",null);
                        builder2.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ref.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                                            if(ds.getKey().toString().equals(grouplist.get(position))){
                                                /////////////////判斷密碼正不正確////////////////////////
                                                if(etPC.getText().toString().equals(ds.child("password").getValue().toString())){
                                                    mref.child(user).child("group")
                                                            .child(grouplist.get(position).toString())
                                                            .setValue(grouplist.get(position));
                                                    Toast.makeText(getActivity(),"成功!!Successful!!",Toast.LENGTH_SHORT).show();
                                                }else {
                                                    Toast.makeText(getActivity(),"密碼錯誤!!Wrong!!",Toast.LENGTH_SHORT).show();
                                                }

                                                etPC.setText("");
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });
                        builder2.show();
                    }
                });
                builder.show();
            }
        });
        gpChoose=2;
    }

    public void ResetListView(){
        GroupMy.setBackgroundColor(Color.WHITE);
        GroupAll.setBackgroundColor(Color.WHITE);
        adapter.clear();
        listView.setAdapter(adapter);
        GroupMy.setOnClickListener(tvListener);
        GroupAll.setOnClickListener(tvListener);

    }
}
