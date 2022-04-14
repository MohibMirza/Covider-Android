STEPS/INFO:
1. Install Redis (https://www.youtube.com/watch?app=desktop&v=wv5kPhjcK_s)
2. Start redis by running “redis-server” on command line
3. Clone this github repo and locate the DummyData.java file in redis.test package. Run the
java file to populate the database with dummy data.
https://github.com/MohibMirza/Covider-Backend-Redis
4. Start the app in android studio. Here you can login and play with the features with a few
login accounts. Here are some logins you can check out:
a. Username : Password
b. Sarah : 1234
c. Mark : 1234
d. John : 1234
e. Sam : 1234
f. INFO: They are already filled up with individual user data such as classes and
most visited locations on the map. When you log on, in the self-reports tab there
will be data on the student’s covid status as well as whether his classes are in
person or not. If you log in with Sarah, who is an instructor, you will notice that
you can see all the people in her classes individual covid statuses.
5. In the self-report tab you can declare if you have COVID or have symptoms of sickness
or are healthy. You can also report entering a building. The system keeps track of how
many times you entered a building and how frequently. If you entered a building within
10 days of reporting covid, the building’s covid score will decrease by 5. Every buildings
covid score starts at 1000 and as semester goes on it will decrease as more cases are
reported. Also, instructors have a special option in their self-report tab to declare whether
their classes are in-person or online. Also, if an instructor gets covid, all their classes will
automatically be set to online.
6. On the map the buildings will be color coded. Green buildings are buildings the user
visited a lot and are part of his class. Yellow buildings are medium-visited buildings. Red
are buildings you don’t frequent very often. If you click on the markers you can get data
on the buildings such as any special instructions and the covid status of the building. You
can make the covid score decrease on any building by reporting that you went to
building and then afterwards reporting symptoms or you have covid. The system keeps
track of all this data from the database and it will persist even if you log in with other
users.
7. In the Statuses tabs you can view your own status as well as whether you classes are
in-person or hybrid.
Please let us know if you have any questions regarding running the assignment or any logistics
on it. Thank you for viewing this, we worked very hard on it and are hoping for the best.
Thanks.
