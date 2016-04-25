package model;

import android.bluetooth.BluetoothDevice;

/**
 * Created by genbob on 18.04.16.
 */
public class ListItem {

    private final String mItemKey;
    private final String mItemName;

    public ListItem(String itemName, String itemKey){
        this.mItemName = itemName;
        this.mItemKey = itemKey;
    }

    public String getmItemKey() {
        return mItemKey;
    }

    public String getmItemName() {
        return mItemName;
    }

    @Override
    public String toString(){
        return this.getmItemName();
    }
}
