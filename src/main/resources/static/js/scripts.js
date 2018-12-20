$(".submit-write button[type=button]").click(addAnswer);

function addAnswer(e) {
    e.preventDefault(); //submit 이 자동으로 동작하는 것을 막는다.

    var queryString = $(".submit-write").serialize(); //form data들을 자동으로 묶어준다.
    console.log("query : "+ queryString);
}

String.prototype.format = function() {
  var args = arguments;
  return this.replace(/{(\d+)}/g, function(match, number) {
    return typeof args[number] != 'undefined'
        ? args[number]
        : match
        ;
  });
};