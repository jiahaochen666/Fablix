let item_list2 = $("#item_list2");


function handleItemInfo(info) {
    console.log("submit cart form");
    let arr = info.split(",")
    let ti = arr[0]
    let type = arr[1]
    let id = arr[2]
    console.log(ti + "     " + type)

    $.ajax("api/cart", {
        dataType: "json",
        method: "post",
        data: {title: ti, type: type, id: id},
        success: handleCartArray
    });
}

function handleCartArray(resultDataString) {
    let res = "";
    if (resultDataString.length !== 0) {

        // const resultArray = resultDataString.substring(1,resultDataString.length-1).split(",");
        // change it to html list
        for (let i = 0; i < resultDataString.length; i++) {
            // each item will be in a bullet point
            console.log(resultDataString[i])
            res += "<tr><th>" + resultDataString[i]["id"] + "</th><th>" + resultDataString[i]["title"] + "</th><th>" + resultDataString[i]["num"] + "</th><th>" + resultDataString[i]["price"] + "</th><th style='width: 30%'>";

            res += "<a href= # onclick='handleItemInfo(\"" + resultDataString[i]["title"] + "," + 'add,' + resultDataString[i]["id"] + "\")'>Add one more</a>"
            res += "<a href= # onclick='handleItemInfo(\"" + resultDataString[i]["title"] + "," + "del," + resultDataString[i]["id"] + "\")'>Delete one</a>"
            res += "<a href= # onclick='handleItemInfo(\"" + resultDataString[i]["title"] + "," + "rem," + resultDataString[i]["id"] + "\")'>Remove all</a>"

            res += "</th></tr>";

        }
    }
    // clear the old array and show the new array in the frontend
    item_list2.html("");
    item_list2.append(res);

}

$.ajax("api/cart", {
    dataType: "json",
    method: "get",
    success: handleCartArray
})



