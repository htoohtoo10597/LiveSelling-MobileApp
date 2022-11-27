package iss.workshop.livestreamapp.helpers;

import java.util.Comparator;

import iss.workshop.livestreamapp.models.User;

public class UserSortByName implements Comparator<User> {
    @Override
    public int compare(User o1, User o2) {
        return o1.getFirstName().toLowerCase().compareTo(o2.getFirstName().toLowerCase());
    }
}
