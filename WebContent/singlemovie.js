let movieId = getParameterByName('movieid');

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

function handleSingleMovieResult(resultData) {
    console.log("handleSingleMovieResult: populating single movie table from resultData");
    console.log(resultData.toString())

    let singlemovieTableBodyElement = jQuery("#movie_table_body");

    let rowHTML = "";

    rowHTML += "<tr id = moiveId>";
    rowHTML += "<th id = title>" + resultData[0]["title"] + "</th>";
    rowHTML += "<th id = year>" + resultData[0]["year"] + "</th>";
    rowHTML += "<th id = director>" + resultData[0]["director"] + "</th>";
    rowHTML += "<th>";
    console.log(resultData)
    for (let i = 0; i < resultData[1].length; i++) {
        rowHTML += "<p>" + '<a href="singleStar.html?starid=' + resultData[1][i]["id"] + '">' + resultData[1][i]["name"] + '</a>' + "</p>";
    }
    rowHTML += "</th>";
    rowHTML += "<th>";
    for (let i = 0; i < resultData[2].length; i++) {
        rowHTML += "<p>" + '<a href="index.html?genre=' + resultData[2][i] + '&frontpage=0&size=25&sort=rating&order=Desc1Desc2">' + resultData[2][i] + '</a>' + "</p>";
    }
    rowHTML += "</th>";
    rowHTML += "<th>" + resultData[0]["rating"] + "</th>";


    // rowHTML += "<input name=\"item\" required=\"required\" type=\"text\">"
    rowHTML += "<th>"
    rowHTML += "<p> <a href= # onclick=' handleItem(\"" + resultData[0]["title"] + ",add," + movieId + "\")' >Add to cart</a> </p>"
    rowHTML += "</th>"
    rowHTML += "</tr>";
    singlemovieTableBodyElement.append(rowHTML);

    var x;
    let back = jQuery("#back");
    rowHTML = "<a href='index.html?";
    for (x in resultData[3]) {
        rowHTML += x + "=" + resultData[3][x] + "&";
    }
    rowHTML = rowHTML.slice(0, rowHTML.length - 1);
    rowHTML += "'>" + "Back to home</a>";
    back.append(rowHTML);
}


jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/singlemovie?movieid=" + movieId,
    success: (resultData) => handleSingleMovieResult(resultData)
});


function handleItem(info) {
    window.alert("Add to cart successfully!")

    console.log("submit cart form");
    let arr = info.split(",")
    console.log(arr)
    let ti = arr[0]
    let type = arr[1]
    let id = arr[2]
    console.log(ti + "     " + type)

    $.ajax("api/singlemovie", {
        method: "post",
        data: {title: ti, type: type, id: id}
    });

}
