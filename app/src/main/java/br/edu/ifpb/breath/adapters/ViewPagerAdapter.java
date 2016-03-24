package br.edu.ifpb.breath.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.View;

import java.util.HashMap;

import br.edu.ifpb.breath.fragments.AboutDialog;
import br.edu.ifpb.breath.fragments.MonitorFragment;

/**
 * This class represents the ViewPagerAdapter.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    private CharSequence mTitles[];
    private HashMap<Integer, Fragment> mPageReferenceMap = new HashMap<Integer, Fragment>();

    /**
     * Constructor method.
     * @param fm - Fragment manager.
     * @param titles - Fragments titles.
     */
    public ViewPagerAdapter(FragmentManager fm, CharSequence titles[]) {
        super(fm);

        this.mTitles = titles;
    }

    /**
     * This method return the fragment for the every position in the View Pager
     *
     * @param position - Position of fragment.
     * @return - Fragment at position.
     */
    @Override
    public Fragment getItem(int position) {
        Fragment result;
        switch(position){
            default:
            case 0:
                result = new MonitorFragment(); break;
        }
        mPageReferenceMap.put(position, result);
        return result;
    }

    /**
     * Returns all instanced fragments.
     */
    public HashMap<Integer, Fragment> getExistentFragments() {
        return mPageReferenceMap;
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        super.destroyItem(container, position, object);
        mPageReferenceMap.remove(position);
    }

    /**
     * This method return the titles for the Tabs in the Tab Strip
     *
     * @param position - Position to get the title.
     * @return - Page title.
     */
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }

    /**
     * This method return the Number of mTabsLayout for the mTabsLayout Strip
     *
     * @return - Fragments count.
     */
    @Override
    public int getCount() {
        return mTitles.length;
    }
}
