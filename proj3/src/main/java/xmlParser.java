import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

class xmlParser {
    static Connection connection;
    static String home = System.getProperty("user.home");

    private static void dump() {
        try {
            File movies_csv = new File(home + "/proj3/moviedb_movies.csv");
            BufferedWriter out = new BufferedWriter(new FileWriter(movies_csv));

            String query = "SELECT * FROM movies";
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "mypassword");
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                out.write(rs.getString("id") + "," + rs.getString("title") + "," +
                        rs.getInt("year") + "," + rs.getString("director") + "\n");
            }
            rs.close();
            statement.close();
            out.flush();
            out.close();

            File stars = new File(home + "/proj3/moviedb_stars.csv");
            out = new BufferedWriter(new FileWriter(stars));

            query = "SELECT * FROM stars";
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                out.write(rs.getString("id") + "," + rs.getString("name") + "," +
                        rs.getString("birthYear") + "\n");
            }
            rs.close();
            statement.close();
            out.flush();
            out.close();

            File genres = new File(home + "/proj3/moviedb_genres.csv");
            out = new BufferedWriter(new FileWriter(genres));

            query = "SELECT * FROM genres";
            statement = connection.prepareStatement(query);
            rs = statement.executeQuery();
            while (rs.next()) {
                out.write(rs.getString("id") + "," + rs.getString("name") + "\n");
            }
            rs.close();
            statement.close();
            out.flush();
            out.close();
        } catch (Exception throwable) {
            throwable.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            dump();
            int actor_id = 9423081;
            File file3 = new File(home + "/proj3/moviedb_stars.csv");
            BufferedReader br3 = new BufferedReader(new FileReader(file3));
            HashMap<String, String> origin_map = new HashMap<>();
            String line;
            HashMap<String, String> actorId = new HashMap<>();
            while ((line = br3.readLine()) != null) {
                String[] arr = line.split(",");
                origin_map.put(arr[1], arr[2]);
                actorId.put(arr[1], arr[0]);
            }
            Document doc3 = new SAXReader().read(new File(home + "/proj3/actors63.xml"));
            //选择xml文件的节点
            List actors = doc3.selectNodes("actors");
            StringBuilder sbString_actor = new StringBuilder();
            StringBuilder invalid_actor = new StringBuilder();

            for (Object a : actors) {
                List actor = ((Element) a).elements("actor");
                for (Object b : actor) {
                    String name = ((Element) b).elementText("stagename");
                    String birth = ((Element) b).elementText("dob");
                    if (!origin_map.containsKey(name) || (origin_map.containsKey(name) && !origin_map.get(name).equals(birth))) {
                        sbString_actor.append("nm").append(actor_id).append("\t").append(name).append("\t");
                        actorId.put(name, "nm" + actor_id);
                        actor_id++;
                        if (birth.length() == 0) {
                            sbString_actor.append("\\N").append("\n");
                        } else {
                            sbString_actor.append(birth).append("\n");
                        }
                    } else {
                        invalid_actor.append(name).append("\t").append(birth).append("\n");
                    }
                }
            }

//            System.out.println(sbString_actor.toString());
            ////////////////////////////

            int id = 499470;
            File file1 = new File(home + "/proj3/moviedb_movies.csv");
            File file2 = new File(home + "/proj3/moviedb_genres.csv");
            BufferedReader br = new BufferedReader(new FileReader(file1));
            BufferedReader br1 = new BufferedReader(new FileReader(file2));
            ArrayList<String> existGenre = new ArrayList<>();
            HashMap<String, ArrayList<String>> map = new HashMap<>();

            while ((line = br.readLine()) != null) {
                ArrayList<String> value = new ArrayList<>();
                String[] arr = line.split(",");
                value.add(arr[2]);
                value.add(arr[3]);
                map.put(arr[1], value);
            }

            while ((line = br1.readLine()) != null) {
                existGenre.add(line.split(",")[1]);
            }
//            conn = DbUtil.getConnection();
//            pstmt = conn.prepareStatement(sql);
            HashSet<String> genres = new HashSet<>();
            HashMap<String, String> movie_id = new HashMap<>();
            HashMap<String, String> genre_movie = new HashMap<>();
            Document doc = new SAXReader().read(new File(home + "/proj3/mains243.xml"));
            //选择xml文件的节点
            List directorFilms = doc.selectNodes("movies/directorfilms");
            StringBuilder sbString = new StringBuilder();
            StringBuilder invalid = new StringBuilder();
            for (Object directorInfo : directorFilms) {
                Element dirInfo = (Element) directorInfo;
                //读取节点内容
                String dirname = null;
                List director = dirInfo.elements("director");
                for (Object object : director) {
                    Element info = (Element) object;
                }
                List films = dirInfo.elements("films");
                for (Object object : films) {
                    Element film = (Element) object;
                    List filmInfo = film.elements("film");
                    for (Object info : filmInfo) {
                        boolean success = false;
                        Element fInfo = (Element) info;
                        String title = fInfo.elementText("t");
                        String year = fInfo.elementText("year");
                        List dirs = fInfo.elements("dirs");
                        List cats = fInfo.elements("cats");
                        for (Object dir : dirs) {
                            List dir_Info = ((Element) dir).elements("dir");
                            for (Object dInfo : dir_Info) {
                                String dirn = ((Element) dInfo).elementText("dirn");
                                if (title.length() != 0 && dirn != null && !(dirn.contains("Unknown")) && !(dirn.contains("unknown")) && !dirn.contains("UnYear") && !year.contains("yy") && title != null) {
                                    if (!(map.containsKey(title)) || (map.containsKey(title) && !map.get(title).get(1).equals(dirn))) {
                                        sbString.append("tt0").append(id).append("\t").append(title).append("\t").append(year).append("\t").append(dirn).append("\n");
                                        movie_id.put(title, "tt0" + id);
                                        success = true;
                                        id++;
                                    } else {
                                        invalid.append(dirn).append("\t").append(title).append("\t").append(year).append("\n");
                                    }
                                } else {
                                    invalid.append(dirn).append("\t").append(title).append("\t").append(year).append("\n");
                                }
                            }
                        }
                        for (Object cat : cats) {
                            String genre = ((Element) cat).elementText("cat");
                            if (genre != null && genre.length() != 0) {
                                genre = genre.trim();
                                String cap = genre.substring(0, 1).toUpperCase() + genre.substring(1);
                                genres.add(cap);
                                if (success) {
                                    genre_movie.put("tt0" + (id - 1), cap);
                                }
                            }
                        }
                    }
                }
            }

            HashMap<String, Integer> genreMap = new HashMap<>();
            StringBuilder genre_string = new StringBuilder();
            int genre_id = 24;
            for (String s : genres) {
                s = s.trim();
                String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
                genre_string.append(genre_id).append("\t").append(cap).append("\n");
                genreMap.put(cap, genre_id);
                genre_id++;
            }

            for (String s : genre_movie.keySet()) {
                if (genreMap.get(genre_movie.get(s)) != null) {
                    genre_movie.replace(s, genreMap.get(genre_movie.get(s)).toString());
                }
            }


            Document doc1 = new SAXReader().read(new File(home + "/proj3/casts124.xml"));
            //选择xml文件的节点
            StringBuilder actorInMovie = new StringBuilder();
            StringBuilder invalidActorInMovie = new StringBuilder();
            List dirfimls = doc1.selectNodes("casts/dirfilms");
            for (Object o : dirfimls) {
                List filmc = ((Element) o).elements("filmc");
                for (Object x : filmc) {
                    List ms = ((Element) x).elements("m");
                    for (Object y : ms) {
                        String movieTitle = ((Element) y).elementText("t");
                        String star = ((Element) y).elementText("a");
                        if (movie_id.get(movieTitle) != null && actorId.get(star) != null) {
                            actorInMovie.append(actorId.get(star)).append("\t").append(movie_id.get(movieTitle)).append("\n");
                        } else {
                            invalidActorInMovie.append(movieTitle).append("\t").append(star).append("\n");
                        }
                    }
                }
            }

//            FileWriter f = new FileWriter("/home/jason/Desktop/movies.txt");
            BufferedWriter printWriter = new BufferedWriter(new FileWriter(home + "/proj3/movies.csv"));
            String[] ar = sbString.toString().split("\n");
            for (String s : ar) {
                printWriter.write(s);
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();

            printWriter = new BufferedWriter(new FileWriter(home + "/proj3/genres.csv"));
            StringBuilder genreString = new StringBuilder();
            for (String s : genreMap.keySet()) {
                genreString.append(genreMap.get(s)).append("\t").append(s).append("\n");
            }
//            System.out.println(genreString);
            ar = genreString.toString().split("\n");
            for (String s : ar) {
                printWriter.write(s);
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();


            printWriter = new BufferedWriter(new FileWriter(home + "/proj3/stars.csv"));
//            System.out.println(sbString_actor);
            ar = sbString_actor.toString().split("\n");
            for (String s : ar) {
                printWriter.write(s);
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();

            printWriter = new BufferedWriter(new FileWriter(home + "/proj3/stars_in_movies.csv"));
            ar = actorInMovie.toString().split("\n");
            for (String s : ar) {
                printWriter.write(s);
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();
//            System.out.println(actorInMovie.toString());

            printWriter = new BufferedWriter(new FileWriter(home + "/proj3/genres_in_movies.csv"));
//            ar = genre_movie.toString().split("\n");
            for (String s : genre_movie.keySet()) {
                printWriter.write(genre_movie.get(s) + "\t" + s);
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();

            printWriter = new BufferedWriter(new FileWriter(home + "/proj3/ratings.csv"));
//            ar = genre_movie.toString().split("\n");
            for (String s : movie_id.keySet()) {
                printWriter.write(movie_id.get(s) + "\t0.0\t0");
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();


            /*
            invalid_actor
            invalid
            invalidActorInMovie
             */

            printWriter = new BufferedWriter(new FileWriter(home + "/proj3/invalid_actor.csv"));
            ar = invalid_actor.toString().split("\n");
            for (String s : ar) {
                printWriter.write(s);
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();

            printWriter = new BufferedWriter(new FileWriter(home + "/proj3/invalidMovies.csv"));
            ar = invalid.toString().split("\n");
            for (String s : ar) {
                printWriter.write(s);
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();

            printWriter = new BufferedWriter(new FileWriter(home + "/proj3/invalidActorInMovies.csv"));
            ar = invalidActorInMovie.toString().split("\n");
            for (String s : ar) {
                printWriter.write(s);
                printWriter.write("\n");
                printWriter.flush();
            }
            printWriter.close();


            load("genres");
            load("stars");
            load("movies");
            load("stars_in_movies");
            load("genres_in_movies");
            load("ratings");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            DbUtil.close(pstmt);
//            DbUtil.close(conn);
        }
    }

    private static void load(String fileName) throws SQLException {
        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/moviedb", "mytestuser", "mypassword");
        Statement stmt = con.createStatement();
        String sql =
                "load data local infile '" + home + "/proj3/" + fileName + ".csv' \n" +
                        "   into table " + fileName + "\n" +
                        "   columns terminated by '\t' \n";
        System.out.println(stmt.execute(sql));
        stmt.close();
        con.close();
    }
}