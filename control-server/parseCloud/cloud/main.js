
// Use Parse.Cloud.define to define as many cloud functions as you want.
// For example:
Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});


Parse.Cloud.define("update", function(request, response) {
  Parse.initialize("HIWZgDpELVc7HanpltUv1EtSPGF5eBGJBj6QGrVS", "EYPUl3kXlxlRasXmX2JdBNNi4IK2VdwFmwLjqGDi");
  var query = new Parse.Query(Parse.Installation);

  Parse.Push.send({
    where: query, // Set our Installation query
    data: {
      alert: "Broadcast to: "+request.params.userid+" m: "+request.params.message
    }
  }, {
    success: function() {
      // Push was successful
    },
    error: function(error) {
      // Handle error
    }
  });
});
