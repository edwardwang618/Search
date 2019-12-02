Products is the java bean that has been mapped to the table in MySQL.

The table structure is like this:

+--------------+--------------+------+-----+---------+----------------+
| Field        | Type         | Null | Key | Default | Extra          |
+--------------+--------------+------+-----+---------+----------------+
| pid          | int(11)      | NO   | PRI | NULL    | auto_increment |
| pname        | varchar(255) | YES  |     | NULL    |                |
| catalog      | int(11)      | YES  |     | NULL    |                |
| catalog_name | varchar(50)  | YES  |     | NULL    |                |
| price        | double       | YES  |     | NULL    |                |
| number       | int(11)      | YES  |     | NULL    |                |
| description  | longtext     | YES  |     | NULL    |                |
| picture      | varchar(255) | YES  |     | NULL    |                |
| release_time | datetime     | YES  |     | NULL    |                |
+--------------+--------------+------+-----+---------+----------------+


The information of 3803 goods has been stored in MySQL, and with the Dataimport interface in solr server, all data has been imported to solr server and analyzed to create index. The fields are defined as follow in the managed-schema file:

```
<field name="prod_pname" type="solr_cnAnalyzer" indexed="true" stored="true" required="true"/>
  <!-- prod_catalog_name does not need to be analyzed-->
  <field name="prod_catalog_name" type="string" indexed="true" stored="true" required="true"/>
  <field name="prod_price" type="pdouble" indexed="true" stored="true" required="true"/>
  <field name="prod_description" type="solr_cnAnalyzer" indexed="true" stored="true" required="true"/>
<field name="prod_picture" type="string" indexed="false" stored="true" required="true"/>
```

Solr_cnAnalyzer is an analyzer which is suitable to analyze Chinese words as well as English words.


The data-config.xml is as follow for solr to use when importing data:

```
<?xml version="1.0" encoding="UTF-8" ?>
<dataConfig>
  <dataSource type="JdbcDataSource"
              driver="com.mysql.jdbc.Driver"
              url="jdbc:mysql://localhost:3306/solr_test"
              user="root"
              password="123456"/>
  <document>
    <entity name="products" query="select pid,pname,catalog_name,price,description,picture from products">
      <field column="pid" name="id"/>
      <field column="pname" name="prod_pname"/>
      <field column="catalog_name" name="prod_catalog_name"/>
      <field column="price" name="prod_price"/>
      <field column="description" name="prod_description"/>
      <field column="picture" name="prod_picture"/>
    </entity>
  </document>
</dataConfig>
```

To import the data, we need to uncomment the following in solrconfig.xml:
```
<requestHandler name="/dataimport" class="org.apache.solr.handler.dataimport.DataImportHandler">
    <lst name="defaults">
      <str name="config">data-config.xml</str>
    </lst>
</requestHandler>
```

For details, please check the files in the related_files directory. And please notice that, the IDE might report error that some methods not found from the class "Products". By using "lombok", we do not need to add getter and setter in the java bean. Instead we only need to add "Data" annotation ahead of java bean. The code can run smoothly with lombok.
