package thesolocoder.solo.budgettozerorevised;

import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class CategoryManager {
    private Context appContext;

    public CategoryManager(Context context)
    {
        appContext = context;
    }

    public ArrayList readInCategories()
    {
        String item;
        ArrayList<String> categories = new ArrayList<String>();
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(appContext.openFileInput("#categories.txt")));
            while ((item = inputReader.readLine()) != null){
                categories.add(item);
            }
            inputReader.close();
        }
        catch (IOException e) {
            categories.add("Default");
            categories.add("New Category");
        }
        return categories;
    }

    private void writeArraysBackToFile(ArrayList<String> categories)
    {
        int arraySize =  categories.size();

        try{
            OutputStreamWriter out = new OutputStreamWriter(appContext.openFileOutput("#categories.txt", appContext.MODE_PRIVATE));
            for(int i = 0; i < arraySize; i++)
            {
                out.write(categories.get(i).toString() + "\n");
            }
            out.close();
        } catch (java.io.IOException e) {
            //do something if an IOException occurs.
        }
    }

    public void addAlphabetically(String newCategory){

        ArrayList<String> categories = readInCategories();
        int index = 2;
        boolean added = false;

        while (index < categories.size()){
            if(newCategory.compareTo(categories.get(index)) == 0) {
                added =true;
                break;
            }
            if(newCategory.compareTo(categories.get(index)) < 0) {
                categories.add(index, newCategory);
                added = true;
                break;
            }
            index++;
        }
        if(!added)
            categories.add(newCategory);
        writeArraysBackToFile(categories);
    }
    public void removeSingleCategory(String categoryName, boolean deleteAssociatedPurchases) {

        int index;
        ArrayList<String> categories = readInCategories();
        for(index = 0; index < categories.size(); index++)
        {
            if(categories.get(index).equals(categoryName))
                categories.remove(index);;
        }
        writeArraysBackToFile(categories);
        managePurchaseLogs(categoryName, deleteAssociatedPurchases);
    }

    private void managePurchaseLogs(String categoryName, boolean deleteAssociatedPurchases)
    {
        PurchaseLogManager fileHandler = new PurchaseLogManager(appContext);
        fileHandler.overWriteCategory(categoryName + ".txt");
        if(deleteAssociatedPurchases)
            fileHandler.deleteCat_fromPurchaseLog(categoryName);
    }

    public void renameCategory(String orignalName, String newName)
    {
        PurchaseLogManager fileHandler = new PurchaseLogManager(appContext);
        ArrayList<ListObject> purchaseList = fileHandler.readInPurchaseLog(orignalName + ".txt", false);

        changeCategoryName(purchaseList, orignalName, newName);
        fileHandler.writeArraysBackToFile(purchaseList, newName + ".txt");
        addAlphabetically(newName);
        removeSingleCategory(orignalName, false);

        purchaseList = fileHandler.readInPurchaseLog("purchaseLog.txt", false);
        changeCategoryName(purchaseList, orignalName, newName);
        fileHandler.writeArraysBackToFile(purchaseList, "purchaseLog.txt");
    }

    private void changeCategoryName(ArrayList<ListObject> purchaseLog, String oldName, String newName)
    {
        for(int i = 0; i < purchaseLog.size(); i++)
        {
            if(purchaseLog.get(i).getCategory().equals(oldName))
                purchaseLog.get(i).setCategory(newName);
        }
    }
    public boolean isValidCategoryName(String fileName)
    {
        boolean isValid;
        switch(fileName){
            case "": isValid = false; break;
            case "Settings": isValid = false; break;
            case "purchaseLog": isValid = false; break;
            case "Category Manager": isValid = false; break;
            case "All": isValid = false; break;
            case "#categories": isValid = false; break;
            case "null": isValid = false; break;
            case "Default": isValid = false; break;
            default: isValid = true;
        }
        return isValid;
    }
}
