### Spring Web Flux Functional example
- Get all products:
```
curl --location --request GET 'http://localhost:8080/products'
```
- Get product by id:
```
curl --location --request GET 'http://localhost:8080/products/5f03df708e2df44aa57da093'
``` 
- Create product:
```
curl --location --request POST 'http://localhost:8080/products' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "Black Tea",
    "price": 1.49
}'
```
- Update product:
```
curl --location --request PUT 'http://localhost:8080/products/5f0476e78e2df44aa57da095' \
--header 'Content-Type: application/json' \
--data-raw '{
    "id": "5f0476e78e2df44aa57da095",
    "name": "Black Tea",
    "price": 1.69
}'
```
- Delete product by id:
```
curl --location --request DELETE 'http://localhost:8080/products/5f0476e78e2df44aa57da095'
```
- Delete all products:
```
curl --location --request DELETE 'http://localhost:8080/products'
```
- Stream product events:
```
http://localhost:8080/products/events
```