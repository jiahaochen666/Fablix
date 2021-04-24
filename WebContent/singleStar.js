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

function handleSingleStarResult(resultData) {


    console.log("handleSingleMovieResult: populating single movie table from resultData");


    let singlemovieTableBodyElement = jQuery("#star_table_body");


    let rowHTML = "";
    rowHTML += "<tr>";
    rowHTML += "<th>" + resultData[0]["name"] + "</th>";

    rowHTML += "<th>";
    for (let i = 0; i < resultData[1].length; i++) {
        rowHTML += "<p>" + '<a href="singlemovie.html?movieid=' + resultData[1][i]["movieId"] + '">' + resultData[1][i]["title"] + '</a>' + "</p>";
    }
    rowHTML += "</th>";

    rowHTML += "<th>" + resultData[0]["birth"] + "</th>";


    rowHTML += "</tr>";
    singlemovieTableBodyElement.append(rowHTML);

    var x;
    let back = jQuery("#back");
    rowHTML = "<a href='index.html?";
    for (x in resultData[2]) {
        rowHTML += x + "=" + resultData[2][x] + "&";
    }
    rowHTML = rowHTML.slice(0, rowHTML.length - 1);
    rowHTML += "'>" + "Back to home</a>";
    back.append(rowHTML);
}

let starId = getParameterByName('starid');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/singleStar?starid=" + starId,
    success: (resultData) => handleSingleStarResult(resultData)
});