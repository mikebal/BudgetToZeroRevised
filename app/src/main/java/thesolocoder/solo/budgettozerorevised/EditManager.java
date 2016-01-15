package thesolocoder.solo.budgettozerorevised;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class EditManager extends Activity {

    Context appContext;
    ArrayList<ListObject> logFiles = new ArrayList<ListObject>();

    public EditManager(Context context, ArrayList<ListObject> logHistory)
    {
        appContext = context;
        logFiles = logHistory;
    }

    public void deleteItem(int position, boolean notRefund, String sourceCategory){ // if false = refund   // true = delete

        ListObject toDelete = getListObjectToDelete(position);    // Get deep-copy of ListObject

        if(!notRefund)
            recalculateBalance(Double.valueOf(toDelete.getCost()) * -1);
        removeArrayIdexes_andSavetoFile(position, toDelete, sourceCategory);
    }

    public void handleEdit(String category, ListObject editedItem, ListObject originalItem, Double priceDifference, int index){

        if(priceDifference != 0)
            recalculateBalance(priceDifference);

        logFiles.remove(index);            // Remove the original edited item
        logFiles.add(index, editedItem);   // Add the new copy of the item
        PurchaseLogManager logManager = new PurchaseLogManager(appContext);
        logManager.writeArraysBackToFile(logFiles, category + ".txt");

        String secondaryTarget = "";
        if(!category.equals("purchaseLog"))     // find if another category needs to be edited aswell
            secondaryTarget = "purchaseLog.txt";
        else if(!editedItem.getCategory().equals("Default"))
            secondaryTarget = editedItem.getCategory() + ".txt";

        if(!secondaryTarget.equals("") && !secondaryTarget.equals("null.txt")) {
            logFiles = logManager.readInPurchaseLog(secondaryTarget, false);
            if(logFiles.size() == 0)        // Check to ensure category is not empty
                return;
            index = getIndexOfListObjectToDelete(originalItem, logFiles);
            if(index == -1) // if the index was found
                return;
            logFiles.remove(index);
            logFiles.add(index, editedItem);
            logManager.writeArraysBackToFile(logFiles, secondaryTarget);
        }
    }

    private void recalculateBalance(double difference)
    {
        double newBalance;

        SharedPreferences savedData;
        savedData = appContext.getSharedPreferences("savedData", 0);// save data
        SharedPreferences.Editor editor = savedData.edit();

        if(!savedData.getString("budget_spending", "-X").equals("-X"))
        {
            newBalance = Double.valueOf(savedData.getString("budget_spending","00.00"));

            if (savedData.getBoolean("count_direction_up", false))
                newBalance += difference;
            else
                newBalance -= difference;

            DecimalFormat df = new DecimalFormat("0.00");
            df.setMaximumFractionDigits(2);
            editor.putString("budget_spending", df.format(newBalance));
            editor.apply();
        }
    }

    private void removeArrayIdexes_andSavetoFile(int index, ListObject toDelete, String sourceCategory) {

        String alternative_category = logFiles.get(index).getCategory();
        if(alternative_category.equals("Default"))
            alternative_category = "purchaseLog";
        PurchaseLogManager logManager = new PurchaseLogManager(appContext);
        logFiles.remove(index);                         // Delete the entry from source list
        logManager.writeArraysBackToFile(logFiles, sourceCategory+".txt");

        if(!alternative_category.equals(sourceCategory))
        {
            searchAndDelete(toDelete, alternative_category);
        }
        else if(!sourceCategory.equals("purchaseLog"))
            searchAndDelete(toDelete, "purchaseLog");
    }

    private void searchAndDelete(ListObject toDelete, String alternative_category){

        int index;
        PurchaseLogManager logManager = new PurchaseLogManager(appContext);

        ArrayList<ListObject> alt_category_list = logManager.readInPurchaseLog(alternative_category+".txt", false);
        index = getIndexOfListObjectToDelete(toDelete, alt_category_list);
        if(index < alt_category_list.size() && alt_category_list.size() != 0) {
            alt_category_list.remove(index);
            logManager.writeArraysBackToFile(alt_category_list, alternative_category+".txt");
        }
    }

    private ListObject getListObjectToDelete(int position){
        String date = logFiles.get(position).getDate();
        String disc = logFiles.get(position).getDiscription();
        String cost = logFiles.get(position).getCost();
        String category = logFiles.get(position).getCategory();

        ListObject toDelete = new ListObject(date, disc, cost, category);
        return toDelete;
    }

    private int getIndexOfListObjectToDelete(ListObject toDelete,ArrayList<ListObject> searchList)
    {
        int counter = 0;
        boolean found = false;

        while(counter < searchList.size()){
            if(searchList.get(counter).getDate().equals(toDelete.getDate()))
                if(searchList.get(counter).getDiscription().equals(toDelete.getDiscription()))
                    if(searchList.get(counter).getCost().equals(toDelete.getCost()))
                        if(searchList.get(counter).getCategory().equals(toDelete.getCategory())) {
                            found = true;
                            break;
                        }
            counter++;
        }
        if(!found)
            counter = -1;
        return counter;
    }

}
