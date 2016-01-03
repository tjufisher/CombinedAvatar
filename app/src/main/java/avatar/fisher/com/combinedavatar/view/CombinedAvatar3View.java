package avatar.fisher.com.combinedavatar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import avatar.fisher.com.combinedavatar.R;

/**
 * Created by yulong on 2016/1/2.
 */
public class CombinedAvatar3View extends View {
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private double mBigRadius;//大圆半径
    private double mSmallRadius;//小圆半径
    private double mDisOA;//内切圆到圆心距离
    private double mDisAB;//内切圆圆心到弧边的距离
    private double mDis;//覆盖整个图片的圆的半径
    private double mCoverRadius;//遮盖切图为空的部分的圆半径
    private int mDivisionNum = 1;//划分N部分
    private double mAngle_radians;//弧度值

    private Paint mPaint;
    private Paint mCoverPaint;
    private Paint mTextPaint;
    private double mPaintScale;
    private int[] mResources;

    public CombinedAvatar3View(Context context) {
        this(context, null);
    }

    public CombinedAvatar3View(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CombinedAvatar3View(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        init(attrs);
    }

    public void initData(int[] res) throws Exception {
        initData(res, res.length);
    }

    public void initData(int[] res, int division) throws Exception {
        mDivisionNum = division;
        if (res.length != mDivisionNum) {
            throw new Exception("资源个数与分割块数不符，请检查！");
        }
        mResources = res;
        cal();
    }

    public void init(AttributeSet attrs) {
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CombinedAvatarView);
        mDivisionNum = typedArray.getInt(R.styleable.CombinedAvatarView_divisionNum, 1);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);


        mCoverPaint = new Paint();
        mCoverPaint.setColor(Color.LTGRAY);
        mCoverPaint.setDither(true);
        mCoverPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCoverPaint.setStrokeWidth(1);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.GREEN);
        mTextPaint.setDither(true);
        mTextPaint.setTextSize(200);
        mTextPaint.setTextAlign(Paint.Align.CENTER);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        cal();
    }

    /**
     * 计算对应数值
     */
    public void cal() {
        mBigRadius = (mWidth < mHeight ? mWidth : mHeight) / 2;
        mAngle_radians = 2 * Math.PI / mDivisionNum;

        double sin_mAngle_divide_2 = Math.sin(mAngle_radians / 2);
        mSmallRadius = (float) (sin_mAngle_divide_2 / (sin_mAngle_divide_2 + 1)) * mBigRadius;
        mPaintScale = mSmallRadius / mBigRadius;

        mDisOA = mBigRadius - mSmallRadius;
        mDisAB = Math.sqrt(mBigRadius * mBigRadius + mDisOA * mDisOA - 2 * mBigRadius * mDisOA * Math.cos(Math.PI / mDivisionNum));//余弦定理

        //图片个数为3时，选取大变为矩形的长度，解决裁剪空间不足产生黑色区域情况
        //图片个数非大于3时，选择区小边为矩形边长，保证图片的缩放，显示内容较全
        if (mDivisionNum == 3) {
            mDis = mDisOA > mDisAB ? mDisOA : mDisAB;//取较大值
        } else {
            mDis = mDisOA < mDisAB ? mDisOA : mDisAB;//取较小值
        }

        mCoverRadius = mBigRadius - 2 * Math.sqrt(mDisAB * mDisAB - mSmallRadius * mSmallRadius);


    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        Bitmap mBitmap = null;

        Bitmap mOriginBitmap = BitmapFactory.decodeResource(getResources(), mResources[0]);
        Bitmap mArcBitmap = getCroppedArcBitmap(mOriginBitmap, 0);//第一个扇区
        mBitmap = mArcBitmap;
        for (int i = 1; i < mDivisionNum; i++) {
            mOriginBitmap = BitmapFactory.decodeResource(getResources(), mResources[i]);
            mArcBitmap = getCroppedArcBitmap(mOriginBitmap, i);
            mBitmap = getCombinedBitmap(mBitmap, mArcBitmap);
        }
        canvas.drawBitmap(mBitmap, 0, 0, mPaint);

        //图片数量为3，4，5时，截取的扇形区间完整，中心位置不会有黑色区域，无需特殊处理
        //图片数量大于5时，截取的扇形区间有黑色区域，中心位置会有黑色区域，需增加一个特定遮盖区域进行遮盖
        switch (mDivisionNum) {
            case 3:
            case 4:
            case 5:
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
            default:
                canvas.drawCircle((float) mBigRadius, (float) mBigRadius, (float) mCoverRadius, mCoverPaint);
                mTextPaint.setTextSize((float) mCoverRadius);
                canvas.drawText("群聊", (float) mBigRadius, (float) mBigRadius - (mTextPaint.getFontMetrics().descent + mTextPaint.getFontMetrics().ascent) / 2, mTextPaint);
                break;
        }

        mOriginBitmap.recycle();
        mArcBitmap.recycle();
        mBitmap.recycle();


    }

    public Bitmap getCroppedArcBitmap(Bitmap bm, int n) {
        int diameter = (int) mBigRadius * 2;
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);

        RectF rectF = new RectF(0, 0, (float) diameter, (float) diameter);
        canvas.drawArc(rectF, (float) (-90 + n * 360 / mDivisionNum), (float) (360 / mDivisionNum), true, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float x = (float) (mDisOA * Math.sin(mAngle_radians * (2 * n + 1) / 2) + mBigRadius);
        float y = (float) (-mDisOA * Math.cos(mAngle_radians * (2 * n + 1) / 2) + mBigRadius);//注意这个是负值！


        Rect des = new Rect((int) (x - mDis), (int) (y - mDis), (int) (x + mDis), (int) (y + mDis));
        canvas.drawBitmap(bm, null, des, paint);
        bm.recycle();
        return output;
    }

    public Bitmap getCombinedBitmap(Bitmap b1, Bitmap b2) {
        int diameter = (int) mBigRadius * 2;
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);

        canvas.drawBitmap(b1, 0, 0, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
        canvas.drawBitmap(b2, 0, 0, paint);

        b1.recycle();
        b2.recycle();
        return output;
    }

}
