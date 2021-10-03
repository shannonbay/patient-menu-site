function refresh() {
    $.get('/menu/calendar_start', function (calendar_start) {
        var list = calendar_start.start;
        $('#new-start-date').val(list);
    });

    //$('#dinner-menu').html('<tr><td>test</td></tr>');

    var day = $('#day').val();
    $.get('/menu/dinner/' + day, function (fruits) {
        menumenu(fruits, '#dinner-menu')
    });
    $.get('/menu/lunch/' + day, function (fruits) {
        menumenu(fruits, '#lunch-menu')
    });
}

function menumenu(fruits, table){
	var list = '';
	var menu = ["", "", "", ""];
    (fruits || []).forEach(function (fruit) {
         menu[fruit.optionId] = fruit.name
    });
    for(var i = 0; i < 4; i++) {
            list = list + `<tr>
               <td>${i + 1}</td>
               <td><input size=100 value="${menu[i]}" /></td>
            </tr>`
    }
    list = ''
        + '<tr><th>Option</th><th>Name</th></tr>'
        + list
        + '';
    $(table).html(list);
}

function confirm() {
 alert('submitted');
}

function deleteFruit(id) {
    $.ajax('/menu/' + id, {method: 'DELETE'}).then(refresh);
}

function submitMenu(table, url) {
	var menu = document.getElementById(table);
    var data = [];
            
    var day = $('#day').val();
    for (var i = 1, row; row = menu.rows[i]; i++) {
        var main = row.cells[1].querySelector('input').value;
        data.push({menu: 'lunch', day: day, optionId: row.cells[0].innerText - 1, name: main + ""});
    }
    $.post({
        url: url,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (data) {
           alert('Success!')
        }
    }).then(alert('Submitted new main ' + JSON.stringify(data))).then(refresh);
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
    $('#submit-lunch-menu').click(function() { submitMenu('lunch-menu', '/menu/lunch') });
    $('#submit-dinner-menu').click(function() { submitMenu('dinner-menu', '/menu/dinner') });
			
    refresh();        
});
