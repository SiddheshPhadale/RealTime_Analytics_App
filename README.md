# RealTime_Analytics_App

A Spring Boot application for real-time analytics using Kafka for event streaming and Redis for data aggregation.

## Key Features

This application offers a robust set of analytics capabilities via its REST API:

### Core Metrics & Totals:
* **Total Product Views:** Track the cumulative count of all product page views.
* **Total Adds to Cart:** Monitor the aggregate number of times products are added to shopping carts.
* **Total Purchases:** Keep a running count of completed purchase transactions.
* **Total Revenue:** Get the grand total of all generated revenue from purchases.

### Product Performance Insights:
* **Top Viewed Products:** Identify which products are attracting the most attention.
* **Top Added to Cart Products:** Discover items most frequently placed in shopping carts.
* **Top Purchased Products:** Pinpoint your best-selling products.

### Value & Conversion Analysis:
* **Average Add-to-Cart Value:** Understand the typical monetary value of items customers consider buying.
* **Average Order Value:** Calculate the average amount spent per completed order.
* **Product View to Cart Conversion Rate:** Measure the effectiveness of product pages in driving add-to-cart actions.
* **Cart to Purchase Conversion Rate:** Assess how efficiently items in carts are converted into actual purchases.


## Technologies

* **Spring Boot:** Application framework.
* **Apache Kafka:** Event streaming and consumption.
* **Redis:** In-memory data store for analytics aggregation.
