/**
 *   textManager, a GUI for managing bills for texter jobs
 *
 *   Copyright (C) 2013 philnate
 *
 *   This file is part of textManager.
 *
 *   textManager is free software: you can redistribute it and/or modify it under the terms of the
 *   GNU General Public License as published by the Free Software Foundation, either version 3 of the
 *   License, or (at your option) any later version.
 *
 *   textManager is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *   without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 *   See the GNU General Public License for more details. You should have received a copy of the GNU
 *   General Public License along with textManager. If not, see <http://www.gnu.org/licenses/>.
 */
package me.philnate.textmanager.windows.components;

import static me.philnate.textmanager.utils.DB.ds;

import javax.swing.JComboBox;

import me.philnate.textmanager.entities.Customer;


public class CustomerComboBox extends JComboBox<Customer> {

    private static final long serialVersionUID = 4651289340112419072L;

    /**
     * loads all customers currently stored in collection and shows their
     * heading in Combobox
     */
    public void loadCustomer() {
	removeAllItems();
	for (Customer cust : ds.find(Customer.class)
		.order("companyName, contactName").asList()) {
	    addItem(cust);
	}
    }

    public Customer getSelectedCustomer() {
	return (Customer) getSelectedItem();
    }
}
