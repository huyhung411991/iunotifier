// Use Parse.Cloud.define to define as many cloud functions as you want.
Parse.Cloud.afterSave("News", function(request) {
    console.log("afterSave(News) was called");
    var newsID = request.object.get("newsID");
    var newsTitle = request.object.get("newsTitle");
    var newsLink = request.object.get("newsLink");
    if (newsID !== undefined && newsTitle !== undefined && newsLink !== undefined
            && newsTitle.length > 0 && newsLink.length > 0) {
        var notificationContent = "Latest news: " + newsTitle;
        Parse.Push.send({
            channels: ["news"],
            data: {
                alert: notificationContent
            }
        }, {
            success: function() {
                // Push was successful
                console.log("Push successfully");
            },
            error: function(error) {
                // Handle error
                console.log("Push failed: " + error);
            }
        });
    }
});
 
Parse.Cloud.afterSave("Events", function(request) {
    console.log("afterSave(Event) was called");
    var eventID = request.object.get("eventID");
    var eventTitle = request.object.get("eventTitle");
    var eventDescription = request.object.get("eventDescription");
    var eventDate = request.object.get("eventDate");
    var eventPlace = request.object.get("eventPlace");
    if (eventID !== undefined && eventTitle !== undefined && eventDescription !== undefined
            && eventDate !== undefined && eventPlace !== undefined && eventTitle.length > 0
            && eventDescription.length > 0 && eventPlace.length > 0) {
        var notificationContent = "Latest event: " + eventTitle;
        Parse.Push.send({
            channels: ["events"],
            data: {
                alert: notificationContent
            }
        }, {
            success: function() {
                // Push was successful
                console.log("Push successfully");
            },
            error: function(error) {
                console.log("Push failed: " + error);
            }
        });
    }
});
 
Parse.Cloud.define("makeCourseAnnouncement", function(request, response) {
    var message = request.params.message;
    Parse.Push.send({
        channels: ["CourseAnnouncement"],
        data: {
            alert: message
        }
    }, {
        success: function() {
            // Push was successful
            console.log("Push successfully");
        },
        error: function(error) {
            // Handle error
            console.log("Push failed: " + error);
        }
    });
    response.success("Complete Announcement");
});
 
Parse.Cloud.define("checkNews", function(request, response) {
    console.log(request.params);
    var query = new Parse.Query("News");
    query.descending("createdAt");
    query.find({
        success: function(results) {
            var obsolete = "false";
            var createdTime = results[0].get("createdAt");
            var hourOnDb = createdTime.getHours();
            var dateOnDb = createdTime.getDate();
            var monthOnDb = createdTime.getMonth();
            var yearOnDb = createdTime.getFullYear();
            var hourOnDevice = request.params.hour;
            var dateOnDevice = request.params.date;
            var monthOnDevice = request.params.month;
            var yearOnDevice = request.params.year;
 
            if (yearOnDevice < yearOnDb)
                obsolete = "true";
            else if (monthOnDevice < monthOnDb)
                obsolete = "true";
            else if (dateOnDevice < dateOnDb)
                obsolete = "true";
            else if (hourOnDevice < hourOnDb)
                obsolete = "true";
 
            if (obsolete == "true")
                response.success(obsolete);
        },
        error: function() {
            response.error("error");
        }
    });
});
 
Parse.Cloud.define("getNewsList", function(request, response) {
    var query = new Parse.Query("News");
    query.descending("newsID");
    query.find({
        success: function(results) {
            var newsList = new Array();
            for (var i = 0; i < results.length; i++) {
                var news = new Parse.Object();
                news.add("newsID", resuilt[i].get("newsID"));
                news.add("newsTitle"), resuilt[i].get("newsTitle");
                newsList[i] = news;
            }
            response.success(newsList);
        },
        error: function() {
            response.error("NewsList not found");
        }
    });
});