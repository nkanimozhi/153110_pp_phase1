
package com.cg.mypaymentapp.service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


import com.cg.mypaymentapp.beans.Customer;
import com.cg.mypaymentapp.beans.Wallet;
import com.cg.mypaymentapp.exception.InsufficientBalanceException;
import com.cg.mypaymentapp.exception.InvalidInputException;
import com.cg.mypaymentapp.repo.WalletRepo;
import com.cg.mypaymentapp.repo.WalletRepoImpl;


public class WalletServiceImpl implements WalletService
{

    private WalletRepo repo;

	
	public WalletServiceImpl(Map<String, Customer> data){
		repo= new WalletRepoImpl(data);
	}
	public WalletServiceImpl(WalletRepo repo) 
	{
		super();
		this.repo = repo;
	}
	

	public WalletServiceImpl() 
	{
	}
	

	public Customer createAccount(String name, String mobileNo, BigDecimal amount) 
	{
		Customer customer=null;
		
		if(isValidName(name) && isValidMobile(mobileNo) && isValidamount(amount))
		{
		customer=new Customer(name,mobileNo,new Wallet(amount));
		if(repo.findOne(mobileNo) != null)
			throw new InvalidInputException("The account with mobile Number "+ mobileNo+" is already created");
		repo.save(customer);
		}
		
		return customer;		
	}

	public Customer showBalance(String mobileNo) throws InvalidInputException
	{
		Customer customer=null;
		if(isValidMobile(mobileNo))
		{
		  customer=repo.findOne(mobileNo);
		}
		if(customer == null)
			throw new InvalidInputException("The mobile Number You Entered is Not having Payment Wallet Account");
		return customer;
	}

	public Customer fundTransfer(String sourceMobileNo, String targetMobileNo, BigDecimal amount) throws InsufficientBalanceException,InvalidInputException
	{
		Customer source=null;
		Customer target=null;
		if(isValidMobile(sourceMobileNo) && isValidMobile(targetMobileNo) && isValidamount(amount))
		{
			     if(sourceMobileNo.equals(targetMobileNo))
			    	 throw new  InvalidInputException("Enter Different Accounts to transfer Money");
			     
			     if(amount.compareTo(new BigDecimal(0)) == 0 )
			    	 throw new InvalidInputException("Enter valid Amount to transfer");
		         source=repo.findOne(sourceMobileNo);
		         
		         if(source == null)
		        	 throw new InvalidInputException("There is No Payment wallet account for the Number "+sourceMobileNo);
		         
	             target=repo.findOne(targetMobileNo);
	             
	             if(target == null)
	            	 throw new InvalidInputException("There is No Payment wallet account for the Number "+targetMobileNo);
		
		if(amount.compareTo(source.getWallet().getBalance()) > 0 )
			throw new InsufficientBalanceException("Insufficient Balance in the account "+sourceMobileNo);
		/*BigDecimal srcbalance=source.getWallet().getBalance().subtract(amount);
		BigDecimal tarbalance=target.getWallet().getBalance().add(amount);
		
		source.setWallet(new Wallet(srcbalance));
		target.setWallet(new Wallet(tarbalance));*/
		
		source=withdrawAmount(sourceMobileNo, amount);
		target=depositAmount(targetMobileNo, amount);
		repo.save(source);
		repo.save(target);
		}
		return source;
	}

	public Customer depositAmount(String mobileNo, BigDecimal amount) throws InvalidInputException
	{
		Customer customer=null;
		if(isValidMobile(mobileNo) && isValidamount(amount))
		{
		customer=repo.findOne(mobileNo);
		
		if(customer == null)
			throw new InvalidInputException("There is No Payment wallet account for the Number "+mobileNo);
		
		if(amount.equals(new BigDecimal(0)))
			throw new InvalidInputException("Enter Valid Amount to Withdraw");
		
		BigDecimal balance=customer.getWallet().getBalance().add(amount);
		customer.setWallet(new Wallet(balance));
		repo.save(customer);
		}
		
		return customer;
	}

	public Customer withdrawAmount(String mobileNo, BigDecimal amount) throws InsufficientBalanceException
	{
		Customer customer=null;
		if(isValidMobile(mobileNo) && isValidamount(amount))
		{
			if(amount.equals(new BigDecimal(0)))
				throw new InvalidInputException("Enter Valid Amount to Withdraw");
			
		 customer=repo.findOne(mobileNo);
		 
		 if(customer == null)
				throw new InvalidInputException("There is No Payment wallet account for the Number "+mobileNo);
		
		if(amount.compareTo(customer.getWallet().getBalance()) > 0 )
			throw new InsufficientBalanceException("Insufficient Balance");
		
		BigDecimal balance=customer.getWallet().getBalance().subtract(amount);
		customer.setWallet(new Wallet(balance));
		repo.save(customer);
	}
		return customer;
	
}





public boolean isValidName(String name) throws InvalidInputException 
{
	if( name == null)
		throw new InvalidInputException( "Sorry, Customer Name is null" );
	
	if( name.trim().isEmpty() )
		throw new InvalidInputException( "Sorry, customer Name is Empty" );
	
	return true;
}

public boolean isValidMobile(String mobileNo)throws InvalidInputException
{
	if( mobileNo == null ||  isPhoneNumberInvalid( mobileNo ))
		throw new InvalidInputException( "Sorry, Phone Number "+mobileNo+" is invalid"  );
	
	return true;
}

public boolean isValidamount(BigDecimal amount)throws InvalidInputException
{
	if( amount == null || isAmountInvalid( amount ) )
		throw new InvalidInputException( "Amount is invalid" );

	return true;
}

public boolean isAmountInvalid(BigDecimal amount) 
{
	
	if( amount.compareTo(new BigDecimal(0)) < 0) 
	{
		return true;
	}		
	else 
		return false;
}

public static boolean isPhoneNumberInvalid( String phoneNumber )
{
	if(String.valueOf(phoneNumber).matches("[1-9][0-9]{9}")) 
	{
		return false;
	}		
	else 
		return true;
}

}