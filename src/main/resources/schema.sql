DROP TABLE IF EXISTS 'User';

CREATE TABLE 'User' (
                        'id' bigint(20) unsigned NOT NULL AUTO_INCREMENT,
                        'firstname' varchar(100) NOT NULL,
                        'lastname' varchar(100) NOT NULL,
                        'username' varchar(100) NOT NULL,
                        KEY 'id' ('id'),
                        KEY 'username' ('username')
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8;