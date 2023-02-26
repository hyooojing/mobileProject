package ddwucom.mobile.finalproject.ma02_20200981;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MemoActivity extends AppCompatActivity {
    final static String TAG = "MainActivity";
    final int REQ_CODE = 100;
    final int UPDATE_CODE = 200;

    ListView listView;
    ArrayAdapter adapter;
    ArrayList<Memo> memoList = null;
    MemoDBManager memoDBManager;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo);

        listView = findViewById(R.id.listView);
        memoList = new ArrayList<Memo>();

        adapter = new ArrayAdapter<Memo>(this, android.R.layout.simple_list_item_1, memoList);
        listView.setAdapter(adapter);
        memoDBManager = new MemoDBManager(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //position 정보로 사용할 메모 객체를 찾아내야 함
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Memo memo = memoList.get(position);
                Intent intent = new Intent(MemoActivity.this, UpdateActivity.class);
                intent.putExtra("memo", memo);
                //객체 담을 때, 메모 객체 자체가 serializable로 만들어졌기 때문에 intent에 넣을 수 있음
                startActivityForResult(intent, UPDATE_CODE);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;   //롱클릭하는 위치를 기억해뒀다가
                AlertDialog.Builder builder = new AlertDialog.Builder(MemoActivity.this);
                builder.setTitle("삭제 확인")
                        .setMessage("삭제하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (memoDBManager.removeMemo(memoList.get(pos).get_id())) {
                                    Toast.makeText(MemoActivity.this,"삭제 완료", Toast.LENGTH_SHORT).show();
                                    memoList.clear();
                                    memoList.addAll(memoDBManager.getAllMemo());
                                    adapter.notifyDataSetChanged();
                                } else {
                                    Toast.makeText(MemoActivity.this, "삭제 실패", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("취소", null)
                        .setCancelable(false)
                        .show();
                return true; //작업 정상적으로 끝났을 때
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        memoList.clear();
        memoList.addAll(memoDBManager.getAllMemo());
        adapter.notifyDataSetChanged();
    }


    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addMemo:
                Intent memoAddIntent = new Intent(MemoActivity.this, MemoAddActivity.class);
                startActivity(memoAddIntent);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE) {  // AddActivity 호출 후 결과 확인
            switch (resultCode) {
                case RESULT_OK:
                    String memo = data.getStringExtra("memo");
                    Toast.makeText(this, memo + " 추가 완료", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "메모 추가 취소", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (requestCode == UPDATE_CODE) {    // UpdateActivity 호출 후 결과 확인
            switch (resultCode) {
                case RESULT_OK:
                    Toast.makeText(this, "메모 수정 완료", Toast.LENGTH_SHORT).show();
                    break;
                case RESULT_CANCELED:
                    Toast.makeText(this, "메모 수정 취소", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
