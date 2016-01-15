package thesolocoder.solo.budgettozerorevised;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class PurchaseLogManager extends Activity{
    Context appContext;

    public PurchaseLogManager(Context context)
    {
        appContext = context;
    }

    public ArrayList readInPurchaseLog(String fileName, boolean converting)
    {
        String inputDate;
        String inputDisc;
        String inputCost;
        String inputCategory;
        ListObject singleListItem;
        ArrayList<ListObject> logFiles = new ArrayList<ListObject>();

        if(isLogWanted())
            try {
                BufferedReader inputReader = new BufferedReader(new InputStreamReader(appContext.openFileInput(fileName)));

                logFiles.clear();

                while ((inputDate = inputReader.readLine()) != null)
                {
                    inputDisc = inputReader.readLine();
                    inputCost = inputReader.readLine();
                    if(!converting) {
                        inputCategory = inputReader.readLine();
                        singleListItem = new ListObject(inputDate, inputDisc, inputCost, inputCategory);
                    }
                    else
                        singleListItem = new ListObject(inputDate, inputDisc, inputCost, "default");
                    logFiles.add(0, singleListItem);
                }
                inputReader.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        return logFiles;
    }

    public void writeArraysBackToFile(ArrayList<ListObject> logFiles, String filename)
    {
        ListObject itemToWrite;

        int arraySize =  logFiles.size();

        try{
            OutputStreamWriter out = new OutputStreamWriter(appContext.openFileOutput(filename, appContext.MODE_PRIVATE));
            for(int i = (arraySize - 1); i >= 0; i--)
            {
                itemToWrite = logFiles.get(i);

                out.write(itemToWrite.getDate() + "\n");
                out.write(itemToWrite.getDiscription() + "\n");
                out.write(itemToWrite.getCost() + "\n");
                out.write(itemToWrite.getCategory() + "\n");
            }
            out.close();
        } catch (java.io.IOException e) {
            //do something if an IOException occurs.
        }
    }
    private boolean isLogWanted()
    {
        SharedPreferences savedData;
        savedData = appContext.getSharedPreferences("savedData", 0);// save data

        if(savedData.getBoolean("show_history_on",true))
            return true;
        else
            return false;
    }

    public void changeFileFormat(){
        ArrayList<ListObject> logFiles = readInPurchaseLog("purchaseLog.txt", true);
        String filename = "purchaseLog.txt";
        ListObject itemToWrite;

        int arraySize =  logFiles.size();

        try{
            OutputStreamWriter out = new OutputStreamWriter(appContext.openFileOutput(filename, appContext.MODE_PRIVATE));
            for(int i = (arraySize - 1); i >= 0; i--)
            {
                itemToWrite = logFiles.get(i);

                out.write(itemToWrite.getDate() + "\n");
                out.write(itemToWrite.getDiscription() + "\n");
                out.write(itemToWrite.getCost() + "\n");
                out.write("default\n");
            }
            out.close();
        } catch (java.io.IOException e) {
            //do something if an IOException occurs.
        }
    }

    public void overWriteCategory(String categoryName) {
        try{
            OutputStreamWriter out = new OutputStreamWriter(appContext.openFileOutput(categoryName, appContext.MODE_PRIVATE));
            out.write("");
            out.close();
        } catch (java.io.IOException e) {
            //do something if an IOException occurs.
        }
    }
    public void deleteCat_fromPurchaseLog(String categoryName)
    {
        ArrayList<ListObject> purchaseLog = readInPurchaseLog("purchaseLog.txt", false);
        int index = 0;

        while (index < purchaseLog.size())
        {
            if(purchaseLog.get(index).getCategory().equals(categoryName))
                purchaseLog.remove(index);
            else
                index++;
        }
        writeArraysBackToFile(purchaseLog,"purchaseLog.txt");
    }
}