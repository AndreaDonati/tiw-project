/* Animazione Navbar on Scroll */
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

/* Funzione per mandare richieste GET */
function makeGet(servletUrl, callback) {	
	// Richiesta asincrona non cambia la pagina
	request = new XMLHttpRequest(); // Nuova richiesta
	// var url = 'getCorsi'; // URL della Servlet

	request.onreadystatechange = callback; // Chiamata al callback
	request.open("GET", servletUrl); // Richiesta POST all'URL
	request.send();
}

var request;
var loaderDiv;

function init() {
	loaderDiv = document.getElementById("loader");
// Rimpiazza la gestione del browser
// chiamata a servlet che risponde con json dei corsi
	console.log("Chiamo init");
	// QUI INIZIO LOADER
	loaderDiv.style.display = "block";

	// QUI CHIAMATA PER PRENDERE DATI SESSION
	//makeGet("getInfoUtente", showInfo);
	makeGet("getCorsi", showCorsi); // Chiamata asincrona
//	makeGet(riempiSidebar);
}

function showInfo() {
    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) sostituisco informazioni utente nella navbar
	if (request.readyState == 4 && request.status == 200 ) {
		utente = JSON.parse(request.responseText);
		$("#immagine").attr("src", utente.immagine);
		$("#nome").text(utente.nome); //nome e cognome
		$("#mail").text(utente.mail);
		$("#matricola").text(utente.matricola);
		
	}
}

function showCorsi() {
	// QUI FINE LOADER   
	loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append dei corsi ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		corsi = JSON.parse(request.responseText);
		$("#content").append(
		'<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">'+
		'</div>');
		for (i=0; i<corsi.length; i++){
			$("#accordion").append(
				'<div class="panel panel-default">'+
                '	<div class="panel-heading" role="tab" id="headingOne">'+
                '		<h4 class="panel-title">'+
                '			<a data-parent="#accordion" onclick="getEsami(`'+corsi[i].nome+'`)" >'+corsi[i].nome+'</a>'+
                '		</h4>'+
                '	</div>'+
                '</div>'
				);
		}
	}
}

function getEsami(nomeCorso) {
	console.log(nomeCorso);
	$("#content").empty(); // rimuovo gli elementi che ci sono ora nella pagina, non servono piu
	// QUI INIZIO LOADER
	loaderDiv.style.display = "block";

	makeGet("ElencoEsami", showEsami); // Chiamata asincrona
}

function showEsami() {
	// QUI FINE LOADER  
	loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append di tutti i corsi + esami ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		esami = JSON.parse(request.responseText);
		// accordion con corsi + esami
	}
}

function getRisultati(idEsame) {
	console.log(idEsame);
	$("#content").empty(); // rimuovo gli elementi che ci sono ora nella pagina, non servono piu
	// QUI INIZIO LOADER
	loaderDiv.style.display = "block";

	makeGet("getResults", showRisultati); // Chiamata asincrona
}

function showRisultati() {
	// QUI FINE LOADER  
	loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append di tutti i corsi + esami ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		risultati = JSON.parse(request.responseText);
		if(risultati.ruoloUtente == "teacher"){
			console.log("we prof");
			// tabella con tutti i risultati
		} else {
			console.log("we student");
			// tabella con una sola riga
		}
	}
}