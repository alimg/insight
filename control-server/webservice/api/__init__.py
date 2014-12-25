import ServerConstants
import mysql.connector.pooling

ServerConstants.mysql_pool = mysql.connector.pooling.MySQLConnectionPool(pool_name = "mypool",
                                                      pool_size = 3,
                                                      **ServerConstants.dbconfig)
