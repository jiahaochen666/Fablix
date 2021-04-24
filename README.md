# cs122b-spring20-team-83
cs122b-spring20-team-83 created by GitHub Classroom

Team members: Jiahao Chen, Zhanchun Wang

All the code are written by Jiahao and Zhanchun, and we do not plagrize anyone.

[project2 Demo Link](https://youtu.be/AygIk06IT54)

To deploy, we build war file using maven and move it into tomcat webapp folder.

For our substring matching, we use "like '%substring%'" in our sql for title, directors, and stars search(not including year). To find the title which starts with non-alphanumerical characters, we use "title REGEXP '^[^a-z0-9A-z]'". 

Jiahao Chen is the group leader and he set up both the backend and frontend of login page, search and browse page, jump functionality and all the css files.

Zhanchun Wang set up the backend and frontend of shopping cart page, payment page and additional performance, and upload all files to AWS server and record the video.

---
##### proj3

Jiahao Chen finished task: 2,4,6

Zhanchun Wang finished task:3,5,7

The java files we use preparedstatement are
- loginServlet.java
- movielistServlet.java
- paymentServlet.java
- dashboardServlet.java
- dashboard_addmovie.java


Two parsing time optimization strategies: 
    We choose to handle the three xml files by write the result into csv files first, and we also dump the whole database into csv files to compare with the new files, which significantly reduce the comparision time.
    Instead of using insert statements, we use load function to load the handled data from the csv file into the moviedb database directly.
    Two strategies help us to finish task 7 in less than 10 sec.

Inconsistency data link
 - [invalidActor](https://drive.google.com/file/d/14jmUJ3Pr7Ynuuy-UsEPnP6d5o3709UJN/view?usp=sharing)
 - [invalidMovies](https://drive.google.com/file/d/1rXUE2gIJM_N8QAqGaNlhZt2Yi85zt4IN/view?usp=sharing)
 - [invalidActorInMovies](https://drive.google.com/file/d/1Cb5U937FnCoTldKsYJDhY4LqvoeET1rs/view?usp=sharing)
    
[Proj3 Demo](https://youtu.be/GnJDAwiT1qw)   

---
###project4
[Proj4 Demo](https://youtu.be/0Ihs7emfHQ8)
To deploy, we build war file using maven and move it into tomcat webapp folder.
We did not do the fuzzy search.
Jiahao Chen do the Android part, and Zhanchun Wang do the auto-complete and full-text search.



- # General
    - #### Team#:83
    
    - #### Names: Zhanchun Wang     Jiahao  Chen
    
    - #### Project 5 Video Demo Link: https://drive.google.com/file/d/1f9x4U82iFDcNGkE5rsNnjSc69c7IKktr/view?usp=sharing

    - #### Instruction of deployment: To deploy, we build war file using maven and move it into tomcat webapp folder.

    - #### Collaborations and Work Distribution: Jiahao finishes task1 and task2. Zhanchun finishes task3. Task4 is finished collaboratively.


- # Connection Pooling
    - #### All files under src folder with "servlet" included in file name have been utilized with connection pooling.
    
    - #### All previous databases connection created by "@resource" are replaced by 
      `Context initContext = new InitialContext();            `
    
      `Context envContext = (Context) initContext.lookup("java:/comp/env");`
    
      
    
      `DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb");`
    
      or
    
      `DataSource ds = (DataSource) envContext.lookup("jdbc/moviedb-write");`
    
    - #### All reading actions are through localhost JDBC, and any action related to write will be redirected to master.
    
- # Master/Slave
    - #### WebContent/META-INF/context.xml, the master resource is "jdbc/moviedb-write".

    - #### All reading actions are through localhost JDBC, and any action related to write will be redirected to master.
    
- # JMeter TS/TJ Time Logs
    - #### The function would open and read every line inside the file path that was given, and store each line as integer inside a list. Then use sum(list) / len(list) to calculate the average time.


- #  JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**         | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                                 |
| --------------------------------------------- | ---------------------------- | -------------------------- | ----------------------------------- | ------------------------- | ------------------------------------------------------------ |
| Case 1: HTTP/1 thread                         | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-83/blob/master/p5/imgs/Screenshot%20from%202020-06-05%2019-38-43.png)   | 104                        | 3.3                                 | 2.95                      | This one takes shorter time because there would not be congestion when accessing the same resource. |
| Case 2: HTTP/10 threads                       | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-83/blob/master/p5/imgs/Screenshot%20from%202020-06-05%2019-46-30.png)   | 339                        | 4.9                                 | 4.64                      | This takes a little longer because there is congestion when some threads trying to access the same resource. |
| Case 3: HTTPS/10 threads                      | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-83/blob/master/p5/imgs/Screenshot%20from%202020-06-05%2020-05-08.png)   | 331                        | 4.6                                 | 4.40                      | HTTPS takes more time since it needs encryption compared to HTTP |
| Case 4: HTTP/10 threads/No connection pooling | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-83/blob/master/p5/imgs/Screenshot%20from%202020-06-05%2023-28-06.png)   | 345                        | 5.16                                | 4.94                      | Because there is not connection pooling, every request will create a new database connection|

| **Scaled Version Test Plan**                  | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis**                                           |
| --------------------------------------------- | ---------------------------- | -------------------------- | ----------------------------------- | ------------------------- | ------------------------------------------------------ |
| Case 1: HTTP/1 thread                         | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-83/blob/master/p5/imgs/Screenshot%20from%202020-06-05%2021-03-48.png)   | 106                       | 4.26                                 | 3.72                       | TS and TJ now are separated because of load balancing. |
| Case 2: HTTP/10 threads                       | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-83/blob/master/p5/imgs/Screenshot%20from%202020-06-05%2021-17-34.png)   | 217                       | 6.04                                 | 5.8                        | Runs slower because of congestion.                     |
| Case 3: HTTP/10 threads/No connection pooling | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring20-team-83/blob/master/p5/imgs/Screenshot%20from%202020-06-05%2023-40-14.png)   | 280                       | 4.5                                  | 4.1                        | Because there is not connection pooling, every request will create a new database connection|
