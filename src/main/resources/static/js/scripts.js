$(".answer-write input[type='submit']").on("click", addAnswer);

function addAnswer(e) {
  e.preventDefault();

  var queryString = $(".answer-area").val();
  var url = $(".answer-write").attr("action");

  console.log("queryString " + queryString);
  console.log(JSON.stringify(queryString));
  console.log("url " + url);

  $.ajax({
    type: 'post',
    url: url,
    contentType: 'application/json',
    data: '{"contents":' + JSON.stringify(queryString) + '}',
    dataType: 'json',
    error: onError,
    success: onSuccess
  });
}

function onError(error) {
  console.warn("error" + error.status);
}

function onSuccess(data, status) {
  var answerTemplate = $("#answerTemplate").html();
  var template = answerTemplate.format(data.writer.userId, data.formattedCreateDate, data.contents, data.question.id, data.id);
  $(".qna-comment-slipp-articles").append(template);

  $("textarea[name=contents]").val("");
}

$(".qna-comment-slipp-articles").on("click", "a.link-delete-article", deleteAnswer);

function deleteAnswer(e) {
  e.preventDefault();

  var deleteBtn = $(this);

  var url = $(this).attr("href");

  $.ajax({
    type: 'delete',
    url: url,
    dataType: 'json',
    error: function (xhr, status) {
      console.log("error");
    },
    success: function (data, status) {
      if (data.valid) {
        deleteBtn.closest("article").remove();
      } else {
        alert(data.errorMessage);
      }
    }
  })
}

String.prototype.format = function () {
  var args = arguments;
  return this.replace(/{(\d+)}/g, function (match, number) {
    return typeof args[number] != 'undefined'
        ? args[number]
        : match
        ;
  });
};