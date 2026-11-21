package com.finance.service;

import com.finance.entity.FinovaUserDetails;
import com.finance.model.CustomerDetails;

public interface CustomerService {
	
	public FinovaUserDetails validateUser(FinovaUserDetails userRegister);

	public FinovaUserDetails userLogin(CustomerDetails customerDetails);

	public FinovaUserDetails addUser(FinovaUserDetails userRegister);
	
	public String userLogOut();
	
    public FinovaUserDetails getFinovaUser();

	public FinovaUserDetails updateProfile(FinovaUserDetails finovaUserDetails);

}
