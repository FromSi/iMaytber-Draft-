package kz.imaytber.sgq.imaytber.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import kz.imaytber.sgq.imaytber.R;
import kz.imaytber.sgq.imaytber.fragments.ScreenSlideWelcomeFragment;

/**
 * Created by fromsi on 17.01.18.
 */

public class ScreenSlideWelcomeAdapter extends FragmentStatePagerAdapter {
    private Context context;
    public ScreenSlideWelcomeAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        ScreenSlideWelcomeFragment screenSlidePageFragment = new ScreenSlideWelcomeFragment();
        switch (position) {
            case 0:
                screenSlidePageFragment.init(context.getString(R.string.screen_one), false);
                break;
            case 1:
                screenSlidePageFragment.init(context.getString(R.string.screen_two), false);
                break;
            case 2:
                screenSlidePageFragment.init(context.getString(R.string.screen_tre), true);
                break;
            default:
                break;
        }
        return screenSlidePageFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}