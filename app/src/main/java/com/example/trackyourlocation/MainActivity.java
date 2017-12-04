package com.example.trackyourlocation;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceException;
import com.microsoft.windowsazure.mobileservices.http.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.http.OkHttpClientFactory;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.sync.MobileServiceSyncContext;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.ColumnDataType;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.MobileServiceLocalStoreException;
import com.microsoft.windowsazure.mobileservices.table.sync.localstore.SQLiteLocalStore;
import com.microsoft.windowsazure.mobileservices.table.sync.synchandler.SimpleSyncHandler;
import com.squareup.okhttp.OkHttpClient;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";

    /**
     * Mobile Service Client reference
     */
    private MobileServiceClient mClient;

    /**
     * Mobile Service Table used to access data
     */
    private MobileServiceTable<Locations> mLocationsTable;
    private MobileServiceTable<FavLocation> mFavLocationsTable;

    //Offline Sync
    /**
     * Mobile Service Table used to access and Sync data
     */
    //private MobileServiceSyncTable<Locations> mLocationsTable;

    /**
     * Adapter to sync the items list with the view
     */
    private LocationAdapter mAdapter;

    /**
     * EditText containing the "New To Do" text
     */

    /**
     * Progress spinner to use for table operations
     */
    private ProgressBar mProgressBar;
    private TextView mFavLocations;

    /**
     * Initializes the activity
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        mProgressBar = (ProgressBar) findViewById(R.id.loadingProgressBar);

        //Testing
        mFavLocations=(TextView)findViewById(R.id.favLocation) ;

        // Initialize the progress bar
        mProgressBar.setVisibility(ProgressBar.GONE);

        try {
            // Create the Mobile Service Client instance, using the provided

            // Mobile Service URL and key
//            mClient = new MobileServiceClient(
//                    "https://trackyourlocation.azurewebsites.net",
//                    this).withFilter(new ProgressFilter());
            mClient = new MobileServiceClient(
                    "https://trackyourlocation.azurewebsites.net",
                    this).withFilter(new ServiceFilter() {
                @Override
                public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilter) {
                    // Get the request contents
                    String url = request.getUrl();
                    String content = request.getContent();

                    if (url != null) {
                        Log.d("Request URL:", url);
                        Log.d(TAG, "handleRequest: req "+request.toString());
                    }

                    if (content != null) {
                        Log.d("Request Content:", content);
                    }else{
                        Log.d(TAG, "handleRequest: content = null");
                    }

                    // Execute the next service filter in the chain
                    ListenableFuture<ServiceFilterResponse> responseFuture = nextServiceFilter.onNext(request);

                    Futures.addCallback(responseFuture, new FutureCallback<ServiceFilterResponse>() {
                        @Override
                        public void onFailure(Throwable exception) {
                            Log.d("Exception:", exception.getMessage());
                        }

                        @Override
                        public void onSuccess(ServiceFilterResponse response) {
                            if (response != null && response.getContent() != null) {
                                Log.d("Response Content:", response.getContent());
                            }
                        }
                    });

                    return responseFuture;
                }
            });

            // Extend timeout from default of 10s to 20s
            mClient.setAndroidHttpClientFactory(new OkHttpClientFactory() {
                @Override
                public OkHttpClient createOkHttpClient() {
                    OkHttpClient client = new OkHttpClient();
                    client.setReadTimeout(20, TimeUnit.SECONDS);
                    client.setWriteTimeout(20, TimeUnit.SECONDS);
                    return client;
                }
            });

            // Get the Mobile Service Table instance to use

            mLocationsTable = mClient.getTable(Locations.class);
            mFavLocationsTable = mClient.getTable(FavLocation.class);

            // Offline Sync
            //mLocationsTable = mClient.getSyncTable("Locations", Locations.class);

            //Init local storage
            initLocalStore().get();



            // Create an adapter to bind the items with the view
            mAdapter = new LocationAdapter(this, R.layout.row_list_to_do);
            ListView listViewToDo = (ListView) findViewById(R.id.listViewToDo);
            listViewToDo.setAdapter(mAdapter);

            // Load the items from the Mobile Service
            refreshLocationsFromTable();

        } catch (MalformedURLException e) {
            createAndShowDialog(new Exception("There was an error creating the Mobile Service. Verify the URL"), "Errorwith url");
        } catch (Exception e){
            createAndShowDialog(e, "Error on onCreate");
        }
    }

    /**
     * Initializes the activity menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    /**
     * Select an option from the menu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_refresh) {
            refreshLocationsFromTable();
        }

        return true;
    }


    /**
     * Add a new item
     *
     * @param view
     *            The view that originated the call
     */
    public void addLocations(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final Locations item = new Locations();

        //TODO get Current location
        LocationManipulating locationManipulating=new LocationManipulating(this);

        LocationObject locationObject=locationManipulating.getLocation();
        //set item attributes -->then send to a func to have an ID
        item.setAltitude(locationObject.getAltitude());
        item.setLongitude(locationObject.getLongitude());
        item.setLatitude(locationObject.getLatitude());



        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final Locations entity = addLocationInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.add(entity);
                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

        Log.d(TAG, "addLocation: ends");
    }

    public void addFavLocation(View view) {
        if (mClient == null) {
            return;
        }

        // Create a new item
        final FavLocation item = new FavLocation();

        //TODO get Current location
        LocationManipulating locationManipulating=new LocationManipulating(this);

        LocationObject locationObject=locationManipulating.getLocation();
        //set item attributes -->then send to a func to have an ID

        item.setLongitude(locationObject.getLongitude());
        item.setLatitude(locationObject.getLatitude());



        // Insert the new item
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    final FavLocation entity = addFavLocationInTable(item);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           // mAdapter.add(entity);
                            Log.d(TAG, "FavLocations insert ");
                            mFavLocations.append(entity.toString()+"\n------------------------------------");

                        }
                    });
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };

        runAsyncTask(task);

        Log.d(TAG, "addFavLocation: ends");
    }

    /**
     * Add an item to the Mobile Service Table
     *
     * @param item
     *            The item to Add
     */
    public Locations addLocationInTable(Locations item) throws ExecutionException, InterruptedException {
        Log.d(TAG, "addLocationInTable: starts");
        Locations entity = mLocationsTable.insert(item).get();
        return entity;
    }

    public FavLocation addFavLocationInTable(FavLocation item) throws ExecutionException, InterruptedException {
        Log.d(TAG, "addLocationInTable: starts");
        FavLocation entity = mFavLocationsTable.insert(item).get();
        return entity;
    }

    /**
     * Refresh the list with the items in the Table
     */
    private void refreshLocationsFromTable() {

        // Get the items and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<Locations> results = refreshLocationsFromMobileServiceTable();

                    //Offline Sync
                    //final List<Locations> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.clear();

                            for (Locations item : results) {
                                mAdapter.add(item);
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error refreshing items ");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }



    private void refreshFavLocationsFromTable() {

        // Get the items and add them in the
        // adapter

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {

                try {
                    final List<FavLocation> results = refreshFavsFromMobileServiceTable();

                    //Offline Sync
                    //final List<Locations> results = refreshItemsFromMobileServiceTableSyncTable();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                           mFavLocations.setText(" ");

                            for (FavLocation item : results) {
                               mFavLocations.append(item.toString()+"\n------------------------------");
                            }
                        }
                    });
                } catch (final Exception e){
                    createAndShowDialogFromTask(e, "Error refreshing items ");
                }

                return null;
            }
        };

        runAsyncTask(task);
    }

    /**
     * Refresh the list with the items in the Mobile Service Table
     */

    private List<Locations> refreshLocationsFromMobileServiceTable() throws ExecutionException, InterruptedException ,MobileServiceException{
        Log.d(TAG, "refreshLocationsFromMobileServiceTable: starts");
        return mLocationsTable.execute().get();
    }

    private List<FavLocation> refreshFavsFromMobileServiceTable() throws ExecutionException, InterruptedException ,MobileServiceException{
        Log.d(TAG, "refreshLocationsFromMobileServiceTable: starts");
        return mFavLocationsTable.execute().get();
    }

    //Offline Sync
    /**
     * Refresh the list with the items in the Mobile Service Sync Table
     */
    /*private List<Locations> refreshItemsFromMobileServiceTableSyncTable() throws ExecutionException, InterruptedException {
        //sync the data
        sync().get();
        Query query = QueryOperations.field("complete").
                eq(val(false));
        return mLocationsTable.read(query).get();
    }*/

    /**
     * Initialize local storage
     * @return
     * @throws MobileServiceLocalStoreException
     * @throws ExecutionException
     * @throws InterruptedException
     */
    private AsyncTask<Void, Void, Void> initLocalStore() throws MobileServiceLocalStoreException, ExecutionException, InterruptedException {

        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {

                    MobileServiceSyncContext syncContext = mClient.getSyncContext();

                    if (syncContext.isInitialized())
                        return null;

                    SQLiteLocalStore localStore = new SQLiteLocalStore(mClient.getContext(), "OfflineStore", null, 1);

                    Map<String, ColumnDataType> tableDefinition = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("longitude", ColumnDataType.String);
                    tableDefinition.put("latitude", ColumnDataType.String);
                    tableDefinition.put("altitude", ColumnDataType.String);

                    localStore.defineTable("Locations", tableDefinition);


                    Map<String, ColumnDataType> tableDefinition2 = new HashMap<String, ColumnDataType>();
                    tableDefinition.put("id", ColumnDataType.String);
                    tableDefinition.put("longitude", ColumnDataType.String);
                    tableDefinition.put("latitude", ColumnDataType.String);


                    localStore.defineTable("FavLocation", tableDefinition);

                    SimpleSyncHandler handler = new SimpleSyncHandler();

                    syncContext.initialize(localStore, handler).get();

                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error iniating localstore");
                }

                return null;
            }
        };

        return runAsyncTask(task);
    }

    //Offline Sync
    /**
     * Sync the current context and the Mobile Service Sync Table
     * @return
     */
    /*
    private AsyncTask<Void, Void, Void> sync() {
        AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    MobileServiceSyncContext syncContext = mClient.getSyncContext();
                    syncContext.push().get();
                    mLocationsTable.pull(null).get();
                } catch (final Exception e) {
                    createAndShowDialogFromTask(e, "Error");
                }
                return null;
            }
        };
        return runAsyncTask(task);
    }
    */

    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialogFromTask(final Exception exception, final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(exception, title);
                Log.d(TAG, "createAndShowDialog with title "+title+" and ex "+exception.getMessage());
            }
        });
    }


    /**
     * Creates a dialog and shows it
     *
     * @param exception
     *            The exception to show in the dialog
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(Exception exception, String title) {
        Throwable ex = exception;
        if(exception.getCause() != null){
            ex = exception.getCause();
        }
        createAndShowDialog(ex.getMessage(), title);
    }

    /**
     * Creates a dialog and shows it
     *
     * @param message
     *            The dialog message
     * @param title
     *            The dialog title
     */
    private void createAndShowDialog(final String message, final String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }

    /**
     * Run an ASync task on the corresponding executor
     * @param task
     * @return
     */
    private AsyncTask<Void, Void, Void> runAsyncTask(AsyncTask<Void, Void, Void> task) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            return task.execute();
        }
    }

    private class ProgressFilter implements ServiceFilter {

        @Override
        public ListenableFuture<ServiceFilterResponse> handleRequest(ServiceFilterRequest request, NextServiceFilterCallback nextServiceFilterCallback) {

            final SettableFuture<ServiceFilterResponse> resultFuture = SettableFuture.create();


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.VISIBLE);
                }
            });

            ListenableFuture<ServiceFilterResponse> future = nextServiceFilterCallback.onNext(request);

            Futures.addCallback(future, new FutureCallback<ServiceFilterResponse>() {
                @Override
                public void onFailure(Throwable e) {
                    resultFuture.setException(e);
                }

                @Override
                public void onSuccess(ServiceFilterResponse response) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            if (mProgressBar != null) mProgressBar.setVisibility(ProgressBar.GONE);
                        }
                    });

                    resultFuture.set(response);
                }
            });

            return resultFuture;
        }
    }
}