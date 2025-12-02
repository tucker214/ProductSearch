A basic app that allows user to lookup products from the Kroger public API. The app will generate an authorization token each time it is loaded up via POST request. It will also check if the token is valid each time a search is requested. The app uses the Zxing library to parse barcodes through the camera, and will send a request with the numeric value of that barcode to search for products. The user can also enter search terms in the search bar to send a request. A request can display a column of up to 50 entries, which is the API limit for one search, and will then display the product image, aisle location, price, and the 13 digit UPC. Image functionality is provided with the Coil image library.
Networking uses Retrofit + OKHttp3


![alt text](https://github.com/tucker214/ProductSearch/blob/main/Screenshot_20250103_220238.jpg?raw=true)
