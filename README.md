# fundrecs


DEVELOPMENT

* Enable dev profile using VM arg -Dspring.profiles.active=development

RUNNING APPLICATION

* Build the application using: mvn clean package
* To execute, issue the command: java -jar target/rchiarinelli-0.0.1-SNAPSHOT.jar


API Documentation


* API - /transactions
POST body example to save new transactions:

[
   {
       "date": "11-12-2018",
       "type": "CREDIT", //UPPERCASE
       "amount": "9898.36"
   },
   {
       "date": "01-06-2017",
       "type": "DEBIT", //UPPERCASE
       "amount": "50"
   }
]

* API - /transactions/{date}
GET example: /transactions/01-06-2017

[
    {
        "date": "01-06-2017",
        "type": "DEBIT",
        "amount": 50
    }
]

* API - /transactions/{date}/{type}

GET example: /transactions/01-06-2017/DEBIT
[
   {
       "date": "01-06-2017",
       "type": "DEBIT", 
       "amount": "50"
   }
]


