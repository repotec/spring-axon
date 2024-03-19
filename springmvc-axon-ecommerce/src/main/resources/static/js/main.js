$('document').ready(function(){
    $('.table #btn1').on('click', function(event){
        event.preventDefault();
        const href = $(this).attr('href');
        console.log('href:' + href);
        $('#orderRef').attr('href', href);
        $('#orderModal').modal("show");
    });

    $('#orderRef').on('click', function(event){
        console.log('test');
        event.preventDefault();
        const href = $(this).attr('href');

        $.ajax({
             type: "POST",
             url: href,
             contentType: "application/json; charset=utf-8",
             success: function (data, status, jqXHR) {
                 alert("success");// write success in " "
             },
             error: function (jqXHR, status) {
                 console.log(jqXHR);
                 alert('fail' + status.code);
             }
          });
    });

});