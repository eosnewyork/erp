jQuery(document).ready(function() {
    "use strict";
    $('#submit').on('click', function() {

        var action = $('#contactform').attr('action');

        $("#message").fadeOut(200, function() {
            $('#message').hide();

            $('#submit')
                .attr('disabled', 'disabled');

            $.post(action, {
                    name: $('#name').val(),
                    email: $('#email').val(),
                    phone: $('#phone').val(),
                    comments: $('#comments').val()
                },
                function(data) {
                    document.getElementById('message').innerHTML = data;
                    $('#message').fadeIn(200);
                    $('.hide').hide(0);
                    $('#submit').removeAttr('disabled');

                }
            );

        });

        return false;

    });

});