package com.zgf.marqueview;

public interface ProgressListener {

    void onAnimationCancel();

    void onAnimationEnd();

    void onAnimationRepeat();

    void onAnimationStart();

    void onAnimationPause();

    void onAnimationResume();
}
