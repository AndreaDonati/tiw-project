/* Oggetto globale per contenere le variabili globali */
var myApp = {}

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

/* Funzione per mandare richieste GET senza parametri */
function makeGet(servletUrl, callback) {	
	// Richiesta asincrona
	var request = new XMLHttpRequest(); // Nuova richiesta
	request.onreadystatechange = function (req) {callback(req.target);}; // Chiamata al callback
	request.open("GET", servletUrl); 
	request.send();
}

/* Funzione per mandare richieste GET con parametri */
function makeGetParameters(servletUrl, callback, paramNameValue) {	
	// Richiesta asincrona
	request = new XMLHttpRequest(); // Nuova richiesta
	request.onreadystatechange = function (req) {callback(req.target);}; // Chiamata al callback
	
	// Composizione URL per una GET in base ai parametri passati
	servletUrl += "?";
	servletUrl += paramNameValue[0]+"="+paramNameValue[1];
	for(i = 2; i < paramNameValue.length; i+2){
			servletUrl += "&"+paramNameValue[i]+"="+paramNameValue[i+1];
	}

	request.open("GET", servletUrl); 
	request.send();
}

function init() {
	// Prendo l'elemento del loader
	resetTable();
	resetStorage();
	myApp.loaderDiv = document.getElementById("loader");	

	// Start del loader 
	myApp.loaderDiv.style.display = "block";

	makeGet("getInfoUtente", showInfo); // Chiamata alla servlet che restituisce i dati della sessione
	makeGet("getCorsi", showCorsi); // Dati dei corsi disponibili
}

function showInfo(request) {
    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) sostituisco informazioni utente nella navbar
	if (request.readyState == 4 && request.status == 200 ) {
		utente = JSON.parse(request.responseText);

		document.getElementById("immagine").src = utente.image;
		document.getElementById("nome").innerHTML = utente.nome + ' ' + utente.cognome;
		document.getElementById("mail").innerHTML = utente.email.split("@")[0]+'\n@'+utente.email.split("@")[1];
		document.getElementById("matricola").innerHTML = utente.matricola;
	}
}

function showCorsi(request) {

	myApp.loaderDiv.style.display = "none";

	document.getElementById("content").innerHTML = ""; // reset della pagina
	document.getElementById("titleText").innerHTML = "I tuoi corsi"; // aggiorno jumbotron

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append dei corsi ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		corsi = JSON.parse(request.responseText);

		if(corsi != null){
			var accordionDiv = createDiv("panel-group", "accordion", "tablist"); // div accordion

			for (i = 0; i < corsi.length; i++){
				
				txt = createText("h4", "panel-title", "pTitle"+i); // testo con nome del corso
				btn = createBtn("a", "menu", corsi[i].nome); // bottone per selezionare corso
				btn.setAttribute("data-parent", "#accordion");
				btn.setAttribute("nome", corsi[i].nome);
				btn.addEventListener('click', (event) => {
					myApp.nomeCorso = event.target.getAttribute("nome");
					document.getElementById("accordion").innerHTML = "";
					getEsami(); // richiesta alla servlet per il corso selezionato

				});
				txt.appendChild(btn);

				var div = createDiv("panel panel-default", "pDefault"+i, ""); // div per elemento accordion
				div.appendChild(createDiv("panel-heading", "pHead"+i, "tab").appendChild(txt)); // div per titolo
				accordionDiv.appendChild(div);
			}

			document.getElementById("content").appendChild(accordionDiv);
		} else{
			//TODO modificare
			if(utente.ruolo == "teacher"){
				$("#content").append(
					'<div id="no-results-msg" class="jumbotron animate__animated animate__backInRight animate__fast">'+
					'	<h4>Non sei il docente di nessun corso al momento</h4>'+
					'</div>'
				);
				} else{
					$("#content").append(
					'<div id="no-results-msg" class="jumbotron animate__animated animate__backInRight animate__fast">'+
					'	<h4>Non sei iscritto a nessun corso al momento</h4>'+
					'</div>'
					);
				}
				
				$("#content").addClass('animate__animated');
				$("#content").addClass('animate__backInRight');
		}
	}
}

function getEsami() {

	myApp.loaderDiv.style.display = "block";

	document.getElementById("content").innerHTML = ""; // reset della pagina

	document.getElementById("titleText").innerHTML = "I tuoi esami"; // aggiorno jumbotron e back button (torna a HOME)	
	btn = createBtn("a", "", ""); // creo back button
	btn.addEventListener('click', () => {
		makeGet("getCorsi", showCorsi); // richiesta alla servlet per tutti i corsi, callback per re-render
	});
	btn.appendChild(createIcon("fas fa-chevron-left back-btn")); // append dell'icona al bottone
	document.getElementById("titleText").prepend(btn); // prepend del bottone al testo

	makeGetParameters("ElencoEsami", showEsami, ["nomeCorso", myApp.nomeCorso]); // Chiamata asincrona
}

function showEsami(request) {

	myApp.loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append di tutti i corsi + esami ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		
		corsiEsami = JSON.parse(request.responseText);

		var accordionDiv = createDiv("panel-group", "accordion", "tablist"); // div accordion

		for(i = 0; i < corsiEsami[0].length; i++){

			txt = createText("h4", "panel-title", "pTitle"+i); // testo con nome del corso

			btn = createAccordionBtn(corsiEsami[0][i].nome+' '+corsiEsami[0][i].anno, i);
    		txt.appendChild(btn); // bottone per corso-anno 

			var txt2;

			if(corsiEsami[1] != null && corsiEsami[1][i].length > 0){
				txt2 = createAccordionText("p");
				
				for(j = 0; j < corsiEsami[1][i].length; j++){			
					var btn2 = createBtn("button", "a-btn", "Appello "+  formatDate(corsiEsami[1][i][j].dataAppello)); // bottone per specifico esame
					btn2.setAttribute("idEsame", corsiEsami[1][i][j].id);
					btn2.addEventListener('click', (event) => {
						myApp.idEsame = event.target.getAttribute("idEsame");
						document.getElementById("accordion").innerHTML = "";

						getRisultati(); // richiesta alla servlet per il corso selezionato
					});
					txt2.appendChild(btn2);
				}
		 	} else{
				 //TODO
				txt2 = document.createElement("p");
				if(utente.ruolo == "teacher"){
					txt2.innerHTML = "Non sono presenti appelli per questo corso";
				} else{
					txt2.innerHTML = "Non sei iscritto/a a nessun appello di questo corso";
				}
			}

			var div = createDiv("panel panel-default", "pDefault"+i, ""); // div per elemento accordion
			var headDiv = createDiv("panel-heading", "pHead"+i, "tab"); // div per head accordion
			headDiv.append(txt) // aggiungo corso-anno
			div.appendChild(headDiv); // aggiungo head a elemento

			var collapseDiv = createLabelledDiv("panel-collapse collapse", "collapse"+i, "tabpanel", "pHead"+i); // div per parte nascosta
			x = createDiv("panel-body", "appelli"+i, ""); // div per elenco appelli
			x.append(txt2); // aggiungo testo e bottoni appelli
			collapseDiv.appendChild(x); // aggiungo al div
			div.appendChild(collapseDiv); // aggiungo all'elemento

			accordionDiv.appendChild(div); // aggiungo elemento all'accordion
		}
		document.getElementById("content").appendChild(accordionDiv); // aggiungo accordion al DOM

	}
}

function getRisultati() {

	document.getElementById("content").innerHTML = "";
	// QUI INIZIO LOADER
	myApp.loaderDiv.style.display = "block";

	// aggiorno anche il titolo della pagina e il back button (torna a ESAMI)
	document.getElementById("sottotitolo").innerHTML = ""; // aggiorno jumbotron e back button (torna a HOME)	
	
	document.getElementById("titleText").innerHTML = "Esito"; // aggiorno jumbotron e back button (torna a HOME)	
	btn = createBtn("a", "", ""); // creo back button
	btn.addEventListener('click', () => {
		resetTable();
		$("#checkAll").prop("checked", false);
		getEsami(); // richiesta alla servlet per tutti i corsi, callback per re-render
	});
	btn.appendChild(createIcon("fas fa-chevron-left back-btn")); // append dell'icona al bottone
	document.getElementById("titleText").prepend(btn); // prepend del bottone al testo

	makeGetParameters("getResults", showRisultati, ["idEsame", myApp.idEsame]); // Chiamata asincrona
}

function showRisultati(request) {
	document.getElementById("content").innerHTML = "";

	// QUI FINE LOADER  
	myApp.loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append di tutti i corsi + esami ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		risposta = JSON.parse(request.responseText);

		let risultati = risposta.risultati;

		ruoloUtente = risposta.ruolo;
		arePubblicabili = risposta.pubblicabili;
		areVerbalizzabili = risposta.verbalizzabili;

		if(ruoloUtente == "teacher"){
			// prendo la lista degli studenti e la salvo in sessionStorage (servirà dopo)
			let studenti = [];
			risultati.forEach(e => {
				studenti.push(e.studente);
			});

			myApp.studenti = JSON.stringify(studenti);

			// prendo la lista dei risultati con voto non inserito
			var risultatiModal = [];
			risultati.forEach(e => {
				if(e.stato == "non inserito") // per ogni voto non inserito aggiungo una riga di modifica nel modal
					risultatiModal.push(e);
			});

			if(risultatiModal.length == 0){ // se non ci sono voti da modificare
				document.getElementById("headTabellaModal").style.display = "none";
				$("#bottoneInserimento").attr("disabled", "disabled"); 

				document.getElementById("tabellaModal").innerHTML = "<br>Non ci sono voti da inserire.";
				$("#bottoneInvia").attr("disabled", "disabled"); // disabilito bottone
			}
			else {
				$("#bottoneInserimento").removeAttr("disabled");
				document.getElementById("headTabellaModal").style.display = "";
				document.getElementById("tabellaModal").innerHTML = "";

				// riempio il modal con i dati appena ricevuti, sono sempre quelli più aggiornati
				for(i = 0; i < risultatiModal.length; i++){

					var row = document.createElement("tr"); // riga della tabella

					var checkbox = createCheckbox("check"+i); // creazione checkbox
					checkbox.addEventListener('change', (event) => {
						selezionaRiga(event.target.id); //gestione selezione della checkbox
					});
					var cell = document.createElement("td"); 
					cell.append(checkbox);
					row.appendChild(cell); //cella checkbox

					var hiddenInput = createHidden();
					hiddenInput.setAttribute("value", risultatiModal[i].studente.matricola);
					hiddenInput.setAttribute("name", "matricola");

					cell = createCell(risultatiModal[i].studente.matricola);

					cell.append(hiddenInput);
					row.appendChild(cell); //cella matricola

					row.appendChild(createCell(risultatiModal[i].studente.cognome)); // cella cognome
					row.appendChild(createCell(risultatiModal[i].studente.nome)); // cella nome
					row.appendChild(createCell(risultatiModal[i].studente.email.split("@")[0]+'<br/>@'+risultatiModal[i].studente.email.split("@")[1])); // cella mail
					row.appendChild(createCell(risultatiModal[i].studente.cdl)); // cella cdl

					var select = createSelect("sel"+i, true); // creazione select
					select.addEventListener('change', (event) => {
						handleSelection(event.target); //gestione selezione della option
					});	
					cell = document.createElement("td"); 
					cell.append(select);
					row.appendChild(cell); //cella select

					document.getElementById("tabellaModal").append(row);
				}
			
				// aggiorno il campo hidden del form
				document.getElementById("hiddenId").setAttribute("value", myApp.idEsame);

				// abilito il bottone per inviare le modifiche
				$("#bottoneInvia").removeAttr("disabled");
			}
			// rendo visibile il div principale
			document.getElementById("accordion2").style.display = "block";

			// rendo visibile il div della tabella prof e nascondo quella studente
			document.getElementById("divTabellaProf").style.display = "block";
			document.getElementById("divTabellaStud").style.display = "none";

			// aggiorno titolo e data dell'appello 
			document.getElementById("titoloCorso").innerHTML = myApp.nomeCorso + " - " + formatDate(risposta.esame.dataAppello);				

			// setto la class dei bottoni pubblica e verbalizza
			arePubblicabili ? $("#bottonePubblica").removeAttr("disabled") : $("#bottonePubblica").attr("disabled", "disabled"); 
			areVerbalizzabili ? $("#bottoneVerbalizza").removeAttr("disabled") : $("#bottoneVerbalizza").attr("disabled", "disabled"); 

			if(risultati.length >= 1 && risultati[0].id != -1){
				document.getElementById("headTabellaProf").style.display = "";
				document.getElementById("tabellaVotiProf").innerHTML ="";

				// mostro i bottoni pubblica e verbalizza e ins multiplo
				document.getElementById("bottonePubblica").style.display = "";
				document.getElementById("bottoneVerbalizza").style.display = "";
				document.getElementById("bottoneInserimento").style.display = "";

				for(i = 0; i < risultati.length; i++){
					var row = document.createElement("tr"); // riga della tabella

					row.appendChild(createCell(risultati[i].studente.matricola)); // cella matricola
					row.appendChild(createCell(risultati[i].studente.cognome)); // cella cognome
					row.appendChild(createCell(risultati[i].studente.nome)); // cella nome
					row.appendChild(createCell(risultati[i].studente.email.split("@")[0]+'<br/>@'+risultati[i].studente.email.split("@")[1])); // cella mail
					row.appendChild(createCell(risultati[i].studente.cdl)); // cella cdl

					if(risultati[i].voto == null)
						row.appendChild(createCell("")); // cella voto (vuota)
					else
						row.appendChild(createCell(risultati[i].voto)); // cella voto 

					row.appendChild(createCell(risultati[i].stato)); // cella stato

					if(risultati[i].modificabile){
						var modButton = createBtn("a", "modifica-btn", "Modifica"); // creazione bottone modifica
						modButton.setAttribute("matricola", risultati[i].studente.matricola);
						modButton.setAttribute("currentVoto", risultati[i].voto);
						modButton.addEventListener('click', (event) => {
							resetTable();
							modificaVoti(parseInt(event.target.getAttribute("matricola")), event.target.getAttribute("currentVoto"));
						});	
						cell = document.createElement("td"); 
						cell.append(modButton);
						row.appendChild(cell); //cella modifica
					}
					document.getElementById("tabellaVotiProf").append(row);
				}
			}
			else{
				document.getElementById("headTabellaProf").style.display = "none";
				document.getElementById("tabellaVotiProf").innerHTML ="Nessuno studente iscritto all'appello."; 
				
				// nascondo i bottoni pubblica e verbalizza e ins multiplo
				document.getElementById("bottonePubblica").style.display = "none";
				document.getElementById("bottoneVerbalizza").style.display = "none";
				document.getElementById("bottoneInserimento").style.display = "none";


			}
		} else {
			// rendo visibile il div principale
			document.getElementById("accordion2").style.display = "block";

			// rendo visibile il div della tabella studente e nascondo quella del prof
			document.getElementById("divTabellaStud").style.display = "block";
			document.getElementById("divTabellaProf").style.display = "none";

			// aggiorno titolo
			document.getElementById("titoloCorso").innerHTML = myApp.nomeCorso;

			// controllo se il voto non è ancora definito
			if(risultati == null){
				document.getElementById("headTabellaStud").style.display = "none";
				document.getElementById("tabellaVotiStud").innerHTML = "Voto non ancora definito.";
			}
			else{
				document.getElementById("headTabellaStud").style.display = "";
				document.getElementById("tabellaVotiStud").innerHTML = "";

				for(i = 0; i < risultati.length; i++){
					var row = document.createElement("tr"); // riga della tabella
	
					row.appendChild(createCell(risultati[i].studente.matricola)); // cella matricola
					row.appendChild(createCell(risultati[i].studente.nome + ' ' + risultati[i].studente.cognome)); // cella nome e cognome
					row.appendChild(createCell(formatDate(risultati[0].esame.dataAppello))); // cella data
	
					if(risultati[i].voto == null)
						row.appendChild(createCell("")); // cella voto (vuota)
					else
						row.appendChild(createCell(risultati[i].voto)); // cella voto 
	
					row.appendChild(createCell(risultati[i].stato)); // cella stato
	
					var rifButton;
					if(risultati[i].rifiutabile){
	
						rifButton = createBtn("a", "rifiuta-btn", "Rifiuta"); // creazione bottone rifiuta
						rifButton.addEventListener('click', (event) => {	
							rifiutaVoto();
						});	
						cell = document.createElement("td"); 
						cell.append(rifButton);
						row.appendChild(cell); //cella rifiuta
					}
					else{
						if(risultati[i].stato == "verbalizzato")
							row.appendChild(createCell("")); // cella rifiuta (vuota)
						else
						row.appendChild(createCell("Il voto è stato rifiutato.")); // cella rifiuta (rifiutato)
					}	
					document.getElementById("tabellaVotiStud").append(row);
				}
			}
		}
	}
}

function resetTable(){
	document.getElementById("tabellaVotiProf").innerHTML = "";
	document.getElementById("tabellaVotiStud").innerHTML = "";
	document.getElementById("accordion2").style.display = "none";
}

function resetStorage(){
	delete myApp.nomeCorso;
	delete myApp.idEsame;
	delete myApp.studenti;
}

function formatDate(date){
	return new Date(date).toLocaleDateString();
}

function formatDateAndTime(date){
	return new Date(date).toLocaleString();
}

function inserimentoMultiplo(){
	// QUI INIZIO LOADER
	myApp.loaderDiv.style.display = "block";

	request = new XMLHttpRequest(); // Nuova richiesta
	var url = 'inserimentoMultiplo'; // URL della Servlet
	var formElement = document.querySelector("#modalForm"); // Prendo parametri dal form
	var formData = new FormData(formElement);

	request.onreadystatechange = function (req) {showRisultati(req.target);};; // Chiamata al callback
	request.open("POST", url); // Richiesta POST all'URL
	request.send(formData); // Mando dati del form);
}


function modificaVoti(matricola, voto) {

	document.getElementById("titleText").innerHTML = "Modifica Voto"; // aggiorno jumbotron e back button (torna a RISULTATI)	
	btn = createBtn("a", "", ""); // creo back button
	btn.addEventListener('click', () => {
		//resetTable();
		getRisultati(); // richiesta alla servlet per tutti i corsi, callback per re-render
	});
	btn.appendChild(createIcon("fas fa-chevron-left back-btn")); // append dell'icona al bottone
	document.getElementById("titleText").prepend(btn); // prepend del bottone al testo

	let studente;
	// cerco i dati dell'utente
	JSON.parse(myApp.studenti).forEach(e => {
		if(e.matricola == matricola)
			studente = e;
	});

	tabella = createTabella(studente, voto);
	form = createForm();

	hiddenId = createHidden();
	hiddenId.setAttribute("name", "idEsame");
	hiddenId.setAttribute("value", myApp.idEsame);

	hiddenMatr = createHidden();
	hiddenMatr.setAttribute("name", "matricola");
	hiddenMatr.setAttribute("value", matricola);

	form.appendChild(tabella);
	form.appendChild(hiddenId);
	form.appendChild(hiddenMatr);

	var modificaDiv = createDiv("col-xs-10", "", "");
	modificaDiv.appendChild(form);

	document.getElementById("content").appendChild(createDiv("col-xs-1", "", "")); // layout
	document.getElementById("content").appendChild(modificaDiv); // aggiungo form al DOM
	document.getElementById("content").appendChild(createDiv("col-xs-1", "", "")); // layout
}

function inserisciVoti(){
	// QUI INIZIO LOADER
	myApp.loaderDiv.style.display = "block";

	request = new XMLHttpRequest(); // Nuova richiesta
	var url = 'inserisciVoti'; // URL della Servlet
	var formElement = document.querySelector("#modificaForm"); // Prendo parametri dal form
	var formData = new FormData(formElement);

	request.onreadystatechange = function (req) {showRisultati(req.target);};; // Chiamata al callback
	request.open("POST", url); // Richiesta POST all'URL
	request.send(formData); // Mando dati del form

	// aggiorno anche il titolo della pagina e il back button (torna a ESAMI)
	document.getElementById("titleText").innerHTML = "Esito"; // aggiorno jumbotron e back button (torna a HOME)	
	btn = createBtn("a", "", ""); // creo back button
	btn.addEventListener('click', () => {
		resetTable();
		getEsami(); // richiesta alla servlet per tutti i corsi, callback per re-render
	});
	btn.appendChild(createIcon("fas fa-chevron-left back-btn")); // append dell'icona al bottone
	document.getElementById("titleText").prepend(btn); // prepend del bottone al testo
}

function rifiutaVoto(){
	myFadeIn("rifiutaToast");
	setTimeout(() => {
		myFadeOut("rifiutaToast");
	}, 1000);
	makeGetParameters("rifiutaVoti",showRisultati,["idEsame", myApp.idEsame]);
}

function pubblicaVoti(){
	myFadeIn("pubblicaToast");
	setTimeout(() => {
		myFadeOut("pubblicaToast");
	}, 1000);
	makeGetParameters("pubblicaVoti",showRisultati,["idEsame", myApp.idEsame]);
}

function verbalizzaVoti(){
	myFadeIn("verbalizzaToast");
	setTimeout(() => {
		myFadeOut("verbalizzaToast");
	}, 1000);
	makeGetParameters("verbalizzaVoti",showVerbale,["idEsame", myApp.idEsame]);
}

function showVerbale(request){

	resetTable();
	// QUI FINE LOADER   
	myApp.loaderDiv.style.display = "none";

	document.getElementById("content").innerHTML = ""; // rimuovo gli elementi che ci sono ora nella pagina, non servono piu

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append dei corsi ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		verbale = JSON.parse(request.responseText);
		
		// setto il titolo con anche il bottone per tornare indietro (alla pagina Esito)
		document.getElementById("titleText").innerHTML = "Verbale n° " + verbale.id; // aggiorno jumbotron e back button (torna a HOME)	
		btn = createBtn("a", "", ""); // creo back button
		btn.addEventListener('click', () => {
			getRisultati(); // richiesta alla servlet per tutti i corsi, callback per re-render
		});
		btn.appendChild(createIcon("fas fa-chevron-left back-btn")); // append dell'icona al bottone
		document.getElementById("titleText").prepend(btn); // prepend del bottone al testo

		text1 = createText("h3","","");
		text1.innerHTML = "Data: " + formatDateAndTime(verbale.dataVerbale);
		text2 = createText("h4","","");
		text2.innerHTML = verbale.risultati[0].corso.nome + " - Appello del " + formatDate(verbale.risultati[0].esame.dataAppello);
		document.getElementById("sottotitolo").appendChild(text1); // append h3
		document.getElementById("sottotitolo").appendChild(text2); // append h4

		// creo la tabella del verbale
		tabella = createTabellaVerbale();
		var tbody = document.createElement("tbody");

		for(i = 0; i < verbale.risultati.length; i++){
			var row = document.createElement("tr"); // riga della tabella

			row.appendChild(createCell(verbale.risultati[i].studente.matricola));
			row.appendChild(createCell(verbale.risultati[i].studente.cognome));
			row.appendChild(createCell(verbale.risultati[i].studente.nome));
			row.appendChild(createCell(verbale.risultati[i].voto));

			tbody.appendChild(row);
		}		
		
		tabella.appendChild(tbody);

		var verbaleDiv = createDiv("col-xs-10", "", "");
		verbaleDiv.appendChild(tabella);

		document.getElementById("content").appendChild(createDiv("col-xs-1", "", "")); // layout
		document.getElementById("content").appendChild(verbaleDiv); // aggiungo form al DOM
		document.getElementById("content").appendChild(createDiv("col-xs-1", "", "")); // layout
	}
}

var opacity = 0;
var offset = 40;

function myFadeIn(element){

	if (opacity < 1 && offset > 30) {
		offset -= 0.8;
      	opacity += .1;
      	setTimeout(function(){myFadeIn(element)},40);
   	}
   	document.getElementById(element).style.opacity = opacity;
	document.getElementById(element).style.bottom = offset+"px";

}

function myFadeOut(element){

	if (opacity > 0 && offset < 40) {
		offset += 0.8;
		opacity -= .1;
	   	setTimeout(function(){myFadeOut(element)},40);
	}
	document.getElementById(element).style.opacity = opacity;
	document.getElementById(element).style.bottom = offset+"px";

 }