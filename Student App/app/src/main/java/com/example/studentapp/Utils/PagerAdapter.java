package com.example.studentapp.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.studentapp.Fragments.QueueFragment;
import com.example.studentapp.Models.Queue;

import java.util.ArrayList;
import java.util.Collection;

public class PagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Fragment> fragments;

    public PagerAdapter(FragmentManager fm, Collection<Queue> queues) {
        super(fm);
        this.fragments = new ArrayList<>();
        for (Queue q : queues) {
            fragments.add(new QueueFragment());
        }
    }

    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
