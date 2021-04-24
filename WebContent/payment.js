let payment = $("#payment")
payment.submit(handlePaymentResult)

$.ajax(
    "api/payment", {
        method: "get",
        success: generatePrice
    }
);

function generatePrice(resultData) {
    let resultDataJson = JSON.parse(resultData);

    console.log("handle price response");
    console.log(resultDataJson);
    console.log(resultDataJson["price"])

    let items = $("#items")
    console.log(items + "1111111111111111111111111")
    items.text("Total price: " + resultDataJson["price"])
    console.log(items)

}

function handlePaymentResult(formSubmitEvent) {
    console.log("submit Search form");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/payment", {
            method: "post",
            data: payment.serialize(),
            success: handleResult
        }
    );

}

function handleResult(resultData) {
    let resultDataJson = JSON.parse(resultData);

    console.log("handle payment response");
    console.log(resultDataJson);
    console.log(resultDataJson["message"]);
    console.log(resultDataJson["price"])


    let message;
    if (resultDataJson["message"] === "success") {
        window.location.replace("confirm.html");
    } else {
        console.log("show error message");
        console.log(resultDataJson["message"]);
        message = $("#message");
        console.log(message)
        message.text(resultDataJson["message"]);
    }


}