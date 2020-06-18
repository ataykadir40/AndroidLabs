package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    ArrayList<Message> list = new ArrayList<>();
    private MyListAdapter myAdapter=new MyListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ListView myList = findViewById(R.id.theListView);
        myList.setAdapter(myAdapter);

        Button addButton = findViewById(R.id.addButton3);
        addButton.setOnClickListener( click -> {
            EditText typeField = findViewById(R.id.text11);
            Message msg = new Message(typeField.getText().toString(), "send");
            list.add(msg);
            typeField.setText(null);
            myAdapter.notifyDataSetChanged();
            //myList.setAdapter(new MyListAdapter());
        });

        Button addButtonnew = findViewById(R.id.addButton4);
        addButtonnew.setOnClickListener( click -> {
            EditText typeField = findViewById(R.id.text11);
            Message msg = new Message(typeField.getText().toString(), "receive");
            list.add(msg);
            typeField.setText(null);
            myAdapter.notifyDataSetChanged();
           // myList.setAdapter(new MyListAdapter());
        });

        //myList.setAdapter( myAdapter = new MyListAdapter());
        myList.setOnItemLongClickListener( (parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this?")
                    .setMessage("The selected row is: " + (position+1) + "\nThe database id is: " + (myAdapter.getItemId(position)+1) )
                    .setPositiveButton("Yes", (click, arg) -> {
                        list.remove(position);
                        myAdapter.notifyDataSetChanged();
//                        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresher);
//                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
//                        {
//                            @Override
//                            public void onRefresh()
//                            {
//                                myList.setAdapter(new MyListAdapter());
//                            }
//                        });
                    })
                    .setNegativeButton("No",  (click, arg) -> {
                        Toast.makeText(ChatRoomActivity.this, "Nothing changed", Toast.LENGTH_LONG).show();})
                    .create().show();
            return true;
        }   );
    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() { return list.size();}

        public Object getItem(int position) { return list.get(position); }

        public long getItemId(int position) { return (long) position; }

        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();

            Message msg = (Message)getItem(position);

            if(msg.getType().equals("send")){
                View newView = inflater.inflate(R.layout.activity_send, null);

                TextView tView = newView.findViewById(R.id.Textview12);
                tView.setText(msg.getMessage());

                //return it to be put in the table
                return newView;
            } else{
                View newView = inflater.inflate(R.layout.activity_receive, null);

                TextView tView = newView.findViewById(R.id.Textview13);
                tView.setText(msg.getMessage());

                //return it to be put in the table
                return newView;
            }
            //set what the text should be for this row:
        }
}}