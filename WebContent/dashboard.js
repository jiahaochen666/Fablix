let database_table = $("#database_table");
function handleDashboardDatabaseTable(result) {
    let html = "";

    for (let i = 0; i < result.length; i++) {
        html += "<table><tr style='color: chartreuse; font-size: 20px;'>" + result[i][0]["table_name"]
            + "</tr><thead><tr><th>Field</th><th>Type</th><th>Null</th><th>Key</th><th>Extra</th></tr></thead><tbody>";
        for (let j = 0; j < result[i].length; j++) {
            html += "<tr><th>" + result[i][j]["field"] + "</th><th>" + result[i][j]["type"] + "</th><th>"
                + result[i][j]["null"] + "</th><th>" + result[i][j]["key"] + "</th><th>" + result[i][j]["extra"]
                + "</th></tr>"
        }
        html += "</tbody></table><br>";
    }

    database_table.append(html);
}

$.ajax("api/dashboard",{
    dataType: "json",
    method: "GET",
    success: handleDashboardDatabaseTable
});

let addstar = $("#addstar");

function handleAddResult(result) {
    $("#add_message").text(result["message"]);
}

function submitAddForm(formSubmitEvent) {
    console.log("submit add star form");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/dashboard", {
            method: "post",
            data: addstar.serialize(),
            success: handleAddResult
        }
    );
}

addstar.submit(submitAddForm);

let addmovie = $("#addmovie");

function handleAddMovieResult(result) {
    $("#addmovie_message").text(result["message"]);
}

function submitAddMovieForm(formSubmitEvent) {
    console.log("submit add movie form");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/dashboard_addmovie", {
            method: "post",
            data: addmovie.serialize(),
            success: handleAddMovieResult
        }
    );
}

addmovie.submit(submitAddMovieForm);
