$(document).ready(function(){
    $('.sidenav').sidenav();
});
let slider = $('.slider');
let nav=$('.navigator');
M.Slider.init(slider,{
    indicators: false,
    height: 650 ,
    transition: 500,
    interval: 6000
});

$(document).ready(function(){
    $('.carousel').carousel({
        indicators: true,
        fullWidth: true
    });

    setInterval(function () {
        $('.carousel').carousel('next');
    }, 5000);
});

$('.dropdown-trigger').dropdown();

$(document).ready(function(){
    $("#check").change(function(){
        if($(this).is(':checked')){
            $("#password").attr("type","text");
        }else{
            // Changing type attribute
            $("#password").attr("type","password");
        }
    });
});

$(document).ready(function(){
    $("#check1").change(function(){
        if($(this).is(':checked')){
            $("#newpassword").attr("type","text");
        }else{
            // Changing type attribute
            $("#newpassword").attr("type","password");
        }
    });
});
$(document).ready(function(){
    $('.collapsible').collapsible();
});

const newp=$("#newp");
const confirm=$("#confirmp");

$(document).ready(function(){
    $("#newp").change(function(){
        if($("#newp").val().length<8){
            this.setCustomValidity("Password length must be no less than 8!");
        }
        else{
            this.setCustomValidity("");
        }
    });

    $("#confirmp").keyup(function(){
        if($("#confirmp").val()!=$("#newp").val()){
            this.setCustomValidity("Password doesnt match!");
        }
        else{
            this.setCustomValidity("");
        }
    });
});



// $("#newp").change(passwordMatch);
// $("#newp").keyup(passwordLength);
// $("#confirmp").keyup(passwordMatch);



// const login = $(".modal");
// M.Modal.init(login,{
//     opacity:0.7
// });



