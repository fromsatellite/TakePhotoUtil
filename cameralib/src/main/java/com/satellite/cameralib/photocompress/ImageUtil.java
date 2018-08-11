package com.satellite.cameralib.photocompress;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.NinePatch;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@SuppressLint({"SimpleDateFormat"})
public final class ImageUtil
{
    private static final String TAG = "ImageUtil";
    public static final int PHOTO_QUALITY = 95;
    public static final int MAX_QUALITY = 720;

    public static Bitmap createImageByBytes(byte[] bytes)
    {
        Bitmap img = null;
        try
        {
            img = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        catch (Exception e)
        {
            recycleBitmap(img);
            img = null;
        }
        catch (OutOfMemoryError e)
        {
            recycleBitmap(img);
            img = null;
        }
        return img;
    }

    public static void recycleBitmap(Bitmap img)
    {
        if ((img != null) && (!img.isRecycled()))
        {
            img.recycle();
            img = null;
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
    {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8)
        {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        }
        else
        {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels)
    {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = maxNumOfPixels == -1 ? 1 : (int)Math.ceil(
                Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = minSideLength == -1 ? 128 : (int)Math.min(
                Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        }
        if (minSideLength == -1) {
            return lowerBound;
        }
        return upperBound;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight)
    {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if ((height > reqHeight) || (width > reqWidth))
        {
            int heightRatio = Math.round(height /
                    reqHeight);
            int widthRatio = Math.round(width / reqWidth);

            inSampleSize = heightRatio > widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    public static Bitmap getSmallBitmap(String filePath)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, 108, 192);

        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    public static Bitmap getNormalBitmap(String filePath, int width, int length)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        options.inSampleSize = calculateInSampleSize(options, width, length);

        options.inJustDecodeBounds = false;
        options.inInputShareable = true;

        Bitmap bitmap = null;
        try
        {
            bitmap = BitmapFactory.decodeFile(filePath, options);
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            options.inSampleSize *= 2;
            try
            {
                bitmap = BitmapFactory.decodeFile(filePath, options);
            }
            catch (OutOfMemoryError e2)
            {
                e2.printStackTrace();
            }
        }
        int degree = readPictureDegree(filePath);
        if (degree != 0) {
            bitmap = rotaingImageView(degree, bitmap);
        }
        return bitmap;
    }

    public static Bitmap compressImageFromFile(String srcPath)
    {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800.0F;
        float ww = 480.0F;
        int be = 1;
        if ((w > h) && (w > ww)) {
            be = (int)(newOpts.outWidth / ww);
        } else if ((w < h) && (h > hh)) {
            be = (int)(newOpts.outHeight / hh);
        }
        if (be <= 0) {
            be = 1;
        }
        newOpts.inSampleSize = be;

        newOpts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        newOpts.inPurgeable = true;
        newOpts.inInputShareable = true;

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return bitmap;
    }

    public static void compressBmpToFile(Bitmap bmp, String filePath)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            bmp.compress(Bitmap.CompressFormat.JPEG, 95, baos);
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void compressBmpToFile(Bitmap bmp, String filePath, int quality)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            bmp.compress(Bitmap.CompressFormat.JPEG, quality, baos);
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static int readPictureDegree(String path)
    {
        int degree = 0;
        try
        {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt("Orientation",
                    1);
            switch (orientation)
            {
                case 6:
                    degree = 90;
                    break;
                case 3:
                    degree = 180;
                    break;
                case 8:
                    degree = 270;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return degree;
    }

    public static Bitmap rotaingImageView(int angle, Bitmap bitmap)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap resizedBitmap = null;
        try
        {
            resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return resizedBitmap;
    }

    public static Bitmap image_Compression(String filePath, DisplayMetrics dm, int maxQuality)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        int widthRatio = (int)Math.ceil(opts.outWidth / maxQuality);
        int heightRatio = (int)Math.ceil(opts.outHeight / maxQuality);
        if ((widthRatio > 1) || (heightRatio > 1)) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inInputShareable = true;
        Bitmap bmp = null;
        try
        {
            bmp = BitmapFactory.decodeFile(filePath, opts);
            bmp = scaleImg(bmp, maxQuality);
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            opts.inSampleSize *= 2;
            bmp = BitmapFactory.decodeFile(filePath, opts);
            bmp = scaleImg(bmp, maxQuality);
        }
        return bmp;
    }

    public static Bitmap image_Compression_ByCompressValue(String filePath, int maxSize, int compressValue)
    {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        maxSize = opts.outWidth > opts.outHeight ? opts.outWidth : opts.outHeight;
        if (maxSize > compressValue) {
            maxSize = compressValue;
        }
        int widthRatio = (int)Math.ceil(opts.outWidth / maxSize);
        int heightRatio = (int)Math.ceil(opts.outHeight / maxSize);
        if ((widthRatio > 1) || (heightRatio > 1)) {
            if (widthRatio > heightRatio) {
                opts.inSampleSize = widthRatio;
            } else {
                opts.inSampleSize = heightRatio;
            }
        }
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inInputShareable = true;
        Bitmap bmp = null;
        try
        {
            bmp = BitmapFactory.decodeFile(filePath, opts);
            bmp = scaleImg(bmp, maxSize);
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            opts.inSampleSize *= 2;
            bmp = BitmapFactory.decodeFile(filePath, opts);
            bmp = scaleImg(bmp, maxSize);
        }
        return bmp;
    }

    private static Bitmap scaleImg(Bitmap img, float quality)
    {
        if (img == null) {
            return null;
        }
        float sHeight = 0.0F;
        float sWidth = 0.0F;

        Bitmap result = img;
        if ((img.getWidth() >= img.getHeight()) && (img.getWidth() > quality))
        {
            sWidth = quality;
            sHeight = quality * img.getHeight() / img.getWidth();
            result = Bitmap.createScaledBitmap(img, (int)sWidth, (int)sHeight, true);
            if ((img != null) && (!img.isRecycled()) && (!img.equals(result))) {
                img.recycle();
            }
        }
        else if ((img.getHeight() > img.getWidth()) && (img.getHeight() > quality))
        {
            sHeight = quality;
            sWidth = quality * img.getWidth() / img.getHeight();
            result = Bitmap.createScaledBitmap(img, (int)sWidth, (int)sHeight, true);
            if ((img != null) && (!img.isRecycled()) && (!img.equals(result))) {
                img.recycle();
            }
        }
        return result;
    }

    public static ViewGroup.LayoutParams setProgressImgageWidth(Activity act, ImageView imgView, int imgID, int newImgWidth, int newProgerssTotal, ImageView imgExpProgressBg)
    {
        Bitmap bitMap = BitmapFactory.decodeResource(act.getResources(), imgID);
        ViewGroup.LayoutParams params = imgView.getLayoutParams();
        int progressBgWidth = imgExpProgressBg.getLayoutParams().width / 2;
        if (progressBgWidth <= 0)
        {
            DisplayMetrics dm = new DisplayMetrics();
            act.getWindowManager().getDefaultDisplay().getMetrics(dm);
            progressBgWidth = dm.widthPixels * 3 / 5 / 2;
        }
        double newImgWidthDouble = newImgWidth * 1.0D;
        double result = newImgWidthDouble / newProgerssTotal * progressBgWidth;
        DecimalFormat df = new DecimalFormat("0");
        String data = df.format(result);

        params.width = dp2px(act.getApplicationContext(), Integer.valueOf(data).intValue());
        params.height = bitMap.getHeight();
        return params;
    }

    public static int dp2px(Context context, int dp)
    {
        float scale = context.getResources().getDisplayMetrics().density;
        if (scale > 2.0D) {
            scale = 2.0F;
        }
        return (int)(dp * scale + 0.5F);
    }

    public static ViewGroup.LayoutParams setProgressImgageWidth(TextView textView, ViewGroup.LayoutParams imgParams)
    {
        ViewGroup.LayoutParams params = textView.getLayoutParams();
        params.width = imgParams.width;

        return params;
    }

    public static Bitmap rotate(Bitmap b, int degrees, boolean recycleOri)
    {
        return rotateAndMirror(b, degrees, false, recycleOri);
    }

    public static Bitmap rotateAndMirror(Bitmap b, int degrees, boolean mirror, boolean recycleOri)
    {
        if (((degrees != 0) || (mirror)) && (b != null))
        {
            Matrix m = new Matrix();
            m.setRotate(degrees, b.getWidth() / 2.0F, b.getHeight() / 2.0F);
            if (mirror)
            {
                m.postScale(-1.0F, 1.0F);
                degrees = (degrees + 360) % 360;
                if ((degrees == 0) || (degrees == 180)) {
                    m.postTranslate(b.getWidth(), 0.0F);
                } else if ((degrees == 90) || (degrees == 270)) {
                    m.postTranslate(b.getHeight(), 0.0F);
                } else {
                    throw new IllegalArgumentException("Invalid degrees=" + degrees);
                }
            }
            try
            {
                Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, true);
                if ((b != b2) && (recycleOri))
                {
                    b.recycle();
                    b = b2;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            catch (OutOfMemoryError localOutOfMemoryError) {}
        }
        return b;
    }

    public static String generatePictureFilename()
    {
        long dateTake = System.currentTimeMillis();
        Date date = new Date(dateTake);
        SimpleDateFormat sdf = new SimpleDateFormat("'tu'_yyyyMMdd_HHmmss");
        String filename = sdf.format(date);
        return filename;
    }

    public static Bitmap loadBitmapWithSizeCheck(File bitmapFile)
    {
        Bitmap bmp = null;
        FileInputStream fis = null;
        try
        {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(bitmapFile.getAbsolutePath(), opt);
            int width = opt.outWidth;
            int height = opt.outHeight;

            BitmapFactory.Options newOpt = new BitmapFactory.Options();
            long fileSize = bitmapFile.length();
            long MAX_SIZE = 20480L;
            if (fileSize <= 20480L)
            {
                newOpt.inSampleSize = 1;
            }
            else if (fileSize <= 81920L)
            {
                newOpt.inSampleSize = 2;
            }
            else
            {
                long times = fileSize / 20480L;
                newOpt.inSampleSize = ((int)(Math.log(times) / Math.log(2.0D)) + 1);
            }
            newOpt.inDensity = 160;
            newOpt.outHeight = height;
            newOpt.outWidth = width;
            fis = new FileInputStream(bitmapFile);
            bmp = BitmapFactory.decodeStream(fis, null, newOpt);
            return bmp;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                    fis = null;
                }
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
        return null;
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx)
    {
        int size = 0;
        size = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getWidth() : bitmap.getHeight();

        roundPx = size * roundPx / 60.0F;

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paint = new Paint();

        RectF rectF = new RectF(0.0F, 0.0F, size, size);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(-4276546);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        int left = 0;
        if (size > bitmap.getWidth()) {
            left = (size - bitmap.getWidth()) / 2;
        }
        int top = 0;
        if (size > bitmap.getHeight()) {
            top = (size - bitmap.getHeight()) / 2;
        }
        canvas.drawBitmap(bitmap, new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new Rect(left, top, left == 0 ? size : left + bitmap.getWidth(), top == 0 ? size :
                        top + bitmap.getHeight()), paint);

        return output;
    }

    public static int findMaxSampleSize(int factor)
    {
        if (factor <= 1) {
            return 1;
        }
        switch (factor)
        {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
            case 4:
                return 4;
            case 5:
            case 6:
            case 7:
            case 8:
                return 8;
        }
        if (factor <= 16) {
            return 16;
        }
        return factor;
    }

    public static Bitmap getRoundCornerImage(Context context, Bitmap srcBitmap, int destResId, int wid, int hei)
    {
        if ((wid == 0) || (hei == 0)) {
            return null;
        }
        Bitmap roundConcerImage = Bitmap.createBitmap(wid, hei, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, wid, hei);
        paint.setAntiAlias(true);

        Bitmap bitmap_9patch = BitmapFactory.decodeResource(context.getResources(), destResId);
        NinePatch np = new NinePatch(bitmap_9patch, bitmap_9patch.getNinePatchChunk(), null);
        np.draw(canvas, rect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(srcBitmap, null, rect, paint);

        return roundConcerImage;
    }

    public static Bitmap getCombineImage(Context context, Bitmap srcBitmap, int destResId, int wid, int hei)
    {
        if ((wid == 0) || (hei == 0)) {
            return null;
        }
        Bitmap roundConcerImage = Bitmap.createBitmap(wid, hei, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundConcerImage);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, wid, hei + 1);
        paint.setAntiAlias(true);

        Bitmap bitmap_9patch = BitmapFactory.decodeResource(context.getResources(), destResId);
        NinePatch np = new NinePatch(bitmap_9patch, bitmap_9patch.getNinePatchChunk(), null);
        np.draw(canvas, rect, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));

        rect.bottom -= 1;
        canvas.drawBitmap(srcBitmap, null, rect, paint);

        return roundConcerImage;
    }

    public static BitmapDrawable resizeImage(Bitmap bitmap, int w, int h)
    {
        Bitmap BitmapOrg = bitmap;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);

        return new BitmapDrawable(resizedBitmap);
    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int w, int h)
    {
        Bitmap BitmapOrg = bitmap;

        int width = BitmapOrg.getWidth();
        int height = BitmapOrg.getHeight();
        int newWidth = w;
        int newHeight = h;

        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;

        Matrix matrix = new Matrix();

        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(BitmapOrg, 0, 0, width,
                height, matrix, true);

        return resizedBitmap;
    }

    public static int getOrientation(Context context, String sFileName)
    {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] {
                        "orientation" },
                null, null, null);

        boolean bRet = cursor.moveToLast();
        int nRet = bRet ? cursor.getInt(0) : 0;

        cursor.close();

        return nRet;
    }

    public static Bitmap getCorrectlyOrientedImage(Context context, Uri photoUri, boolean bNeedCompress)
    {
        int MAX_IMAGE_DIMENSION = 1200;
        InputStream is = null;
        Bitmap srcBitmap = null;
        try
        {
            is = context.getContentResolver().openInputStream(photoUri);
            BitmapFactory.Options dbo = new BitmapFactory.Options();
            dbo.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(is, null, dbo);
            is.close();

            String sFile = photoUri.getPath();
            int orientation = getOrientation(context, sFile);
//            int rotatedHeight;
            int rotatedWidth;
            int rotatedHeight;
            if ((orientation == 90) || (orientation == 270))
            {
                rotatedWidth = dbo.outHeight;
                rotatedHeight = dbo.outWidth;
            }
            else
            {
                rotatedWidth = dbo.outWidth;
                rotatedHeight = dbo.outHeight;
            }
            is = context.getContentResolver().openInputStream(photoUri);
            if ((rotatedWidth > MAX_IMAGE_DIMENSION) || (rotatedHeight > MAX_IMAGE_DIMENSION))
            {
                float widthRatio = rotatedWidth / MAX_IMAGE_DIMENSION;
                float heightRatio = rotatedHeight / MAX_IMAGE_DIMENSION;
                float maxRatio = Math.max(widthRatio, heightRatio);

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = ((int)maxRatio);
                srcBitmap = BitmapFactory.decodeStream(is, null, options);
            }
            else if ((rotatedWidth > 1024) && (bNeedCompress))
            {
                float widthRatio = rotatedWidth / 1024.0F;

                float maxRatio = widthRatio;

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = ((int)(maxRatio + 1.0F));
                srcBitmap = BitmapFactory.decodeStream(is, null, options);
            }
            else
            {
                srcBitmap = BitmapFactory.decodeStream(is);
            }
            is.close();
            if (orientation > 0)
            {
                Matrix matrix = new Matrix();
                matrix.postRotate(orientation);

                srcBitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(),
                        srcBitmap.getHeight(), matrix, true);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return srcBitmap;
    }

    public static void close(OutputStream os)
    {
        try
        {
            if (os != null)
            {
                os.close();
                os = null;
            }
        }
        catch (Exception localException) {}
    }
}
