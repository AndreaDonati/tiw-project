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

var request;

function init() {
// Rimpiazza la gestione del browser
// chiamata a servlet che risponde con json dei corsi
	console.log("Chiamo init");
	makeGet(showCorsi); // Chiamata asincrona
//	makeGet(riempiSidebar);
}

function makeGet(callback) {	
	// Richiesta asincrona non cambia la pagina
	request = new XMLHttpRequest(); // Nuova richiesta
	var url = 'getCorsi'; // URL della Servlet

	request.onreadystatechange = callback; // Chiamata al callback
	request.open("GET", url); // Richiesta POST all'URL
	request.send();
}


function showCorsi() {
    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) reindirizzo l'utente alla home (mandata con un sendRedirect dalla Servlet)
	if (request.readyState == 4 && request.status == 200 ) {
		corsi = JSON.parse(request.responseText);
		$("#content").append('<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true"></div>');
		for (i=0; i<corsi.length; i++){
			$("#accordion").append('<div class="panel panel-default">'+
                        '<div class="panel-heading" role="tab" id="headingOne">'+
                           '<h4 class="panel-title">'+
                                '<a data-parent="#accordion" onclick="getEsami(`'+corsi[i].nome+'`)" >'+corsi[i].nome+'</a>'+
                            '</h4>'+
                        '</div>'+
                    '</div>');
		}
		
		
		console.log(JSON.parse(request.responseText));
	}
}

function getEsami(nomeCorso) {
	console.log(nomeCorso);
}
