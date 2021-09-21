function refresh() {
    $.get('/menu/calendar_start', function (calendar_start) {
        var list = calendar_start.start;
        $('#new-start-date').val(list);
    });

    //$('#dinner-menu').html('<tr><td>test</td></tr>');

    $.get('/menu/dinner', function (fruits) {
        menumenu(fruits, '#dinner-menu')
    });
    $.get('/menu/lunch', function (fruits) {
        menumenu(fruits, '#lunch-menu')
    });
}

function menumenu(fruits, table){
	var list = '';
    (fruits || []).forEach(function (fruit) {
        list = list + `<tr>
               <td>${fruit.day}</td>
               <td>${fruit.optionId}</td>
               <td><input size=100 value="${fruit.name}" /></td>
           </tr>`
    });
    for(var i = fruits.length; i < 4; i++){
        list = list + `<tr>
               <td>${i+1}</td>
               <td><input size=100/></td>
           </tr>`
    }
    if (list.length > 0) {
        list = ''
            + '<tr><th>Day</th><th>Name</th></tr>'
            + list
            + '';
    } else {
        list = "No fruits in database"
    }
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
            
    for (var i = 1, row; row = menu.rows[i]; i++) {
        //iterate through rows
        //rows would be accessed using the "row" variable assigned in the for loop
        var main = row.cells[1].querySelector('input').value;
        data.push({day: i, name: main + ""});
    }
    $.post({
        url: url,
        contentType: 'application/json',
        data: JSON.stringify(data),
        success: function (data) {
           alert('Success!')
        }
    }).then(alert('Submitted new main ' + data)).then(refresh);
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
