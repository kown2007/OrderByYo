package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.acl.Group;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupAddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GroupAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupAddFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MainActivity activity;
    ////////////////////////////////////////////////////
    EditText etGroupAdd_name,etGroupAdd_Password;
    String newgpName,newgpPassword;
    Button btnOK,btnNO;
    DatabaseReference ref,mref;

    ///////////////////////////////////////////



    private OnFragmentInteractionListener mListener;

    public GroupAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GroupAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupAddFragment newInstance(String param1, String param2) {
        GroupAddFragment fragment = new GroupAddFragment();
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
        activity = ((MainActivity) getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_group_add, container, false);
        etGroupAdd_name = (EditText)v.findViewById(R.id.etAddGroup_Name);
        etGroupAdd_Password = (EditText)v.findViewById(R.id.etAddGroup_Password);
        btnOK = (Button)v.findViewById(R.id.btnAddGroup_OK);
        btnNO = (Button)v.findViewById(R.id.btnAddGroup_No);

        ref = FirebaseDatabase.getInstance().getReference("Group");
        mref = FirebaseDatabase.getInstance().getReference("Users");

        btnOK.setOnClickListener(btnListener);
        btnNO.setOnClickListener(btnListener);
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

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            newgpName = etGroupAdd_name.getText().toString();
            newgpPassword = etGroupAdd_Password.getText().toString();
            switch (v.getId()){
                case R.id.btnAddGroup_OK:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("確定新增群組?");
                    builder.setMessage("群組名稱 : "+newgpName+"\n"+"密碼 : "+newgpPassword);
                    builder.setPositiveButton("取消", null);
                    builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ref.child(newgpName).child("password").setValue(newgpPassword);//新增到全部群組
                            mref.child(activity.user).child("group").child(newgpName).setValue(newgpName);//把自己加入群組
                            activity.CloseAddGroup();
                        }
                    });
                    builder.show();
                    break;
                case R.id.btnAddGroup_No:
                    activity.CloseAddGroup();
                    break;
            }
        }
    };
}
