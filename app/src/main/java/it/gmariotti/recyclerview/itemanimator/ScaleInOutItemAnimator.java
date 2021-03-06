/*
 * ******************************************************************************
 *   Copyright (c) 2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */
package it.gmariotti.recyclerview.itemanimator;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 *
 * @see android.support.v7.widget.RecyclerView#setItemAnimator(android.support.v7.widget.RecyclerView.ItemAnimator)
 */
public class ScaleInOutItemAnimator extends BaseItemAnimator {

    private float DEFAULT_SCALE_INITIAL = 0.8f;

    private float mInitialScaleX = DEFAULT_SCALE_INITIAL;
    private float mInitialScaleY = DEFAULT_SCALE_INITIAL;

    private float mEndScaleX = DEFAULT_SCALE_INITIAL;
    private float mEndScaleY = DEFAULT_SCALE_INITIAL;

    private float mOriginalScaleX;
    private float mOriginalScaleY;

    public ScaleInOutItemAnimator(RecyclerView recyclerView) {
        super(recyclerView);
    }

    protected void animateRemoveImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;

        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view).setDuration(getRemoveDuration()).
                scaleX(mEndScaleX).scaleY(mEndScaleY).setListener(new VpaListenerAdapter() {
            @Override
            public void onAnimationEnd(View view) {
                ViewCompat.setScaleX(view, mEndScaleX);
                ViewCompat.setScaleY(view, mEndScaleY);
                dispatchRemoveFinished(holder);
                mRemoveAnimations.remove(holder);
                dispatchFinishedWhenDone();
            }
        }).start();
        mRemoveAnimations.add(holder);
    }

    @Override
    protected void prepareAnimateAdd(RecyclerView.ViewHolder holder) {
        retrieveOriginalScale(holder);
        ViewCompat.setScaleX(holder.itemView, mInitialScaleX);
        ViewCompat.setScaleY(holder.itemView, mInitialScaleY);
    }



    protected void animateAddImpl(final RecyclerView.ViewHolder holder) {
        final View view = holder.itemView;

        ViewCompat.animate(view).cancel();
        ViewCompat.animate(view).scaleX(mOriginalScaleX).scaleY(mOriginalScaleY)
                .setDuration(getAddDuration()).
                setListener(new VpaListenerAdapter() {
                    @Override
                    public void onAnimationCancel(View view) {
                        ViewCompat.setScaleX(view, mOriginalScaleX);
                        ViewCompat.setScaleY(view, mOriginalScaleY);
                    }

                    @Override
                    public void onAnimationEnd(View view) {
                        dispatchAddFinished(holder);
                        mAddAnimations.remove(holder);
                        dispatchFinishedWhenDone();
                    }
                }).start();
        mAddAnimations.add(holder);
    }

    public void setInitialScale(float scaleXY){
        setInitialScale(scaleXY, scaleXY);
    }

    public void setInitialScale(float scaleX, float scaleY){
        mInitialScaleX = scaleX;
        mInitialScaleY = scaleY;

        mEndScaleX = scaleX;
        mEndScaleY = scaleY;
    }

    public void setEndScale(float scaleXY){
        setEndScale(scaleXY, scaleXY);
    }

    public void setEndScale(float scaleX, float scaleY){
        mEndScaleX = scaleX;
        mEndScaleY = scaleY;
    }

    private void retrieveOriginalScale(RecyclerView.ViewHolder holder) {
        mOriginalScaleX =1;// holder.itemView.getScaleX();
        mOriginalScaleY = 1;//holder.itemView.getScaleY();
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2, int i, int i2, int i3, int i4) {
        return false;
    }
}
