package ddwucom.mobile.finalproject.ma02_20200981;
import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MuseumActivity extends AppCompatActivity {
    final int ADD_CODE = 200;

    ListView listView;
    EditText editMuseumText;
    String apiAddress;
    String query;

    MyMuseumAdapter adapter;
    ArrayList<MuseumDTO> resultList;
    MuseumXmlParser parser;
    NetworkManager networkManager;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum);

        editMuseumText = findViewById(R.id.editMuseumText);
        listView = findViewById(R.id.listView);

        resultList = new ArrayList();
        adapter = new MyMuseumAdapter(this, R.layout.list_museum, resultList);
        listView.setAdapter(adapter);

        apiAddress = getResources().getString(R.string.api_url);
        parser = new MuseumXmlParser();
        networkManager = new NetworkManager(this);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 롱클릭시 실행할 동작 지정
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("report", "1");
                MuseumDTO museum = resultList.get(i);
                Intent intent = new Intent(MuseumActivity.this, AddActivity.class);
                intent.putExtra("museum", museum);
                startActivityForResult(intent, ADD_CODE);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_CODE) {
            switch (resultCode) {
                case RESULT_OK:
                    MuseumDTO museum = (MuseumDTO) data.getSerializableExtra("museum");
                    Toast.makeText(this, museum.getName() + " 추가 완료", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "추가 취소", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnShow2:
                query = editMuseumText.getText().toString();
                Log.d("check", apiAddress + query);

                try {
                    new NetworkAsyncTask().execute(apiAddress + URLEncoder.encode(query, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    class NetworkAsyncTask extends AsyncTask<String, Integer, String> {
        ProgressDialog progressDlg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDlg = ProgressDialog.show(MuseumActivity.this, "Wait", "Downloading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            String address = strings[0];
            String result = null;
            result = networkManager.downloadContents(address);
            if (result == null) return "Error";
            Log.d(TAG, result);

            resultList = parser.parse(result);
            for (MuseumDTO dto : resultList) {
                //Bitmap bitmap = networkManager.downloadImage(dto.getImageLink());
                //if (bitmap != null) imgFileManager.saveBitmapToTemporary(bitmap, dto.getImageLink());
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            adapter.setList(resultList);
            progressDlg.dismiss();
        }
    }
}
