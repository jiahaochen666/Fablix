function handleBrowseResult(resultArray) {
    let genre_list = $("#genre_list");
    let res = "";
    for (let i = 0; i < resultArray.length; i++) {

        res += "<a href='index.html?genre=" + resultArray[i] + "&frontpage=0&size=25&sort=rating&order=Desc1Desc2'><b>" + resultArray[i] + "</b></a>";
    }
    genre_list.html("");
    genre_list.append(res);

    let letter = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "*"];
    let start = $("#start");
    res = "";
    for (let i = 0; i < letter.length; i++) {
        res += "<a href='index.html?start=" + letter[i] + "&frontpage=0&size=25&sort=rating&order=Desc1Desc2'><b>" + letter[i] + "</b></a>";
    }
    start.html("")
    start.append(res);
}

$.ajax("api/main", {
    dataType: "json",
    method: "get",
    url: "api/main",
    success: (resultData) => handleBrowseResult(resultData)
});

let search = $("#search");


function handleSearchResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);
    let url = "";
    url += "index.html?title=" + resultDataJson["title"] + "&year=" + resultDataJson["year"] + "&director="
        + resultDataJson["director"] + "&actor=" + resultDataJson["actor"] + "&frontpage=0&size=50&sort=rating&order=Desc1Desc2";
    window.location.replace(url);
}

function submitSearchForm(formSubmitEvent) {
    console.log("submit Search form");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/main", {
            method: "post",
            data: search.serialize(),
            success: handleSearchResult
        }
    );
}

function handleLookupAjaxSuccess(data, query, doneCallback) {
    console.log("lookup ajax successful")

    // parse the string into JSON
    var jsonData = JSON.parse(data);
    console.log(jsonData)

    // TODO: if you want to cache the result into a global variable you can do it here
    searched[query] = jsonData;
    // call the callback function provided by the autocomplete library
    // add "{suggestions: jsonData}" to satisfy the library response format according to
    //   the "Response Format" section in documentation
    doneCallback({suggestions: jsonData});
}


function handleLookup(query, doneCallback) {
    console.log("autocomplete initiated")
    console.log("sending AJAX request to backend Java Servlet")
    console.log(111111111111111 + query);
    console.log(2222222222222222222 + doneCallback);

    // TODO: if you want to check past query results first, you can do it here

    // sending the HTTP GET request to the Java Servlet endpoint hero-suggestion
    // with the query data
    jQuery.ajax({
        "method": "GET",
        // generate the request url from the query.
        // escape the query string to avoid errors caused by special characters
        "url": "auto-suggestion?query=" + escape(query),
        "success": function (data) {
            // pass the data, query, and doneCallback function into the success handler
            handleLookupAjaxSuccess(data, query, doneCallback)
        },
        "error": function (errorData) {
            console.log("lookup ajax error")
            console.log(errorData)
        }
    })
}

var searched = {}

function handleSelectSuggestion(suggestion) {
    // TODO: jump to the specific result page based on the selected suggestion

    console.log("xxxxxxxxxxxx" + suggestion["title"]);


    // console.log("you select " + suggestion["value"] + " with ID " + suggestion["data"]["heroID"])
    let url = "";
    url += "singlemovie.html?movieid=" + suggestion["data"]["heroID"];
    console.log("URL is : " + url);
    window.location.href = url;
}

$('#autoTitle').autocomplete({
    // documentation of the lookup function can be found under the "Custom lookup function" section
    minChars: 3,
    triggerSelectOnValidInput: false,
    lookup: function (query, doneCallback) {
        if (query in searched) {
            console.log("searching cached query from front-end.")
            doneCallback({"suggestions": searched[query]})
            return;
        }
        handleLookup(query, doneCallback)
    },
    onSelect: function (suggestion) {
        handleSelectSuggestion(suggestion)
    },
    // set delay time
    deferRequestBy: 300,

    // there are some other parameters that you might want to use to satisfy all the requirements
    // TODO: add other parameters, such as minimum characters
});

search.submit(submitSearchForm);
