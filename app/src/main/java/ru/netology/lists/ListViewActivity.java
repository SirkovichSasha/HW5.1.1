package ru.netology.lists;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListViewActivity extends AppCompatActivity {
    private static final String KEY_TITLE = "key_title";
    private static final String KEY_COUNT = "key_count";
    private static final String STORAGE_FILE = "storage_file.txt";
    private  static final String DELIMITER=";";
    private List<Map<String, String>> values;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView list = findViewById(R.id.list);
        final List<Map<String, String>> values = prepareContent();
        final BaseAdapter listContentAdapter = createAdapter(values);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                   values.remove(position);
                   saveList(values,getValuesFile());
                   listContentAdapter.notifyDataSetChanged();
            }
        });

        list.setAdapter(listContentAdapter);
    }

    private File getValuesFile() {
        return new File(getExternalFilesDir(null), STORAGE_FILE);
    }

    private List<Map<String, String>> loadFromFile(File file) {
        List<Map<String, String>> result=new ArrayList<>();
        BufferedReader reader = null;
        StringBuilder sb=new StringBuilder();
        try {
            reader = new BufferedReader(new FileReader(file));
            int symbol;
            while((symbol=reader.read())!=-1) {
                sb.append((char)symbol);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(reader!=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        String fullFileContent=sb.toString();
        String [] titles=fullFileContent.split("\n\n");
        for(String title:titles) {
            String[] values = title.split(DELIMITER);
            if (values.length != 2) {
                throw new RuntimeException("length is n't 2");
            } else {
                Map<String, String> map = new HashMap<>();
                map.put(KEY_TITLE, values[0]);
                map.put(KEY_COUNT, values[1]);
                result.add(map);
            }
        }
        return result;
    }

    private void saveList(List<Map<String,String>> data, File file)  {
        BufferedWriter writer=null;
        try {
            writer=new BufferedWriter(new FileWriter(file));
            for (Map<String,String> map :data){
                String text=map.get(KEY_TITLE);
                String count=map.get(KEY_COUNT);
                writer.write(text+DELIMITER+count);
                writer.write("\n\n");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    @NonNull
    private BaseAdapter createAdapter(List<Map<String,String>>values) {
        return new SimpleAdapter(this,values,R.layout.item,new String[]{KEY_TITLE,KEY_COUNT},new int[]{R.id.textTv,R.id.simbolCntTv});
    }

    @NonNull
    private String[] oldPrepareContent() {
        return getString(R.string.large_text).split("\n\n");
    }
     private List<Map<String,String>> prepareContent() {
         List<Map<String,String>> result=new ArrayList<>();
         File file=getValuesFile();
       if(file.exists()){
              return loadFromFile(file);
           //file.delete();
         }else {
             String[] titles = getString(R.string.large_text).split("\n\n");
             for (String title : titles) {
                 Map<String, String> map = new HashMap<>();
                 map.put(KEY_TITLE, title);
                 map.put(KEY_COUNT, title.length() + "");
                 result.add(map);
             }
         }
           saveList(result,file);
//         System.out.println("File path "+file.getAbsolutePath());
       return  result;
     }
}
