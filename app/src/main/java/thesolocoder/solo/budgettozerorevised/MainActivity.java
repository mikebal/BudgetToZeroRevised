package thesolocoder.solo.budgettozerorevised;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button makePurchase;
    TextView balance;
    Button setBudgetHeader;
    TextView budgetRuntime;
    ListView log;
    ArrayList<ListObject> logFiles = new ArrayList<ListObject>();
    float x1;
    PopupWindow popupwindow_obj;
    int logPosition;
    private ArrayAdapter<String> mAdapter;
    private ListView mDrawerList;
   // private ListView mDrawerList;
   // private ArrayAdapter<String> mAdapter;
   // private ActionBarDrawerToggle mDrawerToggle;
   // private DrawerLayout mDrawerLayout;
    String[] categoryArray;
    String selectedCategory = "purchaseLog";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        setUpVariables();
        handleFirstStart();
        loadSettings();
        registerForContextMenu(log);
        loadBalanceAndReadLog();
      //  addDrawerItems();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

       /* if (id == R.id.nav_settings) {
            Intent openSettings = new Intent(MainActivity.this, Settings.class);
            startActivity(openSettings);

        } else if (id == R.id.nav_categorymanager) {
            Intent openCategoryManager = new Intent(MainActivity.this, CategoryManagerView.class);
            startActivity(openCategoryManager);
        }*/



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //********************************************************************************************************

    private void handleFirstStart(){
        SharedPreferences savedData;
        savedData = getSharedPreferences("savedData", 0);// save data

        if((savedData.getString("total_budget", "None").equals("None"))) {
            Intent openGetFirstStartBudget = new Intent(MainActivity.this, FirstStart.class);
            startActivity(openGetFirstStartBudget);
        }
        else if(savedData.getBoolean("NewFileFormatNeeded", true))
        {
            PurchaseLogManager reWriter = new PurchaseLogManager(getApplicationContext());
            reWriter.changeFileFormat();
            SharedPreferences.Editor editor = savedData.edit();
            editor.putBoolean("NewFileFormatNeeded", false);
            editor.apply();
        }
    }
    private void loadSettings(){
        SharedPreferences savedData;
        savedData = getSharedPreferences("savedData", 0);

        if(savedData.getBoolean("show_history_on", true)){
            log.setVisibility(View.VISIBLE);
            readInPurchaseLog("purchaseLog");
        }
        else
            log.setVisibility(View.GONE);

        if(savedData.getInt("reset_period", 0) == 0)
            budgetRuntime.setVisibility(View.GONE);
        else
            budgetRuntime.setVisibility(View.VISIBLE);
    }

    void setUpVariables(){
        makePurchase = (Button) findViewById(R.id.bSpend);
        makePurchase.setOnClickListener(handleMakePurchae);
        balance = (TextView) findViewById(R.id.tvRemainingBudget);
        setBudgetHeader = (Button) findViewById(R.id.tvSmallUpperSuper);
        setBudgetHeader.setOnClickListener(HandleFlipBudgetDirection);
        setBudgetHeader.setBackgroundColor(Color.TRANSPARENT);
        budgetRuntime = (TextView) findViewById(R.id.tvBudgetRuntime);
        log = (ListView) findViewById(R.id.List);
        log.setOnTouchListener(handleSwipe);
        popupwindow_obj = popupDisplay();
       // initialize in onCreate()
      //  mDrawerList = (ListView)findViewById(R.id.upperList);
      //  mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenu.ContextMenuInfo menuInfo) {

        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo) menuInfo;

        logPosition = info.position;

        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, r.getDisplayMetrics());
        popupwindow_obj.setHeight((int) px);
        popupwindow_obj.showAsDropDown(log.getChildAt(logPosition - log.getFirstVisiblePosition()), (int) x1, -20);

    }

    public PopupWindow popupDisplay() { // disply designing your popoup window
        final PopupWindow popupWindow = new PopupWindow(this); // inflet your layout or diynamic add view

        View view;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.menulayout, null);

        Button item = (Button) view.findViewById(R.id.menuButtonEdit);
        item.setOnClickListener(menuClickEdit);
        Button item2 = (Button) view.findViewById(R.id.menuButtonDelete);
        item2.setOnClickListener(menuClickDelete);
        Button item3 = (Button) view.findViewById(R.id.menuButtonRefund);
        item3.setOnClickListener(menuClickRefund);

        popupWindow.setFocusable(true);
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

    View.OnClickListener menuClickEdit = new View.OnClickListener() {
        public void onClick(View v) {
            SharedPreferences savedData;
            savedData = getSharedPreferences("savedData", 0);// save data
            SharedPreferences.Editor editor = savedData.edit();

            editor.putString("toEdit_date", logFiles.get(logPosition).getDate());
            editor.putString("toEdit_disc", logFiles.get(logPosition).getDiscription());
            editor.putString("toEdit_cost", logFiles.get(logPosition).getCost());
            editor.putString("toEdit_Category", logFiles.get(logPosition).getCategory());
            editor.putString("toEdit_SourceCat", selectedCategory);
            editor.putInt("edit_position", logPosition);
            editor.apply();

            Intent openMainActivity = new Intent(MainActivity.this, Editor.class);
            startActivity(openMainActivity);
            popupwindow_obj.dismiss();
        }
    };
    View.OnClickListener menuClickDelete = new View.OnClickListener() {
        public void onClick(View v) {
            menuConfirmation("Delete", "Are you sure you want to continue?", true);
        }
    };
    View.OnClickListener menuClickRefund = new View.OnClickListener() {
        public void onClick(View v) {
            menuConfirmation("Refund", "Are you sure you want to continue?", false);
        }
    };

    private void loadBalance() {
        String balanceInfo[];
        BalanceManager balanceHandler = new BalanceManager();

        balanceInfo = balanceHandler.getBalanceInfo(getApplicationContext());

        balance.setText(balanceInfo[0]);
        setBudgetHeader.setText(balanceInfo[1]);
        if(balanceInfo[2].equals("RED"))
            makePurchase.setTextColor(Color.RED);
        else
            makePurchase.setTextColor(Color.BLACK);
    }

    View.OnClickListener HandleFlipBudgetDirection = new View.OnClickListener() {
        public void onClick(View v) {
            BalanceManager budgetHandler = new BalanceManager();
            budgetHandler.flipBudget(getApplicationContext());
            loadBalance();
        }
    };

    View.OnClickListener handleMakePurchae = new View.OnClickListener() {
        public void onClick(View v) {
            Intent openMainActivity = new Intent(MainActivity.this, Purchase.class);
            startActivity(openMainActivity);
        }
    };

    View.OnTouchListener handleSwipe = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {

            // TODO Auto-generated method stub
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();// - rawX;
                    return false;
            }
            return false;
        }
    };

    public void onResume(){
        super.onResume();

        BudgetResetManager budgetResetmanger = new BudgetResetManager();
        if(budgetResetmanger.budgetResetHandler(getApplicationContext()))
            budgetResetmanger.newBudgetAutoResetDateHandler(true,getApplicationContext());
        else
            budgetResetmanger.newBudgetAutoResetDateHandler(false,getApplicationContext());  // Chrck if settings had been changed to new AutoReset period

        loadSettings();
        updateResetCountdown();
        loadBalanceAndReadLog();
        popupwindow_obj.dismiss();
        addDrawerItems();
       // addDrawerItems();
    }
    private void loadBalanceAndReadLog(){
        loadBalance();

        readInPurchaseLog(selectedCategory);
    }

    public void readInPurchaseLog(String fileName)
    {
        PurchaseLogManager logHandler = new PurchaseLogManager(getApplicationContext());
        logFiles = logHandler.readInPurchaseLog(fileName + ".txt", false);
        customAdapter customAdapter = new customAdapter(this, logFiles);
        log.setAdapter(customAdapter);
        customAdapter.notifyDataSetChanged();
    }

    private void updateResetCountdown()
    {
        RuntimeManager resetCountdown = new RuntimeManager();
        String result = resetCountdown.getAutoResetRemaning(getApplicationContext());
        budgetRuntime.setText(result);
    }

    private void menuConfirmation(String header, String message, final boolean ifYesSelected)
    {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(header);
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteOrRefundItem(ifYesSelected);
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        popupwindow_obj.dismiss();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void deleteOrRefundItem(boolean justDelete){
        EditManager editManager = new EditManager(getApplicationContext(), logFiles);
        log = null;
        editManager.deleteItem(logPosition, justDelete, selectedCategory);
        log = (ListView) findViewById(R.id.List);

        loadBalanceAndReadLog();
        popupwindow_obj.dismiss();
    }

  private void addDrawerItems(){

      NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
      Menu menu = navigationView.getMenu();


      CategoryManager categoryHandler = new CategoryManager(getApplicationContext());
      ArrayList<String> categorys = categoryHandler.readInCategories();

      SubMenu categories_menu = menu.addSubMenu(R.id.nav_header_group,R.id.menu_ALL, 0,"Categories");
      for(int i = 0; i < categorys.size(); i++)
        categories_menu.add(categorys.get(i));

      SubMenu advanced_menu = menu.addSubMenu(R.id.nav_header_group, R.id.menu_ALL, 0, "Advanced");

      advanced_menu.add("Category Manager");
      MenuItem advanced_item = advanced_menu.getItem(0);
      advanced_item.setIcon(R.mipmap.ic_folder_black_24dp);
      advanced_menu.add("Edit Budget");
      advanced_item = advanced_menu.getItem(1);
      advanced_item.setIcon(R.mipmap.ic_local_atm_black_24dp);
      advanced_menu.add("Settings");
      advanced_item = advanced_menu.getItem(2);
      advanced_item.setIcon(R.mipmap.ic_settings_black_24dp);

      //item.setVisible(true);
      //m.add

      //settings.setVisible(true);
   /*   CategoryManager categoryHandler = new CategoryManager(getApplicationContext());
      final ArrayList<String> categorys = categoryHandler.readInCategories();
      categorys.add(0,"All");
      categorys.remove(1); // Remove value "Default" from the list
      categorys.remove(1); // Remove "New Category" option

      categoryArray = new String[categorys.size()];
      for (int i = 0; i < categorys.size(); i++)
      {
          categoryArray[i] = categorys.get(i).toString();
      }
      mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, categoryArray);
      mDrawerList.setAdapter(mAdapter);*/
  }


}
