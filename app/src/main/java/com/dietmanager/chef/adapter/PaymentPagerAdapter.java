package com.dietmanager.chef.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.dietmanager.chef.fragment.AddWalletAmountFragment;
import com.dietmanager.chef.fragment.TransactionFragment;


public class PaymentPagerAdapter extends FragmentPagerAdapter {

    private String[] title = new String[]{"Request Wallet Money", "Transactions"};

    public PaymentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AddWalletAmountFragment();
            case 1:
                return new TransactionFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }
}
