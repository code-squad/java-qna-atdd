String.prototype.format = function () {
    var args = arguments;
    return this.replace(/{(\d+)}/g, function (match, number) {
        return typeof args[number] != 'undefined'
            ? args[number]
            : match
            ;
    });
};

if (document.querySelector(".submit-write")) {
    document.querySelector(".submit-write button").addEventListener('click', addAnswer, true);
}

function addAnswer() {
    var form = document.getElementById('submit-answer');
    var req = new XMLHttpRequest();
    req.addEventListener('load', function() {
        writeHandle(req);
    }, false);
    req.open(form.method, form.action);
    req.setRequestHeader('Accept', 'application/json');
    req.setRequestHeader('Content-Type', 'application/json');
    req.send(JSON.stringify({"content" : document.getElementById('answer-editor').value}));
}

function writeHandle(req) {
    if (req.status != 201) {
        alert('답변을 작성할 수 없습니다.');
        return;
    }

    var data = JSON.parse(req.responseText);
    var template = document.getElementById('answerTemplate').innerHTML.format(data.question.id, data.id, data.writer.userId, data.contents, data.createDate);
    var contents = document.createElement('div');
    contents.innerHTML = template;
    contents = contents.firstElementChild;
    document.querySelector('.qna-comment-slipp-articles').prepend(contents);
    document.getElementById('answer-editor').value = '';
}
