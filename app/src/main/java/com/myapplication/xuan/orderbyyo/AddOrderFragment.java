package com.myapplication.xuan.orderbyyo;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //////////////////////////////////////////////////////
    MainActivity activity;
    DatabaseReference ref;
    ListView listView_AO;
    EditText title_name,item_name,item_price;
    ImageButton imb;
    Button btnOK,btnNO;
    Spinner spinner,spinner_kind;
    List newlist,namelist,priceList;
    ArrayAdapter adapter,spadapter,kindadapter;
    String kind="",name="",price="",group="";

    //////////////////////////////////////////////////////
    private OnFragmentInteractionListener mListener;

    public AddOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddOrderFragment newInstance(String param1, String param2) {
        AddOrderFragment fragment = new AddOrderFragment();
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

        activity = ((MainActivity)getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_add_order, container, false);
        ref = FirebaseDatabase.getInstance().getReference("OrderList");

        listView_AO = (ListView)v.findViewById(R.id.listView_AddOrder);
        title_name = (EditText)v.findViewById(R.id.etAddOrder_Title);
        item_name = (EditText)v.findViewById(R.id.etAOItemName);
        item_price = (EditText)v.findViewById(R.id.etAOItemPrice);
        spinner = (Spinner)v.findViewById(R.id.spinner_AOGroup);
        spinner_kind = (Spinner)v.findViewById(R.id.spinner_kind);
        btnOK = (Button)v.findViewById(R.id.btnAddOrder_OK);
        btnNO = (Button)v.findViewById(R.id.btnAddOrder_NO);
        imb = (ImageButton)v.findViewById(R.id.imbAOadd);

        newlist= new ArrayList();
        namelist = new ArrayList();
        priceList = new ArrayList();

        btnOK.setOnClickListener(btnListener);
        btnNO.setOnClickListener(btnListener);
        imb.setOnClickListener(btnListener);
        //setSpinner();
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
            name = item_name.getText().toString();
            price = item_price.getText().toString();
            switch (v.getId()){
                case R.id.btnAddOrder_OK:
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("確定新增點單?(Add Order?)");
                    builder.setPositiveButton("No",null);
                    builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ref.child(kind).child(title_name.getText().toString())
                                    .child("group").setValue(group);
                            ref.child(kind).child(title_name.getText().toString())
                                    .child("boss").setValue(activity.user);
                            for (int i = 0; i < namelist.size(); i++) {
                                ref.child(kind).child(title_name.getText().toString())
                                        .child("menu")
                                        .child(namelist.get(i).toString())
                                        .setValue(priceList.get(i).toString());
                            }
                            title_name.setText("");
                            adapter.clear();
                            listView_AO.setAdapter(adapter);
                            activity.ResetOrder();
                            activity.CloseAddOrder();
                        }
                    });
                    builder.show();

                    break;
                case R.id.btnAddOrder_NO:
                    activity.CloseAddOrder();
                    break;

                case R.id.imbAOadd:
                    if(name.equals("")||price.equals("")){
                        Toast.makeText(getActivity(),"欄位請勿空白!\n Don't blank the field!",Toast.LENGTH_SHORT).show();
                    }else{
                        namelist.add(name);
                        priceList.add(price);
                        newlist.add(name+":"+price);
                        adapter=new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,newlist);
                        listView_AO.setAdapter(adapter);
                        item_name.setText("");
                        item_price.setText("");
                    }
                    break;

            }

        }
    };

    public void setSpinner(){
        namelist.clear();
        priceList.clear();

        kindadapter = ArrayAdapter.createFromResource(getActivity(),R.array.Kind,android.R.layout.simple_spinner_dropdown_item);
        spinner_kind.setAdapter(kindadapter);
        spinner_kind.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    kind = "Food";
                }else{
                    kind = "Drink";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spadapter = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_dropdown_item,activity.mygroup);
        spinner.setAdapter(spadapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                group = activity.mygroup.get(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}
