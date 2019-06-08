package com.nullpointerexception.cicerone.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

/**
 *      ProfileImageFetcher
 *
 *      Is used to retrieve or generate a drawable to use as profile image.
 */
public class ProfileImageFetcher
{
    /**   Context used to do functionality of this object  */
    private Context context;
    /**   Drawable loaded (or generated)  */
    private Drawable resource;

    /**   Interface used to provide an implementation of the callback method invoked when resource is ready.  */
    public interface OnImageFetchedListener { void onImageFetched(Drawable drawable); }

    @SuppressLint("CheckResult")
    public ProfileImageFetcher(@NonNull Context context)
    {
        this.context = context;
    }

    /**
     *      Tries to get an image from url provided by given user.
     *      It generates one if it doesn't have an url or if can't be loaded.
     *
     *      @param user     User that provides image url.
     *      @param onImageFetchedListener   Implementation of the callback method.
     */
    @SuppressLint("CheckResult")
    public void fetchImageOf(@NonNull User user, OnImageFetchedListener onImageFetchedListener)
    {
        String url = user.getProfileImageUrl();

        if(url != null && !url.isEmpty())
        {
            Glide.with(context)
                    .load(url)
                    .listener(new RequestListener<Drawable>()
                    {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource)
                        {
                            if(e != null)
                                Log.e(ProfileImageFetcher.class.getSimpleName(), e.toString());

                            resource = new ProfileImageLetter(user.getDisplayName());

                            if(onImageFetchedListener != null)
                                onImageFetchedListener.onImageFetched(resource);

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource)
                        {

                            return false;
                        }
                    })
                    .into(new CustomTarget<Drawable>()
                    {
                        @Override
                        public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition)
                        {
                            ProfileImageFetcher.this.resource = resource;

                            if(onImageFetchedListener != null)
                                onImageFetchedListener.onImageFetched(resource);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder)
                        {
                            ProfileImageFetcher.this.resource = placeholder;

                            if(onImageFetchedListener != null)
                                onImageFetchedListener.onImageFetched(resource);
                        }
                    });
        }
        else
        {
            resource = new ProfileImageLetter(user.getDisplayName());

            if(onImageFetchedListener != null)
                onImageFetchedListener.onImageFetched(resource);
        }
    }

    public Context getContext()
    {
        return context;
    }

    public Drawable getResource()
    {
        return resource;
    }
}

/**
 *      ProfileImageLetter
 *
 *      Drawable generated from a given string
 */
class ProfileImageLetter extends ShapeDrawable
{
    /**   Letter displayed into drawable  */
    private char letter;

    /**
     *      Constructs object
     *
     *      @param name Name of user
     */
    ProfileImageLetter(String name)
    {
        if( name != null && ! name.isEmpty())
            this.letter = name.charAt(0);
        setShape(new OvalShape());
        setIntrinsicWidth(512);
        setIntrinsicHeight(512);
        setColorFilter( generateColorFor(name) , PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    protected void onDraw(Shape shape, Canvas canvas, Paint paint2)
    {
        super.onDraw(shape, canvas, paint2);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(350);

        canvas.drawText( "" + letter , 150, 380, paint);
    }

    /**
     *      It generates a color based on the given string.
     *
     *      @param name     String from which get color.
     *      @return         Color generated.
     */
    private int generateColorFor(String name)
    {
        if( name == null || name.isEmpty())
            return Color.TRANSPARENT;

        int color = Color.rgb(mod(name.hashCode(), 255),
                            mod(name.hashCode() / 255, 255),
                            mod(name.hashCode() / (255 * 255), 255));

        return color;
    }

    /**
     *      Calculation of an equivalent number from x mod y.
     *      Method created to support versions below API 24. ( Math.floorMod() needs API 24 )
     *
     *      @param x    Number from which calculate its equivalent
     *      @param y    Module
     *      @return     An equivalent number of x mod y
     */
    private int mod(int x, int y)
    {
        while(x < 0)
            x += y;

        return x % y;
    }

}