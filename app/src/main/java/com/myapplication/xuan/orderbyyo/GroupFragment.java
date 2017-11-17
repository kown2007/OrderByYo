package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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
    private View view;
    private ListView listView;
    private List grouplist,mygroup;
    private ArrayAdapter adapter;
    private String user;
    private SharedPreferences sharedPreferences;
    private Button btnAddGroup;



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
        setHasOptionsMenu(true);
        ref = FirebaseDatabase.getInstance().getReference("Group");
        mref = FirebaseDatabase.getInstance().getReference("Users");
        GroupMy = (TextView)view.findViewById(R.id.tvGroupMy);
        GroupAll = (TextView) view.findViewById(R.id.tvGroupAll);
        listView = (ListView) view.findViewById(R.id.listView_Group);
        btnAddGroup = (Button) view.findViewById(R.id.btnGroupAdd);

        GroupMy.setOnClickListener(tvListener);
        GroupAll.setOnClickListener(tvListener);
        btnAddGroup.setOnClickListener(btnListener);
        grouplist = new ArrayList();
        mygroup = new ArrayList();



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        sharedPreferences =this.getActivity().getSharedPreferences("userList", Context.MODE_PRIVATE);
        user = sharedPreferences.getString("User",null);

        /////尋覽使用者的群組
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mygroup.clear();
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    if(ds.getKey().toString().equals(user)) {
                        for(DataSnapshot s: ds.child("group").getChildren()){
                            mygroup.add(s.getKey().toString());
                            Log.d("MMMM", s.toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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

//        adapter = new ArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1,mygroup);
//        listView.setAdapter(adapter);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.group,menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                case R.id.tvGroupMy:
                    GroupMy.setBackgroundColor(Color.parseColor("#66FFE6"));
                    GroupAll.setBackgroundColor(Color.WHITE);
                    adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,mygroup);
                    listView.setAdapter(adapter);
                    break;
                case R.id.tvGroupAll:
                    GroupMy.setBackgroundColor(Color.WHITE);
                    GroupAll.setBackgroundColor(Color.parseColor("#66FFE6"));
                    adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,grouplist);
                    listView.setAdapter(adapter);
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
}
