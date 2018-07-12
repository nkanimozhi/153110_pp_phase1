package com.cg.mypaymentapp.repo;

import java.util.HashMap;
import java.util.Map;

import com.cg.mypaymentapp.beans.*;
import com.cg.mypaymentapp.exception.InvalidInputException;

public class WalletRepoImpl implements WalletRepo{

	private Map<String, Customer> data; 
	public WalletRepoImpl(Map<String, Customer> data) 
	{
		super();
		this.data = data;
	}

	public boolean save(Customer customer) 
	{
		if(data.containsKey(customer.getMobileNo()))
		{
			data.replace(customer.getMobileNo(), customer);
		}
		else
			data.put(customer.getMobileNo(), customer);
	
		return true;
		
	}

	public Customer findOne(String mobileNo) 
	{
		Customer customer=null;
		
			
						
		customer=data.get(mobileNo);
		return customer;
	}
}
