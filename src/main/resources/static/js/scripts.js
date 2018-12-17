String.prototype.format = function() {
  var args = arguments;
  return this.replace(/{(\d+)}/g, function(match, number) {
    return typeof args[number] != 'undefined'
        ? args[number]
        : match
        ;
  });
};

$("#question > button[type=submit]").click(submitQuestion);

function submitQuestion(e) {
    e.preventDefault();
    console.log("Call submitQuestion Method()");

    var queryString = $("#question").serialize();
    var url = '/api/questions';

    $.ajax({
        type : 'post',
        url : url,
        data : queryString,
        dataType : 'json',
        error : function (xhr) {
            console.log("질문하기 실패!");
            console.log(xhr);
        },
        success : function (data, status, xhr) {
            console.log("질문하기 성공!");
            console.log(xhr.getAllResponseHeaders());
            location.href = xhr.getResponseHeader('Location');
        }
    });
}