package avatar.fisher.com.combinedavatar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import avatar.fisher.com.combinedavatar.view.CombinedAvatarView;

public class MainActivity extends Activity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

    }


    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.btn_avatar:
                intent = new Intent(mContext, CombinedAvatarActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_avatar2:
                intent = new Intent(mContext, CombinedAvatar2Activity.class);
                startActivity(intent);
                break;
            case R.id.btn_avatar3:
                intent = new Intent(mContext, CombinedAvatar3Activity.class);
                startActivity(intent);
                break;

        }
    }

}
