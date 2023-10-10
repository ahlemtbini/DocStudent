package com.technifysoft.docstudent;

import android.widget.Filter;

import java.util.ArrayList;

public class FilterDoc extends Filter {

    //ArrayList in which we want to search
    ArrayList<ModelDoc> filterList;

    //Adapter in which filter needs to be implemented
    AdapterDoc adapterDoc;

    //constructor


    public FilterDoc(ArrayList<ModelDoc> filterList, AdapterDoc adapterDoc) {
        this.filterList = filterList;
        this.adapterDoc = adapterDoc;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();

        //Value should not be empty
        if (constraint != null && constraint.length() > 0){

            //avoid sensitivity
            constraint = constraint.toString().toUpperCase();
            ArrayList<ModelDoc> filteredModels = new ArrayList<>();

            for(int i=0;i<filterList.size();i++){
                //validate
                if (filterList.get(i).gettitle().toUpperCase().contains(constraint)){
                    filteredModels.add(filterList.get(i));
                }
            }

            results.count = filteredModels.size();
            results.values = filteredModels;

        }
        else {
            results.count = filterList.size();
            results.values = filterList;
        }


        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, FilterResults results) {
        //apply filter Changes
        adapterDoc.DocArrayList = (ArrayList<ModelDoc>)results.values;

        //notify Changes
        adapterDoc.notifyDataSetChanged();

    }
}
