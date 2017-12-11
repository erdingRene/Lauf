package de.simon.brandhuber.lauf;


import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AlreadyRunFragment extends Fragment {

    DatabaseHelper rundb;
    Button  btnDelete;
    EditText txtDeleteName;


    public AlreadyRunFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_already_run, container, false);


        String[] runs = {"Erding","München","Freising","Frankfurt Oder"};
        ListAdapter adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,runs);
        ListView listRuns = (ListView) v.findViewById(R.id.listRuns);
        listRuns.setAdapter(adapter);

        txtDeleteName = (EditText) v.findViewById(R.id.txtDeleteName);

        btnDelete = (Button) v.findViewById(R.id.btnDelete);
        rundb = new DatabaseHelper(getContext());
        viewColumns();
        deleteData();
        return v;


    }

    public void deleteData(){
        btnDelete.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        Integer deletedRows = rundb.deleteData(txtDeleteName.getText().toString() );
                        if (deletedRows > 0)
                            Toast.makeText(getActivity() ,deletedRows + " row(s) deleted",Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(getActivity(),"Data not deleted",Toast.LENGTH_LONG).show();

                    }
                }
        );
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
