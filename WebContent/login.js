let login_form = $("#login_form");


function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "customer") {
        window.location.replace("main.html");
    } else {
        if (resultDataJson["status"] === "administor") {
            window.location.replace("_dashboard.html");
        } else {
            console.log("show error message");
            console.log(resultDataJson["message"]);
            $("#login_error_message").text(resultDataJson["message"]);
        }
    }
}


function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");

    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "POST",
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

login_form.submit(submitLoginForm);