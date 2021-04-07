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
	request.open("GET", servletUrl); 
	request.send();
}

/* Funzione per mandare richieste GET con parametri */
function makeGetParameters(servletUrl, callback, paramNameValue) {	
	// Richiesta asincrona non cambia la pagina
	request = new XMLHttpRequest(); // Nuova richiesta
	// var url = 'getCorsi'; // URL della Servlet

	request.onreadystatechange = callback; // Chiamata al callback
	request.open("GET", servletUrl+"?"+paramNameValue[0]+"="+paramNameValue[1]); 
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
	makeGet("getCorsi", showCorsi); 
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
		for (i = 0; i < corsi.length; i++){
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
	
	// aggiorno anche il titolo della pagina e il back button (torna a HOME)
	$("#titleText").empty();
	$("#titleText").append('<a onclick="makeGet(`getCorsi`, showCorsi)"><i class="fas fa-chevron-left back-btn"></i></a>');
	$("#titleText").append("I tuoi esami");

	makeGetParameters("ElencoEsami", showEsami, ["nomeCorso",nomeCorso]); // Chiamata asincrona
}

function showEsami() {
	// QUI FINE LOADER  
	loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append di tutti i corsi + esami ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		corsiEsami = JSON.parse(request.responseText);
		console.log(corsiEsami);
		// accordion con corsi + esami

		$("#content").append(
			'<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">'+
			'</div>'
		);
		
		for(i = 0; i < corsiEsami[0].length; i++){
			$("#accordion").append(
				'<div class="panel panel-default">'+
				'             <div class="panel-heading" role="tab" id="heading'+i+'">'+
				'                 <h4 class="panel-title">'+
				'                     <a role="button" data-toggle="collapse" data-parent="#accordion" href="#collapse'+i+'" aria-expanded="false" aria-controls="collapse'+i+'">'+
				'                         '+corsiEsami[0][i].nome+' '+corsiEsami[0][i].anno+
				'                     </a>'+
				'                 </h4>'+
				'             </div>'+
				'             <div id="collapse'+i+'" class="panel-collapse collapse" role="tabpanel" aria-labelledby="heading'+i+'">'+
				'                 <div class="panel-body" id="appelli'+i+'">'+
				'                     <p>Clicca sull\'appello per visualizzare gli iscritti: </p>'+ // SARA DA DIFFERENZIARE PER USER RUOLO
				'                 </div>'+
				'             </div>'+
				' </div>'
			);
			for(j = 0; j < corsiEsami[1][i].length; j++){
				$("#appelli"+i).append(
						'	<button class="a-btn" onclick="getRisultati('+corsiEsami[0][i].id+',`'+corsiEsami[0][i].nome+'`)">Appello '+corsiEsami[1][i][j].dataAppello+'</button>'
				);
			}
		}
	}
}

function getRisultati(idEsame, nomeCorso) {
	console.log(idEsame);
	$("#content").empty(); // rimuovo gli elementi che ci sono ora nella pagina, non servono piu
	// QUI INIZIO LOADER
	loaderDiv.style.display = "block";

	// aggiorno anche il titolo della pagina e il back button (torna a ESAMI)
	$("#titleText").empty();
	$("#titleText").append('<a onclick="makeGetParameters(`ElencoEsami`, showEsami, [`nomeCorso`,`'+nomeCorso+'`])"><i class="fas fa-chevron-left back-btn"></i></a>');
	$("#titleText").append("Esito");

	makeGetParameters("getResults", showRisultati, ["idEsame",idEsame]); // Chiamata asincrona
}

function showRisultati() {
	// QUI FINE LOADER  
	loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append di tutti i corsi + esami ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		risposta = JSON.parse(request.responseText);
		risultati = risposta.risultati
		ruoloUtente = risposta.ruolo
		if(ruoloUtente == "teacher"){
			console.log("we prof");
			// svuoto il div content
			$("content").empty();
			// riempio il div content
			$("#content").append(
				'<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">'+
				'</div>'
			);
			$("#accordion").append(
                '    <div class="panel panel-default">																							'+		
                '        <div class="panel-heading" role="tab" id="headingOne">																	'+			
                '            <h4 class="panel-title">																							'+			
                '                <a data-parent="#accordion">Tecnologie Informatiche per il web 2021</a>										'+							
                '            </h4>																												'+						
                '        </div>																													'+							
                '        <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">					'+									
                '            <div class="panel-body">																							'+							
				'				<div>																											'+								
				'					<table class="table">																						'+									
				'						<thead>																									'+											
				'							<tr>																								'+															
				'								<th><a style="white-space: nowrap;">Matricola<i class="fas fa-chevron-up sort-i"></i></a></th>	'+																				
				'								<th><a style="white-space: nowrap;">Cognome</a></th>											'+									
				'								<th><a style="white-space: nowrap;">Nome</a></th>												'+											
				'								<th><a style="white-space: nowrap;">E-mail</a></th>												'+										
				'								<th><a style="white-space: nowrap;">Voto</a></th>												'+									
				'								<th><a style="white-space: nowrap;">Stato</a></th>												'+										
				'								<th></th>																						'+								
				'							</tr>																								'+
				'						</thead>																								'+					
				'						<tbody>																									'+											
				'							<tr>																								'+														
				'								<td>800001</td>																					'+												
				'								<td>Bagarin</td>																				'+														
				'								<td>Stefano</td>																				'+													
				'								<td>stefano.bagarin<br>@mail.polimi.it</td>														'+														
				'								<td>Ingegneria Informatica</td>																	'+																					
				'								<td>28</td>																						'+																		
				'								<td>verbalizzato</td>																			'+															
				'																																'+														
				'							</tr>																								'+														
				'						</tbody>																								'+													
				'					</table>																									'+															
				'					<a class="a-btn" href="/tiw-project-html/pubblicaVoti?idEsame=2010">Pubblica</a>							'+																			
				'					<a class="a-btn" href="/tiw-project-html/verbalizzaVoti?idEsame=2010">Verbalizza</a>						'+																				
				'																																'+																		
				'				</div>																											'+											
				'																																'+																									
				'																																'+																								
                '            </div>																												'+																								
                '        </div>																													'+																	
                '    </div>																														'														
				);
		} else {
			console.log("we student");
			// tabella con una sola riga
		}
	}
}