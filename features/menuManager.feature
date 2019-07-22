Feature: Menu Manager
  Background: User landed on CMS
  		Given User is on / Page
  
  Scenario: Create and verify a Public Menu under Page Settings and on frontend for End User
  	And User navigates to /user/login from NAM
    When User login into CMS
    And Click on menuManager
    And Click on addMenu
    Then Type Menu Name Test
    And Click on Public Access Level
    And Click on Internal Pages
    And Drag and Drop Menu item
    And Click on External Pages
    And Insert Page Title %{GD_PageTitle} and Page Link %{GD_PageLink} of External Page
    And Drag and Drop Menu item
    And Get all the menu nodes from CMS for current menu
    And Save Menu
    Given User navigates to /admin/page-manager/view-pages from NAM
    Then User clicks Logged In type PageSettings icon or creates New ContentPage if no available
    And Verify menuName %{GD_MenuName} is not available
    Given User navigates to /admin/page-manager/view-pages from NAM
    Then User clicks Public type PageSettings icon or creates New ContentPage if no available
    And Select %{GD_MenuName} from Menu and click save
    Given User navigates to /user/logout from NAM
    And User navigates to /%{GD_PageUrl} from NAM
    Then Verify the content menu items is displaying as per CMS
    
  Scenario: Create and verify a Logged In Menu under Page Settings and on frontend for End User
  	And User navigates to /user/login from NAM
    When User login into CMS
    And Click on menuManager
    And Click on addMenu
    Then Type Menu Name Test
    And Click on LoggedIn Access Level
    And Click on Internal Pages
    And Drag and Drop Menu item
    And Click on External Pages
    And Insert Page Title %{GD_PageTitle} and Page Link %{GD_PageLink} of External Page
    And Drag and Drop Menu item
    And Get all the menu nodes from CMS for current menu
    And Save Menu
    Given User navigates to /admin/page-manager/view-pages from NAM
    Then User clicks Public type PageSettings icon or creates New ContentPage if no available
    And Verify menuName %{GD_MenuName} is not available
    Given User navigates to /admin/page-manager/view-pages from NAM
    Then User clicks Logged In type PageSettings icon or creates New ContentPage if no available
    And Select %{GD_MenuName} from Menu and click save
    Given User navigates to /user/logout from NAM
    And User navigates to /%{GD_PageUrl} from NAM
    When User enters %{GD_EMAIL_ADDRESS} and %{GD_PASSWORD}
    Then Verify the content menu items is displaying as per CMS
    
 	Scenario: Create a Public Menu as Active and verify it should display default on frontend for End User
  	And User navigates to /user/login from NAM
    When User login into CMS
    And Click on menuManager
    And Click on addMenu
    Then Type Menu Name Test
    And Click on Public Access Level
    And Click on Internal Pages
    And Drag and Drop Menu item
    And Click on External Pages
	  And Insert Page Title %{GD_PageTitle} and Page Link %{GD_PageLink} of External Page
    And Drag and Drop Menu item
    And Get all the menu nodes from CMS for current menu
    And Save Menu
    And Select %{GD_MenuName} as Active Public Menu and Click Save
    Given User navigates to /admin/page-manager/view-pages from NAM
    Then User clicks Public type PageSettings icon or creates New ContentPage if no available
    And Select Default from Menu and click save
    Given User navigates to /user/logout from NAM
    And User navigates to /%{GD_PageUrl} from NAM
    Then Verify the content menu items is displaying as per CMS 
    
 	 Scenario: Create a LoggedIn Menu as Active and verify it should display default on frontend for End User
  	And User navigates to /user/login from NAM
    When User login into CMS
    And Click on menuManager
    And Click on addMenu
    Then Type Menu Name Test
    And Click on LoggedIn Access Level
    And Click on Internal Pages
    And Drag and Drop Menu item
    And Click on External Pages
    And Insert Page Title %{GD_PageTitle} and Page Link %{GD_PageLink} of External Page
    And Drag and Drop Menu item
    And Get all the menu nodes from CMS for current menu
    And Save Menu
    And Select %{GD_MenuName} as Active LoggedIn Menu and Click Save
    Given User navigates to /admin/page-manager/view-pages from NAM
    Then User clicks Logged In type PageSettings icon or creates New ContentPage if no available
    And Select Default from Menu and click save
    Given User navigates to /user/logout from NAM
    And User navigates to /%{GD_PageUrl} from NAM
    When User enters %{GD_EMAIL_ADDRESS} and %{GD_PASSWORD}
    Then Verify the content menu items is displaying as per CMS 
    
   Scenario: Create and verify a Hybrid Menu under Page Settings and on frontend for End User
  	And User navigates to /user/login from NAM
    When User login into CMS
    And Click on menuManager
    And Click on addMenu
    Then Type Menu Name Test
    And Click on Public Access Level
    And Click on LoggedIn Access Level
    And Click on Internal Pages
    And Drag and Drop Menu item
    And Click on External Pages
    And Insert Page Title %{GD_PageTitle} and Page Link %{GD_PageLink} of External Page
    And Drag and Drop Menu item
    And Get all the menu nodes from CMS for current menu
    And Save Menu
    Given User navigates to /admin/page-manager/view-pages from NAM
    Then User clicks hybrid type PageSettings icon or creates New ContentPage if no available
    And Select %{GD_MenuName} from Menu and click save
    Given User navigates to /user/logout from NAM
    And User navigates to /%{GD_PageUrl} from NAM
    Then Verify the content menu items is displaying as per CMS
    
  Scenario: Create a Public Menu verify it under public page change to LoggedIn type and verify under LoggedIn and Public Page
  	And User navigates to /user/login from NAM
    When User login into CMS
    And Click on menuManager
    And Click on addMenu
    Then Type Menu Name Test
    And Click on Public Access Level
    And Click on Internal Pages
    And Drag and Drop Menu item
    And Click on External Pages
    And Insert Page Title %{GD_PageTitle} and Page Link %{GD_PageLink} of External Page
    And Drag and Drop Menu item
    And Save Menu
    And Select %{GD_MenuName} from Menu and click save
    When Click on leftMenu Hamburger
    And Click on menuManager