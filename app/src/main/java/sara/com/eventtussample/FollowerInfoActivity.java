package sara.com.eventtussample;

import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;


import de.hdodenhof.circleimageview.CircleImageView;
import it.carlom.stikkyheader.core.StikkyHeaderBuilder;
import sara.com.Adapters.TweetsAdapter;
import sara.com.DBModels.TweetDB;
import sara.com.Models.User;
import sara.com.Utility.StaticAssets;
import sara.com.Utility.StickyAnimator;
import sara.com.Utility.TwitterHelper;
import sara.com.Utility.UtilityMethod;

public class FollowerInfoActivity extends AppCompatActivity {

    private ImageView img_bg;
    private CircleImageView img_user;
    private ListView lstV_tweets;
    private ProgressBar progress_download;
    private SwipeRefreshLayout swipe_refresh;
    private String[] tweets;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follower_info);

        // setup actionbar
        UtilityMethod.SetupActionBar(this);
        SetupTools();

        // check internet
        if (UtilityMethod.HaveNetworkConnection(this)) {
            new GetTweetClass().execute();
        } else
            SetupTweetList();
    }

    private void SetupTools() {

        // get user info
        if (getIntent().getSerializableExtra("user") != null) {
            user = (User) getIntent().getSerializableExtra("user");
        }

        img_user = (CircleImageView) findViewById(R.id.img_user);
        img_bg = (ImageView) findViewById(R.id.header_image);
        lstV_tweets = (ListView) findViewById(R.id.lstV_user_tweets);
        progress_download = (ProgressBar) findViewById(R.id.custom_progress_tweets);

        // load user img
        if (user != null) {
            UrlImageViewHelper.setUrlDrawable
                    (img_user, user.getProfile_img(),
                            R.drawable.user_icon, StaticAssets.CacheTime);

            // load background img
            if (user.getBg_img() != null && user.getBg_img().length() > 0)
                UrlImageViewHelper.setUrlDrawable
                        (img_bg, user.getBg_img(),
                                R.drawable.background_default, StaticAssets.CacheTime);
        }

        SetupStickyHeader();
    }

    private void SetupStickyHeader() {
        StikkyHeaderBuilder.stickTo(lstV_tweets)
                .setHeader(R.id.header, (ViewGroup) findViewById(R.id.container))
                .minHeightHeaderDim(R.dimen.min_height)
                .animator(new StickyAnimator())
                .build();
    }

    private void SetupTweetList() {

        progress_download.setVisibility(View.GONE);
        tweets = TweetDB.GetTweets(this, user.getUserId());
        lstV_tweets.setAdapter
                (new TweetsAdapter(FollowerInfoActivity.this,
                        R.layout.adapter_tweet, tweets));
    }

    public class GetTweetClass extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            return TwitterHelper.getLastTweet(FollowerInfoActivity.this,
                    user.getUserId());

        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            progress_download.setVisibility(View.GONE);
            if (!result)
                // user close permission to get his tweets
                UtilityMethod.SetToast(FollowerInfoActivity.this, getString(R.string.no_access)
                        + " " + user.getUser_name());
            else
                SetupTweetList();

        }
    }

}
