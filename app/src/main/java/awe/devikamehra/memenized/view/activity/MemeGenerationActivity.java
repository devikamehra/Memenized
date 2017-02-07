package awe.devikamehra.memenized.view.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;

import awe.devikamehra.memenized.R;
import awe.devikamehra.memenized.rest.model.Meme;
import awe.devikamehra.memenized.view.fragment.FontSelectionFragment;
import awe.devikamehra.memenized.view.fragment.MemeImageSelectionFragment;
import awe.devikamehra.memenized.view.fragment.SetTextFragment;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class MemeGenerationActivity extends AppCompatActivity
        implements MemeImageSelectionFragment.OnFragmentInteractionListener,
                   FontSelectionFragment.OnFragmentInteractionListener,
                   SetTextFragment.OnFragmentInteractionListener {

    @InjectView(R.id.main_layout)
    CoordinatorLayout coordinatorLayout;

    @InjectView(R.id.toolbar)
    Toolbar toolbar;

    @InjectView(R.id.collapsing_bar)
    CollapsingToolbarLayout collapsingToolbarLayout;

    @InjectView(R.id.meme_display_image_view)
    ImageView mMemeImageView;

    AHBottomNavigation bottomBar;

    static Meme meme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meme_generation);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ButterKnife.inject(this);

        bottomBar = (AHBottomNavigation) findViewById(R.id.bottom_bar);

        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        setTitle(getString(R.string.title));

        meme = new Meme(getString(R.string.default_link_data));
        AHBottomNavigationItem item1 =  new AHBottomNavigationItem(getResources().getString(R.string.tab_1),
                                        new IconDrawable(this, MaterialIcons.md_photo)
                                            .colorRes(R.color.white)
                                            .actionBarSize(),
                                        getResources().getColor(R.color.tab1));
        AHBottomNavigationItem item2 =  new AHBottomNavigationItem(getResources().getString(R.string.tab_2),
                                        new IconDrawable(this, MaterialIcons.md_text_format)
                                                .colorRes(R.color.white)
                                                .actionBarSize(),
                                        getResources().getColor(R.color.tab2));
        AHBottomNavigationItem item3 =  new AHBottomNavigationItem(getResources().getString(R.string.tab_3),
                                        new IconDrawable(this, MaterialIcons.md_format_quote)
                                                .colorRes(R.color.white)
                                                .actionBarSize(),
                                        getResources().getColor(R.color.tab3));

        // Add items
        bottomBar.addItem(item1);
        bottomBar.addItem(item2);
        bottomBar.addItem(item3);

        // Set background color
        bottomBar.setDefaultBackgroundColor(getResources().getColor(R.color.colorPrimary));

        // Disable the translation inside the CoordinatorLayout
        bottomBar.setBehaviorTranslationEnabled(false);

        // Change colors
        bottomBar.setAccentColor(getResources().getColor(R.color.white));
        bottomBar.setInactiveColor(getResources().getColor(R.color.white));

        // Manage titles
        Fragment fragment1 = MemeImageSelectionFragment.newInstance(meme);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, fragment1);
        transaction.commit();
        bottomBar.setTitleState(AHBottomNavigation.TitleState.SHOW_WHEN_ACTIVE);
        bottomBar.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                Fragment fragment;
                switch (position){
                    case 0:  fragment = MemeImageSelectionFragment.newInstance(meme);
                        break;
                    case 1: fragment = FontSelectionFragment.newInstance(meme);
                        break;
                    case 2: fragment = SetTextFragment.newInstance(meme);
                        break;
                    default: fragment = MemeImageSelectionFragment.newInstance(meme);
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, fragment);
                transaction.commit();
                return true;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onFragmentInteraction(Meme meme) {

    }

}
