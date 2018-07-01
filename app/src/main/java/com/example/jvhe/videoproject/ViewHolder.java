package com.example.jvhe.videoproject;

import android.util.SparseArray;
import android.view.View;

/**
 * 이정배가 만든거 아님!! 소스를 긁어옴!
 * 뷰가 가지고 있는 개체 속성들을 전부 자동으로 긁어와서 뷰 홀더에 넣어줌!
 */


public class ViewHolder {
    @SuppressWarnings("unchecked")
    public static <T extends View> T get(View view, int id) {
        SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
        if (viewHolder == null) {
            viewHolder = new SparseArray<View>();
            view.setTag(viewHolder);
        }
        View childView = viewHolder.get(id);
        if (childView == null) {
            childView = view.findViewById(id);
            viewHolder.put(id, childView);
        }
        return (T) childView;
    }
}