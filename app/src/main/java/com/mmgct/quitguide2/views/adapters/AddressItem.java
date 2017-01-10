package com.mmgct.quitguide2.views.adapters;

import android.location.Address;

/**
 * Created by 35527 on 2/12/2016.
 */
public class AddressItem {

        private Address address;
        private String addressString;

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public String getAddressString() {
            return addressString;
        }

        public void setAddressString(String addressString) {
            this.addressString = addressString;
        }

        @Override
        public String toString() {
            return addressString;

    }
}
