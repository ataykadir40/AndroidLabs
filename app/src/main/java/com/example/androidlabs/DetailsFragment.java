package com.example.androidlabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {
    private Bundle dataFromActivity;
    private String messageText;
    private String messenger;
    private long id;
    private AppCompatActivity parentActivity;

    private boolean isTablet;
    public void setTablet(boolean tablet) { isTablet = tablet; }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        dataFromActivity = getArguments();
        id = dataFromActivity.getLong( ChatRoomActivity.ITEM_ID );
        messageText = dataFromActivity.getString(ChatRoomActivity.ITEM_SELECTED);

        View result =  inflater.inflate(R.layout.fragment_details, container, false);

        //show the message
        TextView message = (TextView) result.findViewById(R.id.fMessageText);
        message.setText(messageText);

        //show the id:
        TextView idView = (TextView)result.findViewById(R.id.fMessageId);
        idView.setText(String.valueOf(id));

        CheckBox isSender = (CheckBox) result.findViewById(R.id.checkBox);
        isSender.setChecked(dataFromActivity.getBoolean(ChatRoomActivity.ITEM_ISSEND));

        Button hideBtn = (Button)result.findViewById(R.id.hide);
        hideBtn.setOnClickListener( clk -> {
            if(isTablet)
            {
                ChatRoomActivity parent = (ChatRoomActivity)getActivity();
                parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
            }
            else //isPhone
            {
                EmptyActivity parent = (EmptyActivity)getActivity();
                Intent goBack = new Intent();
                goBack.putExtra(ChatRoomActivity.ITEM_ID, dataFromActivity.getLong(ChatRoomActivity.ITEM_ID)); //send data to next activity
                parent.setResult(Activity.RESULT_OK, goBack);
                parent.finish();
            }

        });

        return result;

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_details, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //context will either be FragmentExample for a tablet, or EmptyActivity for phone
        AppCompatActivity parentActivity = (AppCompatActivity)context;
    }
}