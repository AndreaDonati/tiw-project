/* Navbar Animation onScroll */
$(window).scroll(function(){
    if($(document).scrollTop() > 80){
      $('nav').addClass('animate');
      $('h4').addClass('animate-side');
      $('img').addClass('animate-img');
    }else{
      $('nav').removeClass('animate');
      $('h4').removeClass('animate-side');
      $('img').removeClass('animate-img');
    }
  });
