-- Change Delimiter to $$

drop procedure if exists add_movie;

DELIMITER $$

CREATE PROCEDURE add_movie(IN movie_title VARCHAR(100),
                           IN movie_year INT,
                           IN movie_director VARCHAR(100),
                           IN star_name VARCHAR(100),
                           IN genre_name VARCHAR(32))
BEGIN
    declare starmax varchar(100);
    declare moviemax varchar(100);
    declare genremax int;
    declare starexist varchar(100);
    declare genreexist varchar(100);
    IF (NOT EXISTS(SELECT *
                   FROM moviedb.movies
                   where title = movie_title
                     and year = movie_year
                     and director = movie_director)) THEN
        set moviemax = (select max(id) from moviedb.movies);
        insert into moviedb.movies
        values ((select concat((select cast((SELECT substring(moviemax, 1, 3)) as nchar)),
                               cast((select cast((SELECT substring(moviemax, 4, 10)) as signed int) + 1) as nchar))),
                movie_title, movie_year, movie_director);
        set moviemax = (select concat((select cast((SELECT substring(moviemax, 1, 3)) as nchar)),
                                      cast(
                                                  (select cast((SELECT substring(moviemax, 4, 10)) as signed int) + 1) as nchar)));
        if (NOT EXISTS(select * from moviedb.stars where name = star_name)) then
            set starmax = (select max(id) from moviedb.stars);
            insert into moviedb.stars
            values ((select concat((select cast((SELECT substring(starmax, 1, 2)) as nchar)),
                                   cast((select cast((SELECT substring(starmax, 3, 9)) as signed int) + 1) as nchar))),
                    star_name, null);
            set starmax = (select max(id) from moviedb.stars);
            insert into moviedb.stars_in_movies values (starmax, moviemax);
            set starexist = 'New ';
        else
            set starmax = (select concat((select max(id) from moviedb.stars where name = star_name)));
            insert into moviedb.stars_in_movies values (starmax, moviemax);
            set starexist = 'Exist ';
        end if;
        if (not exists(select name, id from genres where name = genre_name)) then
            set genremax = (select max(id) from moviedb.genres);
            insert into moviedb.genres values ((genremax + 1), genre_name);
            set genremax = (select max(id) from moviedb.genres);
            insert into moviedb.genres_in_movies
            values ((select max(id) from genres where name = genre_name), moviemax);
            set genreexist = 'New ';
        else
            set genremax = ((select max(id) from moviedb.genres where name = genre_name));
            insert into moviedb.genres_in_movies
            values ((select max(id) from genres where name = genre_name), moviemax);
            set genreexist = 'Exist ';
        end if;
        set moviemax = (select max(id) from moviedb.movies);
        insert into moviedb.ratings values (moviemax, 0.0, 0);
        select moviemax as movieId, starmax as starId, genremax as genreId, starexist, genreexist;
    else
        set starmax = 'Exist';
        set moviemax = 'Exist';
        set genremax = '0';
        set starexist = 'Exist';
        set genreexist = 'Exist';
        select moviemax as movieId, starmax as starId, genremax as genreId, starexist, genreexist;
    END IF;
END
$$
-- Change back Delimiter to ;
DELIMITER ;

# call add_movie('wzc1000', 2000, 'wzc','wzc','wzc')