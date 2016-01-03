package avatar.fisher.com.combinedavatar;

import android.app.Activity;
import android.os.Bundle;

import avatar.fisher.com.combinedavatar.view.CombinedAvatar2View;
import avatar.fisher.com.combinedavatar.view.CombinedAvatarView;

public class CombinedAvatar2Activity extends Activity{

    private CombinedAvatar2View combinedAvatar2View;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar2);
        combinedAvatar2View = (CombinedAvatar2View)findViewById(R.id.cav);
        try {
            combinedAvatar2View.initData(res, res.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
