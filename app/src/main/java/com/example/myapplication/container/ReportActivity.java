package com.example.myapplication.container;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;
import com.example.myapplication.tools.Detail;
import com.example.myapplication.tools.DetailAdapter;
import com.example.myapplication.tools.MyDatabaseHelper;
import com.example.myapplication.tools.Nature;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReportActivity extends AppCompatActivity implements DetailAdapter.OnItemDeletedListener{
    private Button pickDate,addDetail,save,delete;
    private Date date;
    private TextInputLayout textInputLayout;
    private EditText editText,editNature,editPlace,editPtiDej,editDej,editDinner;

    private TextView totalView;
    private RecyclerView recyclerView;

    private Activity activity;
    private List<Detail> listItems=new ArrayList<>();
    private MyDatabaseHelper databaseHelper;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.activity=this;
        databaseHelper=new MyDatabaseHelper(activity);
        initialise();
        verifyIntentData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DetailAdapter adapter=new DetailAdapter(listItems);
        adapter.setOnItemDeletedListener(this);
        recyclerView.setAdapter(adapter);

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();
        materialDateBuilder.setTitleText(R.string.select_date);
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
                materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                    @Override
                    public void onPositiveButtonClick(Object o) {
                        editText.setText("" + materialDatePicker.getHeaderText());
                        Long d= (Long) materialDatePicker.getSelection();
                        date=new Date(d);
                        Toast.makeText(getApplicationContext(),"" + date,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        addDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportDialog dialog=new ReportDialog(activity);
                dialog.setDate(String.valueOf(editText.getText()));
                dialog.build();
                dialog.getAdd().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(dialog.getDetail().isEmpty()){
                            Log.i("DEBUG","Detail: /"+dialog.getDetail()+"/////"+"// Prix: /"+dialog.getPrice());
                        }else {
                            Log.i("DEBUG","Pas vide: Detail: /"+dialog.getDetail()+"// Prix: /"+dialog.getPrice());
                            Detail detail=new Detail(dialog.getDate(),dialog.getDetail(),dialog.getPrice());
                            listItems.add(detail);
                            String total=totalDetail(listItems);
                            totalView.setText(total+" Ar");
                            adapter.notifyItemInserted(listItems.size()-1);
                            dialog.dismiss();
                        }

                    }
                });
                dialog.getCancel().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    saveData();
                }catch (Exception e){
                    Log.e("SHOW","Erreru : "+e);
                }


            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().hasExtra("idNature")) {
                    String id = getIntent().getStringExtra("idNature");
                    AlertDialog.Builder dialog=new AlertDialog.Builder(activity);
                    dialog.setTitle("Supprimer");
                    dialog.setMessage("Voulez-vous vraiment supprimer cet enregistrement?");
                    dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            databaseHelper.deleteNature(Integer.valueOf(id));
                            Intent intent=new Intent(activity, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
                    dialog.show();


                }else {
                    finish();
                }
            }
        });

    }

    void initialise(){
        editText = findViewById(R.id.editTextDate);
        totalView=findViewById(R.id.totalTextview);
        pickDate = findViewById(R.id.pick_date_btn);
        addDetail= findViewById(R.id.add_detail_btn);
        recyclerView= findViewById(R.id.recyclerview_detail);

        save=findViewById(R.id.save);
        delete=findViewById(R.id.delete);

        editNature=findViewById(R.id.edit_text_nature);
        editNature.setText("Repas");
        editPlace=findViewById(R.id.edit_text_lieu);
        editPtiDej=findViewById(R.id.edit_text_ptit_dej);
        editDej=findViewById(R.id.edit_text_dej);
        editDinner=findViewById(R.id.edit_text_dinner);
        date=new Date();
    }

    private String totalDetail(List<Detail> liste){
        int total=0;
        for(Detail detail:liste){
            total=total+detail.getPrice();
        }
        return String.valueOf(total);
    }

    @Override
    public void onItemDeleted(int position) {
        String total=totalDetail(listItems);
        totalView.setText(total+" Ar");
    }

    void saveData(){
        String nature= editNature.getText().toString();
        String lieu= editPlace.getText().toString();
        String ptitDej= editPtiDej.getText().toString();
        String dej= editDej.getText().toString();
        String dinner= editDinner.getText().toString();
        String[] text={nature,lieu,ptitDej,dej,dinner};
        EditText[] editTexts={editNature,editPlace,editPtiDej,editDej,editDinner};
        for(int i=0;i<text.length;i++){
            verifyIfEmpty(text[i],editTexts[i]);
        }

        if(nature.isEmpty() || lieu.isEmpty() || ptitDej.isEmpty() || dej.isEmpty() || dinner.isEmpty()){
            return;
        }
            Nature motif =new Nature();
            motif.setDate(date);
            motif.setPlace(lieu);
            motif.setNature(nature);
            motif.setPtDej(Integer.valueOf(ptitDej));
            motif.setDej(Integer.valueOf(dej));
            motif.setDinner(Integer.valueOf(dinner));

        AlertDialog.Builder dialog=new AlertDialog.Builder(activity);
        dialog.setTitle("Sauvegarder");
        dialog.setMessage("Voulez-vous vraiment enregistrer?");
        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(getIntent().hasExtra("idNature")){
                    String id=getIntent().getStringExtra("idNature");
                    motif.setIdNature(Integer.valueOf(id));
                    databaseHelper.updateNature(motif);
                    databaseHelper.deleteDetail(Integer.valueOf(id));

                }else {
                    databaseHelper.insertNature(motif);
                }

                for(Detail detail:listItems){
                    detail.setNature(motif);
                    databaseHelper.insertDetailNature(detail);
                }
                Intent intent=new Intent(activity, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();

    }

    void verifyIfEmpty(String text, EditText edit){
        if(text.isEmpty()){
            edit.setError("Ce champs est requis");
        }
    }

    void verifyIntentData(){
        if(getIntent().hasExtra("idNature")){
            String id=getIntent().getStringExtra("idNature");
            Nature nature= databaseHelper.getNature(Integer.valueOf(id));
            if(nature!=null){
                SimpleDateFormat dateFormat=new SimpleDateFormat("dd MMM yyyy");
                String date=dateFormat.format(nature.getDate());
                editText.setText(date);
                editNature.setText(nature.getNature());
                editPlace.setText(nature.getPlace());
                editPtiDej.setText(String.valueOf(nature.getPtDej()));
                editDej.setText(String.valueOf(nature.getDej()));
                editDinner.setText(String.valueOf(nature.getDinner()));

                listItems.addAll(databaseHelper.getDetail(Integer.valueOf(id)));
                String total=totalDetail(listItems);
                totalView.setText(total+" Ar");
            }
        }else{
            String currentDate = DateFormat.getDateInstance().format(new Date());
            editText.setText(currentDate);
        }
    }
}