Feature: Prod Sanity
	Background: User landed on homepage
		Given User is on / Page
	
	Scenario: Prod sanity verification
		Then User creates account
		#Then User logged in successfully
		Given Customer details are fetched for %{GD_NEW_EMAIL_ADDRESS} and %{GD_NEW_PASSWORD}
		When User clicks your account
		Then Classic AMGR is displayed with correct %{GD_AccountName} and %{GD_AccountId}
		Given User logout from CAM
		And User navigates to /user/logout from NAM
		When User enters %{GD_EMAIL_ADDRESS} and %{GD_PASSWORD}
		Then User logged in successfully
		Given Customer details are fetched for %{GD_EMAIL_ADDRESS} and %{GD_PASSWORD}
		When User clicks your account
		Then Classic AMGR is displayed with correct %{GD_AccountName} and %{GD_AccountId}
		Given User logout from CAM
		And User navigates to /user/logout from NAM
		When User enters %{GD_NEW_EMAIL_ADDRESS} and %{GD_NEW_PASSWORD}
		Then User logged in successfully
		Given Change password to AMGR1234 for %{GD_NEW_EMAIL_ADDRESS} and %{GD_NEW_PASSWORD}
		And User navigates to /user/logout from NAM
		And User navigates to / from NAM
		When User enters %{GD_NEW_EMAIL_ADDRESS} and %{GD_NEW_PASSWORD}
		Then User logged in successfully
		Given Customer details are fetched for %{GD_NEW_EMAIL_ADDRESS} and %{GD_NEW_PASSWORD}
		When User clicks your account
		Then Classic AMGR is displayed with correct %{GD_AccountName} and %{GD_AccountId}
		
	Scenario: Verify privacy link is set up correctly
		And User clicked on signup link
		Then Verify user is navigated to correct link on clicking privacy policy or terms link
		
	Scenario: Verify CMS Login successfull
		And User navigates to /user/login from NAM
		When User login into CMS
		And Navigate to /admin/group-view
		Then Promotiles page is displayed