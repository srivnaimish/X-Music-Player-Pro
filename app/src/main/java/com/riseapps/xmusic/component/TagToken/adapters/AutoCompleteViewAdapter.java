package com.riseapps.xmusic.component.TagToken.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

abstract public class AutoCompleteViewAdapter<T> extends ArrayAdapter<T> {

    private List<T> originalObjects;
    private Filter filter;


    public AutoCompleteViewAdapter(Context context, int resource, T[] objects) {
        this(context, resource, 0, objects);
    }

    public AutoCompleteViewAdapter(Context context, int resource, int textViewResourceId, T[] objects) {
        this(context, resource, textViewResourceId, new ArrayList<>(Arrays.asList(objects)));
    }

    @SuppressWarnings("unused")
    public AutoCompleteViewAdapter(Context context, int resource, List<T> objects) {
        this(context, resource, 0, objects);
    }


    public AutoCompleteViewAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, new ArrayList<>(objects));
        this.originalObjects = objects;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyDataSetChanged() {
        ((AppFilter)getFilter()).setSourceObjects(this.originalObjects);
        super.notifyDataSetChanged();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void notifyDataSetInvalidated(){
        ((AppFilter)getFilter()).setSourceObjects(this.originalObjects);
        super.notifyDataSetInvalidated();
    }

    @Override
    public Filter getFilter() {
        if (filter == null)
            filter = new AppFilter(originalObjects);
        return filter;
    }

    abstract protected boolean keepObject(T obj, String mask);

    private class AppFilter extends Filter {

        private ArrayList<T> sourceObjects;

        public AppFilter(List<T> objects) {
            setSourceObjects(objects);
        }

        public void setSourceObjects(List<T> objects) {
            synchronized (this) {
                sourceObjects = new ArrayList<T>(objects);
            }
        }

        @Override
        protected FilterResults performFiltering(CharSequence chars) {
            FilterResults result = new FilterResults();
            if (chars != null && chars.length() > 0) {
                String mask = chars.toString();
                ArrayList<T> keptObjects = new ArrayList<T>();

                for (T object : sourceObjects) {
                    if (keepObject(object, mask))
                        keptObjects.add(object);
                }
                result.count = keptObjects.size();
                result.values = keptObjects;
            } else {
                // add all objects
                result.values = sourceObjects;
                result.count = sourceObjects.size();
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if (results.count > 0) {
                AutoCompleteViewAdapter.this.addAll((Collection)results.values);
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}