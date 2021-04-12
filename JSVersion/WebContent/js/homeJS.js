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
	var request = new XMLHttpRequest(); // Nuova richiesta
	// var url = 'getCorsi'; // URL della Servlet

	request.onreadystatechange = function (req) {callback(req.target);}; // Chiamata al callback
	request.open("GET", servletUrl); 
	request.send();
}

/* Funzione per mandare richieste GET con parametri */
function makeGetParameters(servletUrl, callback, paramNameValue) {	
	// Richiesta asincrona non cambia la pagina
	request = new XMLHttpRequest(); // Nuova richiesta
	// var url = 'getCorsi'; // URL della Servlet
	request.onreadystatechange = function (req) {callback(req.target);};; // Chiamata al callback
	
	servletUrl += "?";
	servletUrl += paramNameValue[0]+"="+paramNameValue[1];
	for(i = 2; i < paramNameValue.length; i+2){
			servletUrl += "&"+paramNameValue[i]+"="+paramNameValue[i+1];
	}

	request.open("GET", servletUrl); 
	request.send();
}


/* VARIABILI GLOBALI */
//var request;
var loaderDiv;
var risultati;

function init() {
	loaderDiv = document.getElementById("loader");

// Rimpiazza la gestione del browser
// chiamata a servlet che risponde con json dei corsi
	console.log("Chiamo init");
	// QUI INIZIO LOADER
	loaderDiv.style.display = "block";

	// QUI CHIAMATA PER PRENDERE DATI SESSION
	makeGet("getInfoUtente", showInfo);
	makeGet("getCorsi", showCorsi); 
//	makeGet(riempiSidebar);
}

function showInfo(request) {
	console.log(request);
    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) sostituisco informazioni utente nella navbar
	if (request.readyState == 4 && request.status == 200 ) {
		utente = JSON.parse(request.responseText);
		console.log(utente);
		$("#immagine").attr("src", utente.image);
		$("#nome").text(utente.nome+' '+utente.cognome); //nome e cognome
		$("#mail").text(utente.email.split("@")[0]+'\n@'+utente.email.split("@")[1]);
		$("#matricola").text(utente.matricola);
		
	}
}

function showCorsi(request) {
	console.log(request);

	// QUI FINE LOADER   
	loaderDiv.style.display = "none";

	// aggiungo classe per animazione "uscita" - CONTROLLARE SE FUNZIONA DAVVERO
	//$("#content").addClass('animate__animated');

	$("#content").empty(); // rimuovo gli elementi che ci sono ora nella pagina, non servono piu

	// aggiorno anche il titolo della pagina e tolgo il back button 
	$("#titleText").empty();
	$("#titleText").append("I tuoi corsi");

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

		// aggiungo classe per animare "entrata", rimuovo quella gia esistente - CONTROLLARE SE FUNZIONA DAVVERO
		$("#content").addClass('animate__animated');
		$("#content").addClass('animate__backInRight');

	}
}

function getEsami(nomeCorso) {
	console.log(nomeCorso);
	$("#content").empty(); // rimuovo gli elementi che ci sono ora nella pagina, non servono piu
	// QUI INIZIO LOADER
	loaderDiv.style.display = "block";

	makeGetParameters("ElencoEsami", showEsami, ["nomeCorso",nomeCorso]); // Chiamata asincrona
}

function showEsami(request) {
	// QUI FINE LOADER  
	loaderDiv.style.display = "none";

	// aggiorno anche il titolo della pagina e il back button (torna a HOME)
	$("#titleText").empty();
	$("#titleText").append('<a onclick="makeGet(`getCorsi`, showCorsi)"><i class="fas fa-chevron-left back-btn"></i></a>');
	$("#titleText").append("I tuoi esami");

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
				'                     <p>Clicca sull\'appello per visualizzare i dati: </p>'+
				'                 </div>'+
				'             </div>'+
				' </div>'
			);
			for(j = 0; j < corsiEsami[1][i].length; j++){
				$("#appelli"+i).append(
						'	<button class="a-btn" onclick="getRisultati('+corsiEsami[1][i][j].id+',`'+corsiEsami[0][i].nome+'`)">Appello '+corsiEsami[1][i][j].dataAppello+'</button>'
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
	$("#sottotitolo").empty();
	$("#titleText").empty();
	$("#titleText").append('<a onclick="getEsami(`'+nomeCorso+'`)"><i class="fas fa-chevron-left back-btn"></i></a>');
	$("#titleText").append("Esito");

	makeGetParameters("getResults", showRisultati, ["idEsame",idEsame]); // Chiamata asincrona
}

function showRisultati(request) {
	$("#content").empty(); // rimuovo gli elementi che ci sono ora nella pagina, non servono piu

	// QUI FINE LOADER  
	loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append di tutti i corsi + esami ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		risposta = JSON.parse(request.responseText);
		risultati = risposta.risultati;
		ruoloUtente = risposta.ruolo;
		arePubblicabili = risposta.pubblicabili;
		areVerbalizzabili = risposta.verbalizzabili;

		console.log(risultati);

		if(ruoloUtente == "teacher"){
			console.log("we prof");
			// svuoto il div content
			$("#content").empty();
			
			// mostro il modal
			$("body").prepend(
  			'<div class="modal fade" id="myModal" role="dialog">'+
  			'  <div class="modal-dialog modal-lg">'+
  			'    <div class="modal-content">'+
  			'      <div class="modal-header">'+
  			'        <button type="button" class="close" style="color: #3498db;" data-dismiss="modal">&times;</button>'+
  			'        <h4 class="modal-title">Inserimento Multiplo</h4>'+
  			'      </div>'+
  			'      <div class="modal-body">'+
  			'        <h5>Puoi modificare i singoli voti, oppure selezionare le righe e inserire un solo voto per tutte.</h5>'+
			'	<form id="modalForm">'+
  			'        <table class="table">													'+																	
  			'      	<thead>										'+																									
  			'      		<tr>'+
  			'               <th><input onclick="selezionaTutto()" class="form-check-input" type="checkbox" value="" id="checkAll"></th>	'+																																			
  			'      			<th><a style="white-space: nowrap;">Matricola</a></th>						'+														
  			'      			<th><a style="white-space: nowrap;">Cognome</a></th>						'+														
  			'      			<th><a style="white-space: nowrap;">Nome</a></th>							'+															
  			'      			<th><a style="white-space: nowrap;">E-mail</a></th>							'+														
  			'      			<th><a style="white-space: nowrap;">Corso di Laurea</a></th>				'+														
  			'      			<th><a style="white-space: nowrap;">Voto</a></th>							'+													
  			'      			<th></th>																	'+												
  			'      		</tr>																			'+				
  			'      	</thead>																			'+									
  			'      	<tbody id="tabellaModal">	'+																	
  			'      	</tbody>			'+																																	
  			'      </table>	'+
			'			<input type="hidden" value="'+risultati[0].esame.id+'" name="idEsame">'+
			'	</form>'+
  			'      </div>'+
  			'      <div class="modal-footer">'+
  			'        <button type="button" onclick="inserimentoMultiplo()" class="btn btn-default modal-btn" data-dismiss="modal">Invia</button>'+
  			'      </div>'+
  			'    </div>'+
  			'  </div>'+
  			'</div>'
			);
			// QUI RIEMPIRE CON UN FOR LE RIGHE DELLA tabellaModal CON I RISULTATI SALVATI IN LOCALE (var globale)
			// riempiModal(), che è questa sotto
			visibili = false;
			for(i = 0; i < risultati.length; i++){
				if(risultati[i].stato == "non inserito"){
					visibili = true;
						$("#tabellaModal").append(											
							'<tr>	'+
							' <th><input onclick="selezionaRiga()" class="form-check-input" type="checkbox" value="" id="check'+i+'" onchange=selezionaRiga(this.id)></th>	'+																																															
							'	<td>'+risultati[i].studente.matricola+'<input type="hidden" name="matricola" value="'+risultati[i].studente.matricola+'"></td>																					'+												
							'	<td>'+risultati[i].studente.cognome+'</td>																				'+														
							'	<td>'+risultati[i].studente.nome+'</td>																				'+													
							'	<td>'+risultati[i].studente.email.split("@")[0]+'<br/>@'+risultati[i].studente.email.split("@")[1]+'</td>														'+														
							'	<td>'+risultati[i].studente.cdl+'</td>																	'+																					
							'	<td><select name="voto" id="sel'+i+'" onchange=handleSelection(this)>'+
							'			<option></option>'+
							'			<option>assente</option>'+
							'			<option>rimandato</option>'+
							'			<option>riprovato</option>'+
							'			<option>18</option>'+
							'			<option>19</option>'+
							'			<option>20</option>'+
							'			<option>21</option>'+
							'			<option>22</option>'+
							'			<option>23</option>'+
							'			<option>24</option>'+
							'			<option>25</option>'+
							'			<option>26</option>'+
							'			<option>27</option>'+
							'			<option>28</option>'+
							'			<option>29</option>'+
							'			<option>30</option>'+
							'			<option>30 e Lode</option>'+
							'		</select></td>'+
							'</tr>'
						);
				}
			}
			if(!visibili){
				$("#modalForm").append('<p>Non ci sono voti da inserire.</p>');
			}

			// riempio il div content con la pagina effettiva
			$("#content").append(
				'<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">'+
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
				'								<th><a style="white-space: nowrap;" onclick="ordinaTabella(0)">Matricola<i id="freccia0" class="fas fa-chevron-up sort-i"></i></a></th>	'+																				
				'								<th><a style="white-space: nowrap;" onclick="ordinaTabella(1)">Cognome<i id="freccia1"></a></th>											'+									
				'								<th><a style="white-space: nowrap;" onclick="ordinaTabella(2)">Nome<i id="freccia2"></a></th>												'+											
				'								<th><a style="white-space: nowrap;" onclick="ordinaTabella(3)">E-mail<i id="freccia3"></a></th>												'+										
				'								<th><a style="white-space: nowrap;" onclick="ordinaTabella(4)">Corso di Laurea<i id="freccia4"></a></th>									'+										
				'								<th><a style="white-space: nowrap;" onclick="ordinaTabella(5)">Voto<i id="freccia5"></a></th>												'+									
				'								<th><a style="white-space: nowrap;" onclick="ordinaTabella(6)">Stato<i id="freccia6"></a></th>												'+										
				'								<th></th>																						'+								
				'							</tr>																								'+
				'						</thead>																								'+					
				'						<tbody id="tabellaVoti">																				'+
				'						</tbody>																								'+													
				'					</table>																									'+		
				'					<button id="bottoneInserimento" class="a-btn" data-toggle="modal" data-target="#myModal">Inserimento Multiplo</button>								'+																																
				'					<button id="bottonePubblica" class="a-btn" onclick="pubblicaVoti('+risultati[0].esame.id+')">Pubblica</button>								'+																			
				'					<button id="bottoneVerbalizza" class="a-btn" onclick="verbalizzaVoti('+risultati[0].esame.id+')">Verbalizza</button>							'+																				
				'																																'+																		
				'				</div>																											'+											
				'																																'+																									
				'																																'+																								
                '            </div>																												'+																								
                '        </div>																													'+																	
                '    </div>	'+
				'</div>'
			);
			
			// setto la class dei bottoni pubblica e verbalizza
			arePubblicabili ? $("#bottonePubblica").removeAttr("disabled") : $("#bottonePubblica").attr("disabled", "disabled"); 
			areVerbalizzabili ? $("#bottoneVerbalizza").removeAttr("disabled") : $("#bottoneVerbalizza").attr("disabled", "disabled"); 

			for(i = 0; i < risultati.length; i++){
				if(risultati[i].voto == null){
					risultati[i].voto = "";
				}
				if(risultati[i].modificabile == true){
					risultati[i].modificabile = '<a class="modifica-btn" onclick="modificaVoti('+risultati[i].esame.id+','+risultati[i].studente.matricola+')">Modifica</a>';	
				}
				else{
					risultati[i].modificabile = '';
				}
			}

			for(i = 0; i < risultati.length; i++){
				$("#tabellaVoti").append(											
					'							<tr>																								'+														
					'								<td>'+risultati[i].studente.matricola+'</td>																					'+												
					'								<td>'+risultati[i].studente.cognome+'</td>																				'+														
					'								<td>'+risultati[i].studente.nome+'</td>																				'+													
					'								<td>'+risultati[i].studente.email.split("@")[0]+'<br/>@'+risultati[i].studente.email.split("@")[1]+'</td>														'+														
					'								<td>'+risultati[i].studente.cdl+'</td>																	'+																					
					'								<td>'+risultati[i].voto+'</td>																						'+																		
					'								<td>'+risultati[i].stato+'</td>																							'+
					'								<td>'+risultati[i].modificabile+'</td>    '+	 									
					'							</tr>																							'
					);
			}

		} else {
			console.log("we student");
			// svuoto il div content
			$("#content").empty();

			// controllo se il voto non è ancora definito
			if(risultati == null){
				$("#content").append('<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">'+
				'    <div class="panel panel-default">																							'+		
                '        <div class="panel-heading" role="tab" id="headingOne">																	'+			
                '            <h4 class="panel-title">																							'+			
                '                <a data-parent="#accordion">Tecnologie Informatiche per il web 2021</a>										'+							
                '            </h4>																												'+						
                '        </div>																													'+							
                '        <div id="collapseOne" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingOne">					'+									
                '            <div class="panel-body">																							'+							
				'				<div>																											'+								
				'					<p>Voto non ancora definito.</p>   																			'+
				'				</div>																											'+																									
                '            </div>																												'+																								
                '        </div>																													'+																	
                '    </div>	'+
				'</div>')
			}else{
				// riempio il div content
				$("#content").append(
					'<div class="panel-group" id="accordion" role="tablist" aria-multiselectable="true">'+
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
					'								<th>Matricola</th><th>Nome e Cognome</th><th>Data Appello</th><th>Voto</th><th></th>			'+							
					'							</tr>																								'+
					'						</thead>																								'+					
					'						<tbody id="tabellaVoti">																				'+
					'						</tbody>																								'+													
					'					</table>																									'+																		
					'				</div>																											'+																									
					'            </div>																												'+																								
					'        </div>																													'+																	
					'    </div>	'+
					'</div>'
				);

				for(i = 0; i < risultati.length; i++){
					if(risultati[i].voto == null){
						risultati[i].voto = "";
					}
					if(risultati[i].rifiutabile == true){
						risultati[i].rifiutabile = '<a class="rifiuta-btn" onclick="rifiutaVoto('+risultati[i].esame.id+',`'+risultati[i].corso.nome+'`)">Rifiuta</a>';	
					}
					else{
						if(risultati[i].stato == "verbalizzato")
							risultati[i].rifiutabile = '';
						else
							risultati[i].rifiutabile = '<p>Il voto è stato rifiutato.</p>';
					}
				}

				for(i = 0; i < risultati.length; i++){
					$("#tabellaVoti").append(											
						'							<tr>																								'+														
						'								<td>'+risultati[i].studente.matricola+'</td>																					'+												
						'								<td>'+risultati[i].studente.nome+' '+risultati[i].studente.cognome+'</td>																				'+													
						'								<td>'+risultati[i].esame.dataAppello+'</td>																						'+																		
						'								<td>'+risultati[i].voto+'</td>																						'+																		
						'								<td>'+risultati[i].rifiutabile+'</td>    '+	 									
						'							</tr>																							'
						);
				}
			}
		}
	}
}

function inserimentoMultiplo(){
	// QUI INIZIO LOADER
	loaderDiv.style.display = "block";

	request = new XMLHttpRequest(); // Nuova richiesta
	var url = 'inserimentoMultiplo'; // URL della Servlet
	var formElement = document.querySelector("#modalForm"); // Prendo parametri dal form
	var formData = new FormData(formElement);

	request.onreadystatechange = function (req) {showRisultati(req.target);};; // Chiamata al callback
	request.open("POST", url); // Richiesta POST all'URL
	request.send(formData); // Mando dati del form);
}


function modificaVoti(idEsame, matricola) {
	console.log(idEsame);
	$("#content").empty(); // rimuovo gli elementi che ci sono ora nella pagina, non servono piu

	// aggiorno anche il titolo della pagina e il back button (torna a ESAMI)
	$("#titleText").empty();
	$("#titleText").append('<a onclick="getRisultati('+idEsame+',`'+risultati[0].corso.nome+'`)"><i class="fas fa-chevron-left back-btn"></i></a>');
	$("#titleText").append("Modifica Voto");

	var studente;
	// cerco i dati dell'utente
	risultati.forEach(e => {
		console.log(e);
		if(e.studente.matricola == matricola)
			studente = e.studente;
	});

	// form per modificare il voto
	$("#content").append(
		'	<div class="col-xs-1"></div>'+
		'	<div class="col-xs-10">'+
		'		<form action="#" method="POST" id="modificaForm">'+
		'			<table class="table my-table">'+
		'				<thead>'+
		'					<tr>'+
		'						<th>Matricola</th><th>Cognome</th><th>Nome</th><th>E-mail</th><th>Corso di Laurea</th><th>Voto</th><th></th>'+
		'					</tr>'+
		'				</thead>'+
		'				<tbody id="tabellaVoti">'+
		'					<tr>'+
		'						<td>'+studente.matricola+'</td>																					'+												
		'								<td>'+studente.cognome+'</td>																				'+														
		'								<td>'+studente.nome+'</td>																				'+													
		'								<td>'+studente.email.split("@")[0]+'<br/>@'+studente.email.split("@")[1]+'</td>														'+														
		'								<td>'+studente.cdl+'</td>'+
		'						<td >'+
		'							<select name="voto" id="voto">'+
		'								<option>assente</option>'+
		'								<option>rimandato</option>'+
		'								<option>riprovato</option>'+
		'								<option>18</option>'+
		'								<option>19</option>'+
		'								<option>20</option>'+
		'								<option>21</option>'+
		'								<option>22</option>'+
		'								<option>23</option>'+
		'								<option>24</option>'+
		'								<option>25</option>'+
		'								<option>26</option>'+
		'								<option>27</option>'+
		'								<option>28</option>'+
		'								<option>29</option>'+
		'								<option>30</option>'+
		'								<option>30 e Lode</option>'+
		'							</select>'+
		'						</td>'+
		'						<td><input id="applicaButton" class="modifica-btn" value="Applica" onclick="inserisciVoti(`'+risultati[0].corso.nome+'`)"></td>'+
		'					</tr>'+
		'				</tbody>'+
		'			</table>'+
		'			<input type="hidden" value="'+idEsame+'" name="idEsame">'+
		'			<input type="hidden" value="'+matricola+'" name="matricola">'+
		'		</form>'+
		'	</div>'+
		'	<div class="col-xs-1"></div>'
	);	
}

function inserisciVoti(nomeCorso){
	// QUI INIZIO LOADER
	loaderDiv.style.display = "block";

	request = new XMLHttpRequest(); // Nuova richiesta
	var url = 'inserisciVoti'; // URL della Servlet
	var formElement = document.querySelector("#modificaForm"); // Prendo parametri dal form
	var formData = new FormData(formElement);

	request.onreadystatechange = function (req) {showRisultati(req.target);};; // Chiamata al callback
	request.open("POST", url); // Richiesta POST all'URL
	request.send(formData); // Mando dati del form

	// aggiorno anche il titolo della pagina e il back button (torna a ESAMI)
	$("#titleText").empty();
	$("#titleText").append('<a onclick="getEsami(`'+nomeCorso+'`)"><i class="fas fa-chevron-left back-btn"></i></a>');
	$("#titleText").append("Esito");
}

function rifiutaVoto(idEsame, nomeCorso){
	makeGetParameters("rifiutaVoti",showRisultati,["idEsame",idEsame]);
}

function pubblicaVoti(idEsame){
	makeGetParameters("pubblicaVoti",showRisultati,["idEsame",idEsame]);
}

function verbalizzaVoti(idEsame){
	makeGetParameters("verbalizzaVoti",showVerbale,["idEsame",idEsame]);
}

function showVerbale(request){
	console.log(request);

	// QUI FINE LOADER   
	loaderDiv.style.display = "none";

	// aggiungo classe per animazione "uscita" - CONTROLLARE SE FUNZIONA DAVVERO
	//$("#content").addClass('animate__animated');

	$("#content").empty(); // rimuovo gli elementi che ci sono ora nella pagina, non servono piu

	// aggiorno anche il titolo della pagina e tolgo il back button 
	$("#titleText").empty();


    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append dei corsi ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		verbale = JSON.parse(request.responseText);
		console.log(JSON.parse(request.responseText));
		
		// setto il titolo con anche il bottone per tornare indietro (alla pagina Esito)
		$("#titleText").append('<a onclick="getRisultati('+verbale.risultati[0].esame.id+',`'+verbale.risultati[0].corso.nome +'`)"><i class="fas fa-chevron-left back-btn"></i></a>');
		$("#titleText").append("Verbale n° "+verbale.id);
		$("#sottotitolo").append("<h3>Data: "+verbale.dataVerbale+"</h3>");
		$("#sottotitolo").append("<h4>"+verbale.risultati[0].corso.nome + " - Appello del " +verbale.risultati[0].esame.dataAppello+"</h4>");
	
		// creo la tabella del verbale
		$("#content").append(
		'	<div class="col-xs-1"></div>'+
		'		<div class="col-xs-10">'+
		'			<table class="table my-table">'+
		'				<thead>'+
		'					<tr>'+
		'						<th>Matricola</th><th>Cognome</th><th>Nome</th><th>Voto</th>'+
		'					</tr>'+
		'				</thead>'+
		'				<tbody id="tabellaVerbale">'+
		'				</tbody>'+
		'			</table>'+
		'	</div>'+
		'	<div class="col-xs-1"></div>'
		);

		for(i = 0; i < verbale.risultati.length; i++){
			$("#tabellaVerbale").append(
				'	<tr>'+
				'		<td>'+verbale.risultati[i].studente.matricola+'</td>'+
				'		<td>'+verbale.risultati[i].studente.cognome+'</td>'+
				'		<td>'+verbale.risultati[i].studente.nome+'</td>'+
				'		<td>'+verbale.risultati[i].voto+'</td>'+
				'	</tr>'
			);
		}
	}
}

function ordinaTabella(n) {
	// aggiorno le frecce
	for(i = 0; i < 7; i++){
		if(i != n){
			$("#freccia"+i).removeClass('fas fa-chevron-up sort-i');
			$("#freccia"+i).removeClass('fas fa-chevron-down sort-i');
		}
	}
	$("#freccia"+n).removeClass('fas fa-chevron-down sort-i');
	$("#freccia"+n).addClass('fas fa-chevron-up sort-i');

	// bubble sort :P
	var tabella, rows, switching, i, x, y, shouldSwitch, ordine, switchDone;
	tabella = document.getElementById("tabellaVoti");
	switching = true;
	switchDone = false;
	ordine = "ASC";

	while (switching) {
		switching = false;
		rows = tabella.rows;

		for (i = 0; i < (rows.length - 1); i++) { // -1 perche faccio +1 nel ciclo
			shouldSwitch = false;

			x = rows[i].getElementsByTagName("td")[n]; // cella della riga corrente
			y = rows[i + 1].getElementsByTagName("td")[n]; // cella della riga successiva

			if(compareRows(x, y, ordine, n)) {
				shouldSwitch = true;
				break;
			}
	}
	if (shouldSwitch) {
		rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
		switching = true;
		switchDone = true;
	} else {
		if (!switchDone && ordine == "ASC") {   
			// se arrivo qui significa che non ho scambiato nessuna cella, rifaccio tutto invertendo l'ordine
			// in questo modo gestisco i due ordini senza usare altre variabili
			ordine = "DESC";
			$("#freccia"+n).removeClass('fas fa-chevron-up sort-i');
			$("#freccia"+n).addClass('fas fa-chevron-down sort-i');
			switching = true;
		}
	}
  }
}

function comparaVoti(voto1, voto2, ordine){
	// definisco l'ordinamento personalizzato (ASC)
	var ordinamentoVoti = ["","assente","rimandato","riprovato","18","19","20","21","22","23","24","25","26","27","28","29","30","30 e Lode"];
	if (ordine == "ASC") 
		return ordinamentoVoti.indexOf(voto1) > ordinamentoVoti.indexOf(voto2);
	else 
		return ordinamentoVoti.indexOf(voto1) < ordinamentoVoti.indexOf(voto2);
}

function compareRows(x, y, ordine, n) {
	console.log("ordino "+ordine);
	// ORDINE ASC : <vuoto>, assente, rimandato, riprovato, 18, 19, ... 30 e Lode
	// ORDINE DESC: 30 e Lode, ..., 19, 18, riprovato, rimandato, assente, <vuoto>
	// ORDINE ATTUALE ASC: <vuoto>, 18, 19, ... , assente, rimandato, riprovato 
	// ORDINE ATTUALE DESC: riprovato, rimandato, assente, ..., 19, 18, <vuoto>
	// in base al tipo di colonna ho un ordinamento diverso
	if (n == 5){
		if (ordine == "ASC") 
			// ASC
			return comparaVoti(x.innerHTML.toLowerCase(),y.innerHTML.toLowerCase(), ordine);
		else 
			// DESC
			return comparaVoti(x.innerHTML.toLowerCase(),y.innerHTML.toLowerCase(), ordine);
	} else{
		if (ordine == "ASC")
			// ASC
			return x.innerHTML.toLowerCase() > y.innerHTML.toLowerCase();
		else
			// DESC
			return x.innerHTML.toLowerCase() < y.innerHTML.toLowerCase();
	}
}


var linkedBox = [];
var selectedPos = 0;

function selezionaTutto(){
	// seleziona tutte le righe della tabella
    rows = document.getElementById("tabellaModal").rows;
    linkedBox = [];

    for (i = 0; i < rows.length; i++){
		if($("#checkAll").prop("checked")){
            // selezionate tutte, pusho tutto nella lista e aggiorno anche i voti
            $("#check"+i).prop("checked", true);
            linkedBox.push(i);
            $("#sel"+i).prop('selectedIndex', selectedPos);
        } else{
            // deselezionate tutte
            $("#check"+i).prop("checked", false);
            linkedBox.splice(linkedBox.indexOf(i), 1);
        }
    }
}

function selezionaRiga(id){
    index = parseInt(id.charAt(id.length-1));

    if($("#check"+index).prop("checked")) {// se non è gia nella lista
        linkedBox.push(index);
        $("#sel"+index).prop('selectedIndex', selectedPos);

        // ora controllo se METTERE il check a "checkAll" oppure no
        if(areAllSelected()){
            $("#checkAll").prop("checked", true);
        }

    } else{
        linkedBox.splice(linkedBox.indexOf(index), 1);
        // ora controllo se TOGLIERE il check a "checkAll" oppure no
        if(!areAllSelected())
            $("#checkAll").prop("checked", false);

    }   
}

function areAllSelected(){
    rows = document.getElementById("tabellaModal").rows;

    for (i = 0; i < rows.length; i++){
		if(!$("#check"+i).prop("checked"))
            return false;
    }
    return true;
}

function handleSelection(sel){
    // prendo la riga del select
    index = parseInt((sel.id).charAt((sel.id).length-1));

    if(linkedBox.indexOf(index) != -1){
        // prendo la posizione dell'option selezionata
        selectedPos = $("#"+sel.id).prop('selectedIndex');

        // se la riga del select è in linkedBox allora devo aggiornare tutte le select collegate, altrimenti nulla
        linkedBox.forEach(i => {
            $("#sel"+i).prop('selectedIndex', selectedPos);
        });
        selectedPos = 0;
    }
}