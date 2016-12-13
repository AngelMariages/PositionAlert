package org.angelmariages.positionalertv2.userManager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class DownloadUserImage extends AsyncTask<String, Integer, Bitmap> {

    private ImageView imageView;
    private Context mContext;

    //@TODO Do this with a listener and return the bitmap
    public DownloadUserImage(ImageView imageView, Context context) {
        this.imageView = imageView;
        mContext = context;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap icon = null;
        try {
            InputStream is = new URL(urls[0]).openStream();
            icon = BitmapFactory.decodeStream(is);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return icon;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        RoundedBitmapDrawable dr =
                RoundedBitmapDrawableFactory.create(mContext.getResources(), bitmap);
        dr.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        imageView.setImageDrawable(dr);
        //imageView.setImageBitmap(bitmap);
    }
}
