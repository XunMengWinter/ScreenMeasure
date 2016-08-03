package top.wefor.screen;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final float INCH_CM = 2.54F;
    @BindView(R.id.width_et) AppCompatEditText mWidthEt;
    @BindView(R.id.height_et) AppCompatEditText mHeightEt;
    @BindView(R.id.refresh_btn) Button mRefreshBtn;
    @BindView(R.id.content_layout) FrameLayout mContentLayout;
    @BindView(R.id.cmInch_switch) SwitchCompat mCmInchSwitch;
    @BindView(R.id.scale_iv) ImageView mScaleIv;
    @BindView(R.id.diagonal_tv) TextView mDiagonalTv;

    private float mWidth, mHeight;
    private boolean isInch;


    private float mX, mY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mScaleIv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mX = motionEvent.getRawX();
                        mY = motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float x = motionEvent.getRawX();
                        float y = motionEvent.getRawY();
                        // 注意四舍五入
                        mContentLayout.layout(mContentLayout.getLeft(), mContentLayout.getTop() + (int) (y - mY - 0.5),
                                mContentLayout.getRight() + (int) (x - mX + 0.5), mContentLayout.getBottom());
                        mContentLayout.getLayoutParams().width += (x - mX + 0.5);
                        mContentLayout.getLayoutParams().height += (mY - y + 0.5);
                        mX = x;
                        mY = y;
                        break;
                    case MotionEvent.ACTION_UP:
                        float xDpi = getResources().getDisplayMetrics().xdpi;
                        float yDpi = getResources().getDisplayMetrics().ydpi;
                        mWidth = mContentLayout.getLayoutParams().width / xDpi;
                        mHeight = mContentLayout.getLayoutParams().height / yDpi;
                        if (!isInch) {
                            mWidth *= INCH_CM;
                            mHeight *= INCH_CM;
                        }
                        mWidthEt.setText(mWidth + "");
                        mHeightEt.setText(mHeight + "");
                        refreshText();
                        break;
                }
                return true;
            }
        });

        mCmInchSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isInch = b;

                try {
                    if (isInch) {
                        mWidth = Float.parseFloat(mWidthEt.getText().toString()) / INCH_CM;
                        mHeight = Float.parseFloat(mHeightEt.getText().toString()) / INCH_CM;
                    } else {
                        mWidth = Float.parseFloat(mWidthEt.getText().toString()) * INCH_CM;
                        mHeight = Float.parseFloat(mHeightEt.getText().toString()) * INCH_CM;
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                mWidthEt.setText(mWidth + "");
                mHeightEt.setText(mHeight + "");
                refreshText();
            }
        });

        mRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mWidth = Float.parseFloat(mWidthEt.getText().toString());
                    mHeight = Float.parseFloat(mHeightEt.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    showToast("请输入数字");
                }

                float xDpi = getResources().getDisplayMetrics().xdpi;
                float yDpi = getResources().getDisplayMetrics().ydpi;

                if (isInch) {
                    mContentLayout.getLayoutParams().width = (int) ((mWidth) * xDpi);
                    mContentLayout.getLayoutParams().height = (int) ((mHeight) * yDpi);
                } else {
                    mContentLayout.getLayoutParams().width = (int) ((mWidth / INCH_CM) * xDpi);
                    mContentLayout.getLayoutParams().height = (int) ((mHeight / INCH_CM) * yDpi);
                }


                refreshText();
                Log.i("xyz", "dpi " + xDpi + "~" + yDpi);
            }
        });
    }

    private void refreshText() {
        mDiagonalTv.setText("对角线: " + Math.sqrt(mWidth * mWidth + mHeight * mHeight));
        mRefreshBtn.setText("刷新(" + mWidth + "*" + mHeight + ")");
    }

    /**
     * show toast
     *
     * @param msg
     */
    protected void showToast(CharSequence msg) {
        if (!TextUtils.isEmpty(msg))
            Snackbar.make(getWindow().getDecorView(), msg, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_view) {
            String url = "https://github.com/XunMengWinter/ScreenMeasure";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
