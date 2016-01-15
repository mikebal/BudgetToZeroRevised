package thesolocoder.solo.budgettozerorevised;



import android.app.Activity;
import android.content.Context;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;


/**
 * Created by Michael on 2015-05-04.
 */
public class DateHandler extends Activity {

    private static final int DAY = 0;
    private static final int MAX_DAY = 1;
    private static final int MONTH = 2;
    private static final int YEAR = 3;

    private int[] getDateToday() {
        int date[] = new int[4];
        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        date[0] = calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        date[1] = calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH);
        date[2] = calendar.getInstance().get(Calendar.MONTH);
        date[3] = calendar.getInstance().get(Calendar.YEAR);

        return date;
    }

    public void setAutoResetDAYLIE(Context context)
    {
        int date[] = getDateToday();
        date[DAY] += 1;
        if(date[DAY] > date[MAX_DAY])
        {
            date[DAY] -= date[MAX_DAY];
            if(date[MONTH] == 12) {
                date[MONTH] = 1;
                date[YEAR]++;
            }
            else
                date[MONTH]++;
        }
        saveResetDate(context,date[DAY], date[MONTH], date[YEAR]);
    }

    public void setAutoResetWEEK(Context context)
    {
        int date[] = getDateToday();
        date[DAY] += 7;
        if(date[DAY] > date[MAX_DAY])
        {
            date[DAY] -= date[MAX_DAY];
            if(date[MONTH] == 12) {
                date[MONTH] = 1;
                date[YEAR]++;
            }
            else
                date[MONTH]++;
        }
        saveResetDate(context,date[DAY], date[MONTH], date[YEAR]);
    }

    public void setAutoResetTWOWEEK(Context context)
    {
        int date[] = getDateToday();
        date[DAY] += 14;
        if(date[DAY] > date[MAX_DAY])
        {
            date[DAY] -= date[MAX_DAY];
            if(date[MONTH] == 12) {
                date[MONTH] = 1;
                date[YEAR]++;
            }
            else
                date[MONTH]++;
        }
        saveResetDate(context,date[DAY], date[MONTH], date[YEAR]);
    }

    public void setAutoResetMonth(Context context)
    {
        int date[] = getDateToday();

        if(date[MONTH] != 12)
            date[MONTH]++;
        else{
            date[MONTH] = 1;
            date[YEAR]++;
        }
        saveResetDate(context,date[DAY], date[MONTH], date[YEAR]);
    }
    public void setAutoResetOFF(Context context)
    {
        saveResetDate(context,30,12,9999999);
    }


    private void saveResetDate(Context context, int day, int month, int year)
    {

        String date = String.valueOf(day)
                + "\n"
                + String.valueOf(month)
                + "\n"
                + String.valueOf(year) +"\n";


        try{
            OutputStreamWriter out = new OutputStreamWriter(context.openFileOutput("resetDate.txt", 0));

            out.write(date);
            out.close();
        } catch (java.io.IOException e) {
            //do something if an IOException occurs.
        }
    }

    public boolean isResetRequired(Context context) {

        Calendar calendar = Calendar.getInstance();

        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        boolean reset = false;

        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(context.openFileInput("resetDate.txt")));


            int resetDay = Integer.valueOf(inputReader.readLine());
            int resetMonth = Integer.valueOf(inputReader.readLine());
            int resetYear = Integer.valueOf(inputReader.readLine());
            inputReader.close();

            if (year > resetYear)
                reset = true;
            else if (year == resetYear) {
                if (month > resetMonth)
                    reset = true;
                else if (month == resetMonth && day >= resetDay)
                    reset = true;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return reset;
    }

    private int[] getResetDate(Context context)
    {
        int date[] = new int[4];
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(context.openFileInput("resetDate.txt")));

            date[DAY] = Integer.valueOf(inputReader.readLine());
            date[MONTH] = Integer.valueOf(inputReader.readLine());
            date[YEAR] = Integer.valueOf(inputReader.readLine());
            inputReader.close();

        } catch (IOException e) {
            e.printStackTrace();

            date[DAY] = 1;
            date[MONTH] = 1;
            date[YEAR] = 9999999;

            return date;
        }
        return date;
    }

  /* public int daysUntilReset(Context context)
   {
       int date[] = getDateToday();
       int resetDate[] = getResetDate(context);

       Calendar date1 = Calendar.getInstance();
       Calendar date2 = Calendar.getInstance();

       date1.clear();
       date1.set(date[DAY], date[MONTH], date[YEAR]);
       date2.clear();
       date2.set(resetDate[DAY], resetDate[MONTH], resetDate[YEAR]);

       long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

       float dayCount = (float) diff / (24 * 60 * 60 * 1000);

       if(dayCount != 30)
           dayCount = dayCount/366;

       //dayCount++;



       return ((int) dayCount);
   }*/

    public int badDateCounter(Context context)
    {
        int date[] = getDateToday();
        int resetDate[] = getResetDate(context);
        int difference = 0;
        boolean enteredLoop = false;

        while (date[DAY] != resetDate[DAY]){
            difference++;
            date[DAY]++;
            if(date[DAY] > date[MAX_DAY])
            {
                date[DAY] = 1;
                if(date[MONTH] != 12)
                    date[MONTH]++;
                else
                    date[MONTH] = 1;

            }
            enteredLoop = true;
        }
        if(!enteredLoop)
            if(date[MONTH] != resetDate[MONTH])
                difference=date[MAX_DAY];
        return difference;
    }


}
