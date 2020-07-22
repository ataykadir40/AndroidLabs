package com.example.androidlabs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ChatRoomActivity extends AppCompatActivity {

    public static final String ITEM_SELECTED = "ITEM";
    public static final String ITEM_POSITION = "POSITION";
    public static final String ITEM_ISSEND = "ISSEND";
    public static final String ITEM_ID = "ID";

    ArrayList<Message> msgList = new ArrayList<>();
    private MyListAdapter myAdapter=new MyListAdapter();
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        ListView myList = findViewById(R.id.theListView);
        boolean isTablet = findViewById(R.id.fragmentLocation) != null;
        FrameLayout frameLayout = findViewById(R.id.fragmentLocation);
        loadDataFromDatabase();
        myList.setAdapter(myAdapter);


        myList.setOnItemClickListener((list, item, position, id) -> {
            //Create a bundle to pass data to the new fragment
            Bundle dataToPass = new Bundle();
            dataToPass.putString(ITEM_SELECTED, msgList.get(position).getMessage());
            dataToPass.putInt(ITEM_POSITION, position);
            dataToPass.putBoolean(ITEM_ISSEND, msgList.get(position).isSend());
            dataToPass.putLong(ITEM_ID, msgList.get(position).getId());

            if(isTablet)
            {
                DetailsFragment dFragment = new DetailsFragment(); //add a DetailFragment
                dFragment.setTablet(true);
                dFragment.setArguments( dataToPass ); //pass it a bundle for information
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentLocation, dFragment) //Add the fragment in FrameLayout
                        .commit(); //actually load the fragment.
            }
            else //isPhone
            {
                Intent nextActivity = new Intent(ChatRoomActivity.this, EmptyActivity.class);
                nextActivity.putExtras(dataToPass); //send data to next activity
                startActivity(nextActivity); //make the transition
            }
        });

        Button addButton = findViewById(R.id.addButton3);
        addButton.setOnClickListener( click -> {

            EditText typeField = findViewById(R.id.text11);

            String message = typeField.getText().toString();

            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();

            //Now provide a value for every database column defined in MyOpener.java:
            //put string name in the NAME column:
            newRowValues.put(MyOpener.MESSAGE, message);
            //put string email in the EMAIL column:
            newRowValues.put(MyOpener.ISSENT, true);

            newRowValues.put(MyOpener.ISRECEIVED, false);

            //Now insert in the database:
            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

            Message msg = new Message(message, newId, true, false);
            msgList.add(msg);
            typeField.setText(null);
            myAdapter.notifyDataSetChanged();
            //myList.setAdapter(new MyListAdapter());

            //Show the id of the inserted item:
            Toast.makeText(this, "Inserted item id:"+newId, Toast.LENGTH_LONG).show();
        });

        Button addButtonnew = findViewById(R.id.addButton4);
        addButtonnew.setOnClickListener( click -> {
            EditText typeField = findViewById(R.id.text11);

            String message = typeField.getText().toString();

            //add to the database and get the new ID
            ContentValues newRowValues = new ContentValues();

            //Now provide a value for every database column defined in MyOpener.java:
            //put string name in the NAME column:
            newRowValues.put(MyOpener.MESSAGE, message);
            //put string email in the EMAIL column:
            newRowValues.put(MyOpener.ISSENT, false);

            newRowValues.put(MyOpener.ISRECEIVED, true);

            //Now insert in the database:
            long newId = db.insert(MyOpener.TABLE_NAME, null, newRowValues);

            Message msg = new Message(message, newId, false, true);
            msgList.add(msg);
            typeField.setText(null);
            myAdapter.notifyDataSetChanged();
            //myList.setAdapter(new MyListAdapter());

            //Show the id of the inserted item:
            Toast.makeText(this, "Inserted item id:"+newId, Toast.LENGTH_LONG).show();

        });

        //myList.setAdapter( myAdapter = new MyListAdapter());
        myList.setOnItemLongClickListener( (parent, view, position, id) -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Do you want to delete this?")
                    .setMessage("The selected row is: " + (position+1) + "\nThe database id is: " + id )
                    .setPositiveButton("Yes", (click, arg) -> {
                        if(frameLayout != null) {
                            for(Fragment fragment : getSupportFragmentManager().getFragments()) {
                                if(fragment.getArguments().getLong(ITEM_ID)==Long.valueOf(myAdapter.getItemId(position))) {
                                    getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                                    break;
                                }
                            }
                        }
                        deleteMessage(id);
                        msgList.remove(position);
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

        String [] columns = {MyOpener.ID, MyOpener.MESSAGE, MyOpener.ISRECEIVED, MyOpener.ISSENT};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int version = db.getVersion();
        printCursor(results, version);
    }

    protected void deleteMessage(long id)        {
        db.delete(MyOpener.TABLE_NAME, MyOpener.ID + "= ?", new String[] {Long.toString(id)});
    }

    private void loadDataFromDatabase()
    {
        //get a database connection:
        MyOpener dbOpener = new MyOpener(this);
        db = dbOpener.getWritableDatabase(); //This calls onCreate() if you've never built the table before, or onUpgrade if the version here is newer


        // We want to get all of the columns. Look at MyOpener.java for the definitions:
        String [] columns = {MyOpener.ID, MyOpener.MESSAGE, MyOpener.ISRECEIVED, MyOpener.ISSENT};
        //query all the results from the database:
        Cursor results = db.query(false, MyOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        //Now the results object has rows of results that match the query.
        //find the column indices:
        int idColIndexIndex = results.getColumnIndex(MyOpener.ID);
        int messageColIndex = results.getColumnIndex(MyOpener.MESSAGE);
        int isReceivedColIndex = results.getColumnIndex(MyOpener.ISRECEIVED);
        int isSentColIndex = results.getColumnIndex(MyOpener.ISSENT);

        //iterate over the results, return true if there is a next item:
        while(results.moveToNext())
        {
            String message = results.getString(messageColIndex);
            long id = results.getLong(idColIndexIndex);
            boolean isSend = results.getInt(isSentColIndex)!=0;
            boolean isReceived = results.getInt(isReceivedColIndex)!=0;

            //add the new Contact to the array list:
            msgList.add(new Message(message, id, isSend, isReceived));
        }

        //At this point, the contactsList array has loaded every row from the cursor.
    }

    private class MyListAdapter extends BaseAdapter {

        public int getCount() { return msgList.size();}

        public Object getItem(int position) { return msgList.get(position); }

        public long getItemId(int position) { return msgList.get(position).getId(); }

        public View getView(int position, View old, ViewGroup parent)
        {
            LayoutInflater inflater = getLayoutInflater();

            Message msg = (Message)getItem(position);

            if(msg.isSend()){
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
}
    private void printCursor(Cursor c, int version){
        Log.e("ACTIVITY_NAME", "Database version number: " + db.getVersion());
        Log.e("ACTIVITY_NAME", "Number of columns: " + c.getColumnCount());
        Log.e("ACTIVITY_NAME", "Name of the columns: ");
        for(int i=0; i<c.getColumnCount(); i++){
            Log.e("ACTIVITY_NAME", c.getColumnName(i));
        }
        c.moveToFirst();
        while(!c.isAfterLast()){
            for(int i=0; i<c.getColumnCount(); i++){
                Log.e("ACTIVITY_NAME", "The result of " + c.getColumnName(i) + " is: " + c.getString(i));
            }
            System.out.println("");
            c.moveToNext();
        }
        Log.e("ACTIVITY_NAME", "\nName of the results: " + c.getCount());
    }

}