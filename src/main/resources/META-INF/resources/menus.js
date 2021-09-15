function refresh() {
    $.get('/menu/calendar_start', function (calendar_start) {
        var list = calendar_start.start;
        $('#new-start-date').val(list);
    });
    $.get('/menu', function (fruits) {
        var list = '';
        (fruits || []).forEach(function (fruit) {
            list = list
                + '<tr>'
                + '<td>' + fruit.day + '</td>'
                + '<td>' + fruit.name + '</td>'
                + '<td><a href="#" onclick="deleteFruit(' + fruit.day + ')">Delete</a></td>'
                + '</tr>'
        });
        if (list.length > 0) {
            list = ''
                + '<table><thead><th>Id</th><th>Name</th><th></th></thead>'
                + list
                + '</table>';
        } else {
            list = "No fruits in database"
        }
        $('#dinner-menu').html(list);
    });
}

function confirm() {
 alert('submitted');
}

function deleteFruit(id) {
    $.ajax('/menu/' + id, {method: 'DELETE'}).then(refresh);
}

$(document).ready(function () {

    $('#submit-start-date').click(function () {
        var startDate = $('#new-start-date').val();
        $.post({
            url: '/menu',
            contentType: 'application/json',
            data: JSON.stringify({start: startDate})
        }).then(alert('Start Date Updated to ' + startDate)).then(refresh);
    });

    $('#create-dinner-main').click(function () {
        var dinnerMain = $('#dinner-main').val();
        $.post({
            url: '/menu/item',
            contentType: 'application/json',
            data: JSON.stringify({name: dinnerMain, active: "false"})
        }).then(alert('Submitted new main "' + dinnerMain + "'")).then(refresh);
    });

    refresh();
});