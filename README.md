# Weather_JPMC
JPMC Weather Application

This application is based on Model-View-Presenter (MVP) design pattern. 
With this approach we get better separation of code and each module has single responsibility to perform. 
With this we can achieve more unit test coverage since business logic is separated from view or model. 
Also it is easy to follow Dependency Injection with MVP pattern. 
If required we can have Dagger 2.0 implementation to completely achieve this. 
Also if there is any change in UI then it not required to changes any presenter or model logic. 
View can be changed without changing the presenter business logic. 
Model - contains only the application data which we publish in the view. 
There can be model specific to JSON data coming from server or DB specific data. 
View - contains only the UI part. We try to view keep as simple as possible without any business logic. 
Presenter - contains the business logic. Presneter takes care of getting and publishing the updated data to view.
