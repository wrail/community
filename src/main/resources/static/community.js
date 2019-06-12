// function post() {
//     var val = $(" #question_id ").val();
//     var content = $("#comment_content").val();
//     $.ajax({
//         type: "POST",
//         url: "/comment",
//         contentType: 'application/json',
//         data: JSON.stringify({
//             "parentId":44,
//             "content": content,
//             "type": 1
//         }),
//         success: function (response){
//             if (response.code==200){
//                 $("#comment_section").hidden
//             }else {
//                 alert(response.message)
//             }
//             console.log(response)
//         },
//         dataType:"json"
//     });
//
// }
/**
 * 提交回复
 */
function post() {
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    comment2target(questionId, 1, content);
}

function comment2target(targetId, type, content) {
    if (!content) {
        alert("不能回复空内容~~~");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=70e24ea8682a92b4239f&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    }
                } else {
                    alert(response.message);
                }
            }
        },
        dataType: "json"
    });
}