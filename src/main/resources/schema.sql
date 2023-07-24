create table IF NOT EXISTS mars_rover_model(
  id INT NOT NULL AUTO_INCREMENT,
  rover_name VARCHAR(255) NOT NULL,
  x_axis INT NOT NULL,
  y_axis INT NOT NULL,
  cardinal_direction VARCHAR(5) NOT NULL,
  PRIMARY KEY ( id )
);