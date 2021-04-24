$.ajax("api/confirm", {
    dataType: "json",
    method: "get",
    success: handleFinalResult
});

function handleFinalResult(result) {
    console.log(result)
    let detail = $("#detail")
    let res = "";
    for (let i = 0; i < result.length; i++) {
        res += "<tr><th>" + result[i]["id"] + "</th><th>" + result[i]["title"] + "</th><th>" + result[i]["num"] +
            "</th><th>" + result[i]["saleId"] + "</th><th>" + result[i]["price"];
        res += "</th></tr>";

    }
    detail.html("");
    detail.append(res);

}