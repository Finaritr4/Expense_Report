package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.container.ReportActivity;
import com.example.myapplication.tools.MyDatabaseHelper;
import com.example.myapplication.tools.Nature;
import com.example.myapplication.tools.NatureAdapter;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;

    MyDatabaseHelper myDatabaseHelper;
    RecyclerView recyclerView;
    ImageView imageView;
    TextView textViewNoData;
    NatureAdapter adapter;
    List<Nature> natureList=new ArrayList<>();
    private Activity activity;
    private static final int  REQUEST_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
        setContentView(R.layout.activity_main);
        myDatabaseHelper=new MyDatabaseHelper(this);
        initialise();
        fab= findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reportActivity=new Intent(getApplicationContext(), ReportActivity.class);
                startActivity(reportActivity);
            }
        });
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION);
            Toast.makeText(this,"Demande de permission",Toast.LENGTH_SHORT).show();
        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_STORAGE_PERMISSION){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Demande de permission",Toast.LENGTH_SHORT).show();
            }else{
//
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initialise() {
        imageView=findViewById(R.id.imageView_no_data);
        textViewNoData=findViewById(R.id.textView_no_data);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ActionBar actionBar=getSupportActionBar();
        String appName=getResources().getString(R.string.app_name);
        if(actionBar!=null){
            SpannableString title=new SpannableString(appName);
            title.setSpan(new AbsoluteSizeSpan(23,true),0,title.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            actionBar.setTitle(title);
        }

        SimpleDateFormat dateFormat=new SimpleDateFormat("dd MMM.");
        SimpleDateFormat dateFormat2=new SimpleDateFormat("dd MMM. yyyy");
        Calendar calendar=Calendar.getInstance();
        int jourActuel= calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DAY_OF_WEEK,Calendar.MONDAY - jourActuel) ;
        Date monday=calendar.getTime();
        String dateMonday= dateFormat.format(monday);
        calendar.add(Calendar.DAY_OF_WEEK,6);
        Date sunday=calendar.getTime();
        String dateSunday= dateFormat2.format(sunday);

        long millisecond= monday.getTime();
        long oneDay=24L * 60L * 1000L;
        long newmill=millisecond-oneDay;
        Date newDateMonday=new Date(newmill);

        getSupportActionBar().setSubtitle(getString(R.string.current_week,dateMonday+" - "+dateSunday));


        natureList=myDatabaseHelper.getThisWeek(newDateMonday,sunday);
        showEmpty();
        setAdapter();


    }

    private void setAdapter() {
        adapter =new NatureAdapter(natureList);
        recyclerView.setAdapter(adapter);
    }

    private void showEmpty() {
        if(natureList.isEmpty()){
            imageView.setVisibility(View.VISIBLE);
            textViewNoData.setVisibility(View.VISIBLE);
        }else{
            imageView.setVisibility(View.GONE);
            textViewNoData.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem= menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id == R.id.pick_week_date){
            MaterialDatePicker.Builder<Pair<Long,Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
            materialDateBuilder.setTitleText(R.string.select_date);
            final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
            materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
                @Override
                public void onPositiveButtonClick(Object o) {
                    Pair<Long, Long> date=(Pair<Long, Long>) materialDatePicker.getSelection();
                    Date firstDate=new Date(date.first);
                    Date secondDate=new Date(date.second);
                    Toast.makeText(getApplicationContext(),"Date du " + firstDate+" au "+secondDate,Toast.LENGTH_SHORT).show();
                    natureList.clear();
                    natureList=myDatabaseHelper.getThisWeek(firstDate,secondDate);
                    setAdapter();
                    showEmpty();
                    getSupportActionBar().setSubtitle(getString(R.string.current_week,""+materialDatePicker.getHeaderText()));
                }
            });
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }


    }


}