let title = getParameterByName("title");
let year = getParameterByName("year");
let director = getParameterByName("director");
let actor = getParameterByName("actor");
let genre = getParameterByName("genre");
let start = getParameterByName("start");
let frontpage = parseInt(getParameterByName("frontpage"));
let size = parseInt(getParameterByName("size"));
let order = getParameterByName("order");
let sort = getParameterByName("sort")


// This function is copied from cs122b-spring20-project1-api-example
function getParameterByName(target) {
    let url = window.location.href;

    target = target.replace(/[\[\]]/g, "\\$&");


    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';


    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleMovieResult(resultData) {
    console.log("handleMovieResult: populating movies table from resultData");

    let movieTableBodyElement = jQuery("#movielist_table_body");

    var total = resultData[resultData.length - 1]["total"];
    console.log(resultData)
    console.log(resultData[resultData.length - 1])
    var total = resultData[resultData.length - 1]["total"];

    for (let i = 0; i < resultData.length - 1; i++) {
        movieId = resultData[i]["id"]

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML +=
            "<th>" + '<a href="singlemovie.html?movieid=' + resultData[i]['id'] + '">' + resultData[i]["title"] + '</a>' + "</th>";
        rowHTML += "<th>" + resultData[i]["year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["director"] + "</th>";
        rowHTML += "<th>";
        for (let j = 0; j < resultData[i]["moviegenres"].length; j++) {
            rowHTML += "<p>" + '<a href="index.html?genre=' + resultData[i]["moviegenres"][j] + '&frontpage=0&size=50&sort=rating&order=Desc1Desc2">' + resultData[i]["moviegenres"][j] + '</a>' + "</p>";
        }
        rowHTML += "</th>";
        rowHTML += "<th>";
        for (let j = 0; j < resultData[i]["moviestars"].length; j++) {
            rowHTML += "<p>" + '<a href="singleStar.html?starid=' + resultData[i]["moviestars"][j]["id"] + '">' + resultData[i]["moviestars"][j]["name"] + '</a>' + "</p>";
        }

        rowHTML += "</th>";

        rowHTML += "<th = rating>" + resultData[i]["rating"] + "</th>";
        rowHTML += "<th>"
        const x = "add";
        ///////////////////////////////////////////////////////////////////////////////////////////
        rowHTML += "<p> <img src='shopping.png' onclick=' handleItem(\"" + resultData[i]["title"] + ",add," + movieId + "\")'/></p>"
        // rowHTML += "<button id = movieId onclick = \"handleItemInfo(" + resultData[i]["id"] + ")\" >Add to Cart</button>"


        rowHTML += "</th>";
        rowHTML += "</tr>";
        movieTableBodyElement.append(rowHTML);

    }

    let pre = jQuery("#pre");
    let rowHTML = "";
    let url = "";
    let prepage = frontpage - size;
    if (prepage >= 0) {
        if (!genre && !start) {
            url += "index.html?title=" + title + "&year=" + year + "&director=" + director + "&actor=" + actor + "&frontpage=" + prepage + "&size=" + size + "&order=" + order + "&sort=" + sort;
        } else {
            if (!start) {
                url += "index.html?genre=" + genre + "&frontpage=" + prepage + "&size=" + size + "&order=" + order + "&sort=" + sort;
            } else {
                url += "index.html?start=" + start + "&frontpage=" + prepage + "&size=" + size + "&order=" + order + "&sort=" + sort;
            }
        }
        rowHTML += "<a href='" + url + "'>" + "prev" + "</a>";
        pre.append(rowHTML)
    }

    let next = jQuery("#next");
    rowHTML = "";
    url = "";
    let nextpage = frontpage + size;
    if (nextpage <= total) {
        if (!genre && !start) {
            url += "index.html?title=" + title + "&year=" + year + "&director=" + director + "&actor=" + actor + "&frontpage=" + nextpage + "&size=" + size + "&order=" + order + "&sort=" + sort;
        } else {
            if (!start) {
                url += "index.html?genre=" + genre + "&frontpage=" + nextpage + "&size=" + size + "&order=" + order + "&sort=" + sort;
            } else {
                url += "index.html?start=" + start + "&frontpage=" + nextpage + "&size=" + size + "&order=" + order + "&sort=" + sort;
            }
        }
        rowHTML += "<a href='" + url + "'>" + "next" + "</a>";
        next.append(rowHTML)
    }

    let page = jQuery("#page");
    for (let i = frontpage, j = Math.floor(frontpage / size) + 1, showpages = 1; i < total; i += size, j++, showpages++) {
        let rowHTML = "";
        let url = "";
        if (!genre && !start) {
            url += "index.html?title=" + title + "&year=" + year + "&director=" + director + "&actor=" + actor + "&frontpage=" + i + "&size=" + size + "&order=" + order + "&sort=" + sort;
        } else {
            if (!start) {
                url += "index.html?genre=" + genre + "&frontpage=" + i + "&size=" + size + "&order=" + order + "&sort=" + sort;
            } else {
                url += "index.html?start=" + start + "&frontpage=" + i + "&size=" + size + "&order=" + order + "&sort=" + sort;
            }
        }
        rowHTML += "<a href='" + url + "'>" + j + "</a>";
        page.append(rowHTML)
        if (showpages > 5) {
            break;
        }
    }


    let frontsize = jQuery("#frontsize");
    let l = [10, 25, 50, 100]
    for (let i = 0; i < l.length; i++) {
        let rowHTML = "";
        let url = "";
        if (!genre && !start) {
            url += "index.html?title=" + title + "&year=" + year + "&director=" + director + "&actor=" + actor + "&frontpage=" + frontpage + "&size=" + l[i] + "&order=" + order + "&sort=" + sort;
        } else {
            if (!start) {
                url += "index.html?genre=" + genre + "&frontpage=" + frontpage + "&size=" + l[i] + "&order=" + order + "&sort=" + sort;
            } else {
                url += "index.html?start=" + start + "&frontpage=" + frontpage + "&size=" + l[i] + "&order=" + order + "&sort=" + sort;
            }
        }
        rowHTML += "<option value='" + url + "'>" + l[i] + "</option>";
        frontsize.append(rowHTML)
    }

    let titlesort = jQuery("#titlesort");
    l = ["Asc1Asc2", "Asc1Desc2", "Desc1Asc2", "Desc1Desc2"]
    let l2 = ["title asc, rating asc", "title asc, rating desc", "title desc, rating asc", "title desc, rating desc"]
    for (let i = 0; i < l.length; i++) {
        let rowHTML = "";
        let url = "";
        if (!genre && !start) {
            url += "index.html?title=" + title + "&year=" + year + "&director=" + director + "&actor=" + actor + "&frontpage=" + frontpage + "&size=" + size + "&order=" + l[i] + "&sort=title";
        } else {
            if (!start) {
                url += "index.html?genre=" + genre + "&frontpage=" + frontpage + "&size=" + size + "&order=" + l[i] + "&sort=title";
            } else {
                url += "index.html?start=" + start + "&frontpage=" + frontpage + "&size=" + size + "&order=" + l[i] + "&sort=title";
            }
        }
        rowHTML += "<option value='" + url + "'>" + l2[i] + "</option>";
        titlesort.append(rowHTML)
    }

    let ratingsort = jQuery("#ratingsort");
    l = ["Asc1Asc2", "Asc1Desc2", "Desc1Asc2", "Desc1Desc2"]
    l2 = ["rating asc, title asc", "rating asc, title desc", "rating desc, title asc", "rating desc, title desc"]
    for (let i = 0; i < l.length; i++) {
        let rowHTML = "";
        let url = "";
        if (!genre && !start) {
            url += "index.html?title=" + title + "&year=" + year + "&director=" + director + "&actor=" + actor + "&frontpage=" + frontpage + "&size=" + size + "&order=" + l[i] + "&sort=rating";
        } else {
            if (!start) {
                url += "index.html?genre=" + genre + "&frontpage=" + frontpage + "&size=" + size + "&order=" + l[i] + "&sort=rating";
            } else {
                url += "index.html?start=" + start + "&frontpage=" + frontpage + "&size=" + size + "&order=" + l[i] + "&sort=rating";
            }
        }
        rowHTML += "<option value='" + url + "'>" + l2[i] + "</option>";
        ratingsort.append(rowHTML)
    }


}

function handleItem(info) {
    window.alert("Add to cart successfully!")
    console.log("submit cart form");
    let arr = info.split(",")
    console.log(arr)
    let ti = arr[0]
    let type = arr[1]
    let id = arr[2]
    console.log(ti + "     " + type)

    $.ajax("api/movielist", {
        method: "post",
        data: {title: ti, type: type, id: id}
    });

}


if (!genre && !start) {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/movielist?title=" + title + "&year=" + year + "&director=" + director + "&actor=" + actor + "&frontpage=" + frontpage + "&size=" + size + "&order=" + order + "&sort=" + sort,
        success: (resultData) => handleMovieResult(resultData)
    });
} else {
    if (!start) {
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/movielist?genre=" + genre + "&frontpage=" + frontpage + "&size=" + size + "&order=" + order + "&sort=" + sort,
            success: (resultData) => handleMovieResult(resultData)
        });
    } else {
        jQuery.ajax({
            dataType: "json",
            method: "GET",
            url: "api/movielist?start=" + start + "&frontpage=" + frontpage + "&size=" + size + "&order=" + order + "&sort=" + sort,
            success: (resultData) => handleMovieResult(resultData)
        });
    }
}