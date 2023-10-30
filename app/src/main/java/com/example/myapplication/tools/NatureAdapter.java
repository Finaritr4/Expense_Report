package com.example.myapplication.tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.container.ReportActivity;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NatureAdapter extends RecyclerView.Adapter<NatureVH> implements Filterable {
    List<Nature> listitems;
    List<Nature> listitemsfull;
    Context context;
    MyDatabaseHelper databaseHelper;

    boolean isEnable=false;
    boolean isSelectAll=false;
    ArrayList<Nature> selectList=new ArrayList<>();

    MenuItem itemShare;

    public NatureAdapter(List<Nature> items) {
        this.listitems=items;
        this.listitemsfull=new ArrayList<>(items);

    }

    @NonNull
    @Override
    public NatureVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.nature_item,parent, false);
        context=parent.getContext();
        databaseHelper=new MyDatabaseHelper(context);

        return new NatureVH(view).linkAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull NatureVH holder, @SuppressLint("RecyclerView") int position) {
        holder.place.setText(listitems.get(position).getPlace());
        Date date=listitems.get(position).getDate();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd MMM yyyy HH:mm");
        String dateItem=simpleDateFormat.format(date);
        holder.date.setText(dateItem);
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!isEnable){
                    ActionMode.Callback callback=new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater=actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.menu_delete,menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            isEnable=true;
                            itemShare= menu.findItem(R.id.share);
                            clickItem(holder);
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            int id=menuItem.getItemId();
                            if(id==R.id.menu_delete_item){
                                AlertDialog.Builder dialog= new AlertDialog.Builder(context);
                                dialog.setTitle("Supprimer");
                                dialog.setMessage("Voulez-vous vraiment suprimer :'"+selectList.size()+"' élement(s) ?");
                                dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        for(Nature nature:selectList){
                                            listitems.remove(nature);
                                            databaseHelper.deleteNature(nature.getIdNature());
                                            notifyDataSetChanged();
                                        }
                                    }
                                });
                                dialog.show();

                            } else if (id == R.id.menu_selectAll) {
                                if(selectList.size()==listitems.size()){
                                    isSelectAll=false;
                                    selectList.clear();

                                }else {
                                    isSelectAll=true;
                                    selectList.clear();
                                    for(Nature nature:listitems){
                                        selectList.add(nature);
                                    }
                                }
                                notifyDataSetChanged();

                            }else if(id==R.id.share){
                                //Recuper la Nature
                                //Recuperer le detail du nature
                                //Exporter les données
                                exportData();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable=false;
                            isSelectAll=false;
                            selectList.clear();
                            notifyDataSetChanged();
                        }
                    };
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);

                }else {
                    clickItem(holder);
                }
                return true;
            }
        });
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isEnable){

                    clickItem(holder);
                    itemShare.setVisible(selectList.size()<=4);

                }else{
                    Intent itent=new Intent(context, ReportActivity.class);
                    itent.putExtra("idNature",""+listitems.get(position).getIdNature());
                    context.startActivity(itent);
                }


            }
        });

        if(isSelectAll){
            holder.imageView
                    .setVisibility(View.VISIBLE);
            holder.layout.setBackgroundColor(Color.LTGRAY);
            itemShare.setVisible(selectList.size()<=4);

        }else{
            holder.imageView.setVisibility(View.GONE);
            holder.layout.setBackgroundColor(Color.TRANSPARENT);
        }

    }

    private void exportData() {

        List<List<Detail>> listeDetailNature= new ArrayList<List<Detail>>();
        for(int i=0;i<selectList.size();i++){
            List<Detail> details=new ArrayList<>();
            int id=selectList.get(i).getIdNature();
            details=databaseHelper.getDetail(id);
            listeDetailNature.add(details);
        }
      prepareFile(selectList.size(),listeDetailNature);
    }

    private void prepareFile(int nbFiche, List<List<Detail>> listeDetailNature){

        try {

            InputStream inputStream = context.getResources().openRawResource(R.raw.data);
            File localDirectory = new File(context.getFilesDir(), "local_data");
            localDirectory.mkdirs();

            File localFile = new File(localDirectory, "local_data.xlsx");
//            String externalStoragePath= Environment.getExternalStorageDirectory().getAbsolutePath();
//            String directory= externalStoragePath+"/local_data.xlsx";
//            File localFile=new File(directory);
            if(localFile.exists()){
                Log.i("FICHIER","Le ficier est bien créer");
            }else{
                Log.i("FICHIER","Le ficier n'est pas créer");
            }
            Log.i("FICHIER","Chemin du LocalFile: "+localFile.getAbsolutePath());
            FileOutputStream outputStream = new FileOutputStream(localFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();

            FileInputStream fis = new FileInputStream(localFile);
            Log.i("FICHIER","Utilisation de workbook");
            Workbook workbook=new XSSFWorkbook(fis);
            Sheet sheet= workbook.getSheetAt(0);

                fis.close();

                int[] row1={1,2,3,23};
                int[] row2={26,27,28,48};
                int[] rowDetail1={4,5,6,7,8,9,10,11,12,13,14,15,16,17};
                int[] rowDetail2={29,30,31,32,33,34,35,36,37,38,39,40,41,42};
                int[] cell1={0,1};
                int[] cell2={3,4};

                AlertDialog.Builder dialog=new AlertDialog.Builder(context);
                dialog.setTitle("Exportation");
                switch(nbFiche){
                    case 0:
                        dialog.setMessage("Aucune donnée sélectionné à exporter");
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                        return;
                    case 1:

                        List<Detail> details=listeDetailNature.get(0);
                        dialog.setMessage("Voulez-vous exporter cet 1 élément sélectionné?");
                        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addHeadFiche(sheet,selectList.get(0),row1,1);
                                addDetailNature(sheet,details,rowDetail1,cell1);
                                saveAndShare(localFile,workbook);
                            }
                        });
                        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                        break;
                    case 2:
                        List<Detail> details1=listeDetailNature.get(0);
                        List<Detail> details2=listeDetailNature.get(1);
                        dialog.setMessage("Voulez-vous exporter ces 2 éléments sélectionné?");
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addHeadFiche(sheet,selectList.get(0),row1,1);
                                addDetailNature(sheet,details1,rowDetail1,cell1);
                                addHeadFiche(sheet,selectList.get(1),row1,4);
                                addDetailNature(sheet,details2,rowDetail1,cell2);
                                saveAndShare(localFile,workbook);
                            }
                        });
                        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                        break;
                    case 3:
                        dialog.setMessage("Voulez-vous exporter ces 3 éléments sélectionné?");
                        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addHeadFiche(sheet,selectList.get(0),row1,1);
                                addDetailNature(sheet,listeDetailNature.get(0),rowDetail1,cell1);
                                addHeadFiche(sheet,selectList.get(1),row1,4);
                                addDetailNature(sheet,listeDetailNature.get(1),rowDetail1,cell2);
                                addHeadFiche(sheet,selectList.get(2),row2,1);
                                addDetailNature(sheet,listeDetailNature.get(2),rowDetail2,cell1);
                                saveAndShare(localFile,workbook);
                            }
                        });
                        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                        break;
                    case 4:
                        dialog.setMessage("Voulez-vous exporter ces 4 éléments sélectionné?");
                        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                addHeadFiche(sheet,selectList.get(0),row1,1);
                                addDetailNature(sheet,listeDetailNature.get(0),rowDetail1,cell1);
                                addHeadFiche(sheet,selectList.get(1),row1,4);
                                addDetailNature(sheet,listeDetailNature.get(1),rowDetail1,cell2);
                                addHeadFiche(sheet,selectList.get(2),row2,1);
                                addDetailNature(sheet,listeDetailNature.get(2),rowDetail2,cell1);
                                addHeadFiche(sheet,selectList.get(3),row2,4);
                                addDetailNature(sheet,listeDetailNature.get(3),rowDetail2,cell2);
                                saveAndShare(localFile,workbook);
                            }
                        });
                        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });
                        dialog.show();
                        break;
                    default:
                        break;
                }

                // Étape 7 : Enregistrez le fichier Excel mis à jour


            /*
            // Étape 3 : Déterminez la ligne et la colonne spécifiques où vous souhaitez ajouter les données
            int rowNum = 1; // Ligne 2 (index 1)
            int colNum = 2; // Colonne 3 (index 2)

                 Premiere fiche       Deuxieme Fiche   Troisiem Fiche    Quatrieme fiche
            Date   :row=1/cell=1         row=1/cell=4     row=26/cell=1     row=26/cell=4
            Nature :row=2/cell=1         row=2/cell=4     row=27/cell=1     row=27/cell=4
            Lieu   :row=3/cell=1         row=3/cell=4     row=28/cell=1     row=28/cell=4
            Detail :row=4-16/cell=1      row=4-16/cell=4  row=29-41/cell=1  row=29-41/cell=4   (13 Ligne)
            Total  :row=17/cell=1        row=17/cell=4    row=42/cell=1     row=42/cell=4
            Pers   :row=23/cell=1        row=23/cell=4    row=48/cell=1     row=48/cell=4
            row: 1-17,23  /  26-42,48
            cel: 1        /  4

            Head=row 1-3,23 ; 26-28,48 cell 1 ;4
            */

            // Étape 7 : Exporter le fichier Excel mis à jour via une intention


        }catch (FileNotFoundException exception) {
            Log.e("FICHIER", "Erreur 1: " + exception);
        }catch (IOException exception){
            Log.e("FICHIER", "Erreur 2: " + exception);
        }
    }

    private void saveAndShare(File localFile,Workbook workbook){
        try {
            FileOutputStream fos = new FileOutputStream(localFile);
            workbook.write(fos);
            fos.close();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            Uri uri = FileProvider.getUriForFile(context, "com.example.myapplication.file-provider",localFile);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            context.startActivity(Intent.createChooser(intent, "Partager le fichier Excel"));
            selectList.clear();
            notifyDataSetChanged();
        }catch (Exception e){
            Log.e("FICHIER","Erreur lors d'enregistrement et de partage: "+e);
        }

    }
    private void addHeadFiche(Sheet sheet,Nature nature,int[] row,int c){
        Row[] rows=new Row[row.length];
        Cell[] cells=new Cell[row.length];

            for(int i=0;i<row.length;i++){
                rows[i]=sheet.getRow(row[i]);
                if (rows[i]== null) {
                    rows[i] = sheet.createRow(row[i]);
                }
                cells[i]=rows[i].getCell(c);
                if(cells[i]==null){
                    cells[i]=rows[i].createCell(c);
                }
            }


        for(int i=0;i<cells.length;i++){
            switch (i){
                case 0:
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd/MM/yyyy");
                    String date=simpleDateFormat.format(nature.getDate());
                    cells[i].setCellValue(": "+date);
                    break;
                case 1:
                    cells[i].setCellValue(": "+nature.getNature());
                    break;
                case 2:
                    cells[i].setCellValue(": "+nature.getPlace());
                    break;
                case 3:
                    String nbPers=String.valueOf(nature.getPtDej())+" - "+String.valueOf(nature.getDej())+" - "+String.valueOf(nature.getDinner());
                    cells[i].setCellValue(nbPers);
                    break;
                default:
                    Log.i("FICHIER","Il y a encore une de plus");
                    break;
            }
        }
    }

    private void addDetailNature(Sheet sheet,List<Detail> list,int[] row,int[] cell){
        Row[][] rows=new Row[row.length][cell.length];
        Cell[][] cells=new Cell[row.length][cell.length];


            for(int i=0;i<row.length;i++){
                for(int j=0;j< cell.length;j++){
                rows[i][j]=sheet.getRow(row[i]);
                if (rows[i][j]== null) {
                    rows[i][j]= sheet.createRow(row[i]);
                }
                cells[i][j]=rows[i][j].getCell(cell[j]);
                if(cells[i][j]==null){
                    cells[i][j]=rows[i][j].createCell(cell[j]);
                }
            }
        }
        Log.i("FICHIER","Taille du détail: "+list.size());
        for(int f=0;f<list.size();f++){
            Log.i("FICHIER","Detail: "+list.get(f).getDetail()+" Prix: "+list.get(f).getPrice());
        }

        if(list.size()<= rows.length-1){

                for(int i=0;i<list.size();i++){
                    for(int j=0;j<cell.length;j++){
                    if(j==0){
                        cells[i][j].setCellValue(list.get(i).getDetail());
                    }else if(j==1){
                        cells[i][j].setCellValue(list.get(i).getPrice());
                    }
                }
            }
        }
    }

    private void clickItem(NatureVH holder) {
        Nature item= listitems.get(holder.getAdapterPosition());

        if(holder.imageView.getVisibility()==View.GONE){
            holder.imageView.setVisibility(View.VISIBLE);
            holder.layout.setBackgroundColor(Color.LTGRAY);
            selectList.add(item);



        }else{
                holder.imageView.setVisibility(View.GONE);
                holder.layout.setBackgroundColor(Color.TRANSPARENT);
                selectList.remove(item);


        }








    }

    @Override
    public int getItemCount() {
        return listitems.size();
    }

    @Override
    public Filter getFilter() {
        return listitemsfilter;
    }

    private Filter listitemsfilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Nature> filteredList=new ArrayList<>();
            if(charSequence==null || charSequence.length()==0){
                filteredList.addAll(listitemsfull);
            }else {
                String fiternPattern= charSequence.toString().toLowerCase().trim();

                for(Nature natureItem:listitemsfull){
                    if(natureItem.getPlace().toLowerCase().contains(fiternPattern)){
                        filteredList.add(natureItem);
                    }
                }
            }
            FilterResults results=new FilterResults();
            results.values=filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            listitems.clear();
            listitems.addAll((List) filterResults.values);
            notifyDataSetChanged();

        }
    };

}


class NatureVH extends RecyclerView.ViewHolder{
    NatureAdapter adapter;
    TextView place,date;
    ImageView imageView;

    RelativeLayout layout;
    public NatureVH(@NonNull View itemView) {
        super(itemView);
        place= itemView.findViewById(R.id.text_place_item);
        date=itemView.findViewById(R.id.text_date_item);
        imageView=itemView.findViewById(R.id.checkbox_selected);
        layout=itemView.findViewById(R.id.layout_item_nature);
    }

    public NatureVH linkAdapter(NatureAdapter adapter){
        this.adapter= adapter;
        return this;
    }
}

