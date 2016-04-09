package de.lukaskoerfer.taglauncher;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Koerfer on 08.04.2016.
 */
public class LoadingImage {

    private ImageView Image;
    private ProgressBar Animator;

    public LoadingImage(ImageView image, ProgressBar animator) {
        this.Image = image;
        this.Animator = animator;
    }

    public void switchToImage(Drawable draw) {
        this.Image.setImageDrawable(draw);
        this.Image.setVisibility(View.VISIBLE);
        this.Animator.setVisibility(View.INVISIBLE);
    }

    public void switchToLoading() {
        this.Image.setVisibility(View.INVISIBLE);
        this.Animator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof LoadingImage) {
            LoadingImage li = (LoadingImage) object;
            return (li.Animator == this.Animator) && (li.Image == this.Image);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return (this.Image.hashCode() / 2) + (this.Animator.hashCode() / 2);
    }
}
