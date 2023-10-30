package com.example.myapplication.container;

import android.app.Activity;
import android.app.Dialog;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.example.myapplication.R;

import java.text.DateFormat;
import java.util.Date;

public class ReportDialog extends Dialog {
    private EditText date,detail,price;
    private Button add,cancel;
    public ReportDialog(@NonNull Activity activity) {
        super(activity, androidx.appcompat.R.style.Base_Theme_AppCompat_Dialog);
        setContentView(R.layout.dialog_report_detail);
        date=findViewById(R.id.edit_text_dateDialog);
        detail=findViewById(R.id.edit_text_detailDialog);
        price=findViewById(R.id.edit_text_priceDialog);
        add=findViewById(R.id.add_detail_btnDialog);
        cancel=findViewById(R.id.cancel_detail_btnDialog);

    }

    public void setDate(String date){
        this.date.setText(date);
    }

    public String getDate(){
        String dateEditText = this.date.getText().toString();
        String currentDate = DateFormat.getDateInstance().format(new Date());

        String date= dateEditText.isEmpty() ? currentDate : dateEditText;
        return date;
    }

    public String getDetail(){

        String detail = this.detail.getText().toString();
        return detail;
    }

    public int getPrice(){
        String price= this.price.getText().toString();
        int prix= price.isEmpty() ? 0 : Integer.valueOf(price);
        return prix;
    }

    public Button getAdd() {
        return add;
    }

    public Button getCancel() {
        return cancel;
    }

    public void build(){
        show();

    }
}
