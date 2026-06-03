package view;

import model.Order;

public interface StatusViewListener {
    void onOrderStatusChanged(String currentStatus, String queueDetails);
}