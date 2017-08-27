package uk.co.sloshyd.popularmovies.sync;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;

/**
 * Created by Darren on 23/08/2017.
 */

public class LoadDataService extends IntentService {

    public LoadDataService(){
        super("LoadDataService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Tasks.executeTask(this, intent);
    }
}
