package de.simon.brandhuber.lauf;


import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlreadyRunFragment extends Fragment {

    DatabaseHelper rundb;


    public AlreadyRunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rundb = new DatabaseHelper(getContext());
        viewColumns();

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_already_run, container, false);
    }

    public void viewColumns(){
        Cursor res = rundb.getColumns();
        if(res.getCount() == 0){
            //show message
            showMessage("Error","No data found");
            return;
        }
        StringBuffer buffer = new StringBuffer();
        while(res.moveToNext()){
            buffer.append("RUN_NUMBER: " +res.getString(0)+"\n");
            buffer.append("RUN_NAME: " +res.getString(1)+"\n\n");
        }
        // show two columns
        showMessage("Data",buffer.toString());
    }

    public void showMessage (String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

}
