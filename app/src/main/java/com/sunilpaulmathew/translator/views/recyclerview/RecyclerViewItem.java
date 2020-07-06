package com.sunilpaulmathew.translator.views.recyclerview;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by willi on 24.04.16.
 */
public abstract class RecyclerViewItem {

    private boolean mFullspan;
    private View mView;

    public interface OnItemClickListener {
        void onClick(RecyclerViewItem item);
    }

    private OnItemClickListener mOnItemClickListener;

    public void onCreateView(View view) {
        mView = view;
        fullSpan(mFullspan);
        refresh();
    }

    @LayoutRes
    public abstract int getLayoutRes();

    public void onRecyclerViewCreate(Activity activity) {
    }

    void onCreateHolder() {
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    void setOnViewChangeListener() {
    }

    OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setFullSpan(boolean fullspan) {
        mFullspan = fullspan;
        fullSpan(fullspan);
    }

    private void fullSpan(boolean fullspan) {
        if (mView != null) {
            StaggeredGridLayoutManager.LayoutParams layoutParams =
                    new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setFullSpan(fullspan);
            mView.setLayoutParams(layoutParams);
        }
    }

    protected void refresh() {
    }

    public void onResume() {
    }

    public void onPause() {
    }

    public void onDestroy() {
    }

    boolean cardCompatible() {
        return true;
    }

    boolean cacheable() {
        return false;
    }

}