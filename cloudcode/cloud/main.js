
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});
Parse.Cloud.afterSave ("Pin",function (request) { // name of my parse class is "UserVideoMessage"

    Parse.Push.send ({
        //Selecting the already existing Push Channel
        channels: [""], //This has to be the name of your push channel!!
        data: {
            //Selecting the Key inside the Class, this will be the content of the push notification
            action: "com.yahoo.americancurry.petpeeve.custom", 
            objectId: request.object.id

        }
    }, {
        success: function () {
            //Push was sent successfully
            //nothing was loged
        },
        error: function (error) {
            throw "Got and error" + error.code + " : " + error.message;
        }
    });

});
