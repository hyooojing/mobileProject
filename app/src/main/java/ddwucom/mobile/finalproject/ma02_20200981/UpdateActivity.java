package ddwucom.mobile.finalproject.ma02_20200981;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {
    Memo memo;
    EditText title;
    EditText date;
    EditText info;
    MemoDBManager memoDBManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_memo);
        memo = (Memo) getIntent().getSerializableExtra("memo");
        //객체를 받아올 때는 getSerializableExtra

        title = findViewById(R.id.etTitle);
        info = findViewById(R.id.etInfo);

        title.setText(memo.getTitle());
        info.setText(memo.getInfo());

        memoDBManager = new MemoDBManager(this);
    }

    //클릭했을 때 수정
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnUpdate:
                //수정여부와 상관없이 무조건 읽어옴
                memo.setTitle(title.getText().toString());
                memo.setInfo(info.getText().toString());

                if (memoDBManager.modifyMemo(memo)) {
                    Intent resultIntent = new Intent();
                    //startActivity하면 안됨(MainActivity 위에 UpdateActivity가 있고 다시 메인으로 돌아가는 거니까 업데이트가 사라지면 되는 거임)
                    resultIntent.putExtra("memo", memo);
                    setResult(RESULT_OK, resultIntent);
                } else {
                    setResult(RESULT_CANCELED);
                }
                break;

            case R.id.btnCancel:
                setResult(RESULT_CANCELED);
                break;
        }
        finish();
    }
}
