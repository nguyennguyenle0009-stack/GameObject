package game.entity;

import java.util.ArrayList;
import java.util.List;

import game.object.SuperObject;

public class Inventory {
    private final List<SuperObject> items = new ArrayList<>();
    private final SuperObject[] weaponSlots = new SuperObject[2];

    public void addItem(SuperObject item) {
        items.add(item);
    }

    public boolean removeItem(SuperObject item) {
        return items.remove(item);
    }

    public List<SuperObject> getItems() {
        return items;
    }

    // weapon management
    public boolean equipWeapon(int slot, SuperObject weapon) {
        if (slot < 0 || slot >= weaponSlots.length) {
            return false;
        }
        weaponSlots[slot] = weapon;
        return true;
    }

    public SuperObject getWeapon(int slot) {
        if (slot < 0 || slot >= weaponSlots.length) {
            return null;
        }
        return weaponSlots[slot];
    }

    public SuperObject unequipWeapon(int slot) {
        if (slot < 0 || slot >= weaponSlots.length) {
            return null;
        }
        SuperObject removed = weaponSlots[slot];
        weaponSlots[slot] = null;
        return removed;
    }

    public SuperObject[] getWeaponSlots() {
        return weaponSlots;
    }
}



























