package avatar.fisher.com.combinedavatar.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import avatar.fisher.com.combinedavatar.R;

/**
 * Created by yulong on 2016/1/2.
 */
public class CombinedAvatarView extends View {
    private Context mContext;
    private int mWidth;
    private int mHeight;
    private double mBigRadius;//大圆半径
    private double mSmallRadius;//小圆半径
    private double mRadius;//计算坐标半径
    private int mDivisionNum = 1;//划分N部分
    private double mAngle_radians;//弧度值

    private Paint mPaint;
    private double mPaintScale;
    private int[] mResources;

    public CombinedAvatarView(Context context) {
        this(context, null);
    }

    public CombinedAvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CombinedAvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        init(attrs);
    }

    public void initData(int[] res) throws Exception {
        initData(res, res.length);
    }

    public void initData(int[] res, int division) throws Exception {
        mDivisionNum = division;
        if(res.length != mDivisionNum){
            throw new Exception("资源个数与分割块数不符，请检查！");
        }
        mResources = res;
    }

    public void init(AttributeSet attrs){
        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.CombinedAvatarView);
        mDivisionNum = typedArray.getInt(R.styleable.CombinedAvatarView_divisionNum, 1);
        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(1);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        mBigRadius = ( mWidth < mHeight ? mWidth : mHeight ) / 2;
        mAngle_radians = 2 * Math.PI / mDivisionNum;

        double sin_mAngle_divide_2 = Math.sin(mAngle_radians / 2);
        mSmallRadius = (float)(sin_mAngle_divide_2 / (sin_mAngle_divide_2 + 1)) * mBigRadius;
        mRadius = mBigRadius - mSmallRadius;
        mPaintScale = mSmallRadius / mBigRadius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.WHITE);
        canvas.translate(mWidth / 2, mHeight / 2);
        canvas.drawCircle(0, 0, (float)mBigRadius, mPaint);

        for( int i = 1; i <= mDivisionNum; i++) {
            canvas.drawLine(0, 0, 0, (float) (-mBigRadius), mPaint);

            float x = (float) (mRadius * Math.sin(mAngle_radians / 2));
            float y = (float) ( - mRadius * Math.cos(mAngle_radians / 2));//注意这个是负值！
            canvas.drawCircle(x, y, (float)mSmallRadius, mPaint);

            if(mResources != null){
                Bitmap mOriginBitmap = BitmapFactory.decodeResource(getResources(), mResources[i - 1]);
                Bitmap mRoundBitmap = getCroppedRoundBitmap(mOriginBitmap, (int) mSmallRadius);


                Bitmap mRoundBitmap2 = null;
                try {
                    mRoundBitmap2 = adjustPhotoRotation(mRoundBitmap, -360 * (i - 1) / mDivisionNum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                canvas.drawBitmap(mRoundBitmap2, 0, (float) (y - mSmallRadius), mPaint);
            }



//            drawSmallCircle(canvas);
            canvas.rotate( (float)360 / mDivisionNum );
        }

    }

//  此处不使用旋转后画圆，会因精度丢失而不准确
    public void drawSmallCircle(Canvas canvas){
        canvas.save();
        canvas.rotate((float) (180 / mDivisionNum));
        canvas.drawCircle(0, (float) (-mRadius), (float) mSmallRadius, mPaint);
        canvas.restore();
    }

    /**
     * 获取裁剪后的圆形图片
     *
     * @param radius
     *            半径
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    Bitmap adjustPhotoRotation(Bitmap bm, final float orientationDegree) throws Exception {
        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);

        try {
            Bitmap bm1 = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
            Bitmap bm2 = getCroppedCenterBitmap(bm1, (int) mSmallRadius);
            return bm2;
        } catch (OutOfMemoryError ex) {
            throw new Exception(ex);
        }


//        Matrix m = new Matrix();
//        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
//        float targetX, targetY;
//        if (orientationDegree == 90) {
//            targetX = bm.getHeight();
//            targetY = 0;
//        } else {
//            targetX = bm.getHeight();
//            targetY = bm.getWidth();
//        }
//
//        final float[] values = new float[9];
//        m.getValues(values);
//
//        float x1 = values[Matrix.MTRANS_X];
//        float y1 = values[Matrix.MTRANS_Y];
//
////        m.postTranslate(targetX - x1, targetY - y1);
//
//        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
//        Paint paint = new Paint();
//        Canvas canvas = new Canvas(bm1);
//        canvas.translate( bm.getWidth() / 2, bm.getHeight() / 2);
//        canvas.drawBitmap(bm, m, paint);
//
//        return bm1;
    }

    public Bitmap getCroppedCenterBitmap(Bitmap bm, int radius){
        int diameter = radius * 2;
        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        int tWidth = bm.getWidth() / 2;
        int tHeight = bm.getHeight() / 2;
        Rect srcRec = new Rect(tWidth - radius, tHeight - radius, tWidth + radius, tHeight + radius);
        Rect rect = new Rect(0, 0, diameter, diameter);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(radius ,radius, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bm, srcRec, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();


        return output;
    }

}
