package ddwucom.mobile.finalproject.ma02_20200981;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MemoAddActivity extends AppCompatActivity {
    Memo memo;
    EditText title;
    EditText date;
    EditText info;

    MemoDBManager memoDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memo);

        title = findViewById(R.id.etTitle);
        info = findViewById(R.id.etInfo);
        memoDBManager = new MemoDBManager(this);
    }

    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnSave:
                boolean result = memoDBManager.addNewMemo(new Memo (title.getText().toString(), info.getText().toString()));

                if (result) {   // 정상수행에 따른 처리
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("memo", title.getText().toString());
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(this, "새로운 메모 추가 실패", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
        }
    }
}
