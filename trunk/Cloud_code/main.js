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
                console.log("Push successful");
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
                console.log("Push successful");
            },
            error: function(error) {
                console.log("Push failed: " + error);
            }
        });
    }
});

Parse.Cloud.define("makeCourseAnnouncement", function(request, response) {
    console.log("makeCourseAnnouncement was called");
    var message = request.params.message;
    Parse.Push.send({
        channels: ["course"],
        data: {
            alert: message
        }
    }, {
        success: function() {
            // Push was successful
            console.log("Push successful");
        },
        error: function(error) {
            // Handle error
            console.log("Push failed: " + error);
        }
    });
    response.success("Complete Announcement");
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

Parse.Cloud.define("getUserCourses", function(request, response) {
    console.log("getUserCourses was called");

    var parseUser = request.user;
    var username = parseUser.getUsername();
    if (username !== undefined && username.length > 0) {
        var userQuery = new Parse.Query("User");
        userQuery.equalTo("username", username);
        userQuery.find({
            success: function(results_1) {
                var courseIDList = results_1[0].get("courses");
                var coursesQuery = new Parse.Query("Courses");
                coursesQuery.containedIn("courseID", courseIDList);
                coursesQuery.find({
                    success: function(results_2) {
                        var courseList = new Array();
                        for (var i = 0; i < results_2.length; i++) {
                            var courseID = results_2[i].get("courseID");
                            var courseName = results_2[i].get("courseName");
                            courseList[i] = {"courseID": courseID, "courseName": courseName}
                        }
                        console.log("Query successful with " + results_2.length + " items");
                        response.success(courseList);
                    },
                    error: function() {
                        console.log("Query failed at step 2");
                        response.error("userCourses lookup failed");
                    }
                });
            },
            error: function() {
                console.log("Query failed at step 1");
                response.error("userCourses lookup failed");
            }
        });
    }
});