package avatar.fisher.com.combinedavatar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import avatar.fisher.com.combinedavatar.view.CombinedAvatar2View;
import avatar.fisher.com.combinedavatar.view.CombinedAvatar3View;

public class CombinedAvatar3Activity extends Activity{

    private CombinedAvatar3View combinedAvatar3View;
    private int[] res = { R.drawable.avatar1,
            R.drawable.avatar2,
            R.drawable.avatar3,
            R.drawable.avatar4,
            R.drawable.avatar5,
            R.drawable.avatar6,
            R.drawable.avatar7,
            R.drawable.avatar8,
            R.drawable.avatar9,
            R.drawable.avatar10,
            R.drawable.avatar11,
            R.drawable.avatar12,
            R.drawable.avatar13,
            R.drawable.avatar14
    };
    private int size = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar3);
        combinedAvatar3View = (CombinedAvatar3View)findViewById(R.id.cav);
        try {
            combinedAvatar3View.initData(res, res.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_add:
                size = size >= 14 ?  3 : size + 1;
                break;
            case R.id.btn_minus:
                size = size <= 3 ?  14 : size - 1;
                break;
        }
        try {
            int[] arr = new int[size];
            for( int i = 0; i < size; i++){
                arr[i] = res[i];
            }
            combinedAvatar3View.initData(arr, arr.length);
            combinedAvatar3View.invalidate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
