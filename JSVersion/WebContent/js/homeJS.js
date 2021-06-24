/* Oggetto globale per contenere le variabili globali */
var myApp = {};
myApp.toastOpacity = 0;
myApp.toastOffset = 40;
myApp.request = {};

/* Funzione per mandare richieste GET senza parametri */
function makeGet(servletUrl, callback) {	
	// Richiesta asincrona
	myApp.request = new XMLHttpRequest(); // Nuova richiesta
	myApp.request.onreadystatechange = function (req) {callback(req.target);}; // Chiamata al callback
	myApp.request.open("GET", servletUrl); 
	myApp.request.send();
}

/* Funzione per mandare richieste GET con parametri */
function makeGetParameters(servletUrl, callback, paramNameValue) {	
	// Richiesta asincrona
	myApp.request = new XMLHttpRequest(); // Nuova richiesta
	myApp.request.onreadystatechange = function (req) {callback(req.target);}; // Chiamata al callback
	
	// Composizione URL per una GET in base ai parametri passati
	servletUrl += "?";
	servletUrl += paramNameValue[0] + "=" + paramNameValue[1];
	for(let i = 2; i < paramNameValue.length; i + 2)
		servletUrl += "&" + paramNameValue[i] + "=" + paramNameValue[i + 1];

	myApp.request.open("GET", servletUrl); 
	myApp.request.send();
}

function makePost(servletUrl, callback) {
	myApp.loaderDiv.style.display = "block";

	myApp.request = new XMLHttpRequest(); // Nuova richiesta
	let url = servletUrl; // URL della Servlet

	myApp.request.onreadystatechange = function (req) {callback(req.target);};; // Chiamata al callback
	myApp.request.open("POST", url); // Richiesta POST all'URL
	myApp.request.send(); // Mando la richiesta
}

function makePostForm(formName, servletUrl, callback) {
	myApp.loaderDiv.style.display = "block";

	myApp.request = new XMLHttpRequest(); // Nuova richiesta
	let url = servletUrl; // URL della Servlet
	let formElement = document.querySelector("#"+formName+""); // Prendo parametri dal form
	let formData = new FormData(formElement);

	myApp.request.onreadystatechange = function (req) {callback(req.target);};; // Chiamata al callback
	myApp.request.open("POST", url); // Richiesta POST all'URL
	myApp.request.send(formData); // Mando dati del form
}

function setLabelListeners(){
	// prendo tutti gli elementi (tolgo l'ultimo dato che è vuoto)
	let labels = document.getElementById("headTabellaProf").getElementsByTagName("th");

	for (let i = 0; i < labels.length - 1; i++) {
		labels[i].firstChild.addEventListener('click', () => {
			ordinaTabella(i);
		});			
	}
}

function init() {	
	// Aggiungo listener al bottone che rimanda alla home
	document.getElementById("navbar-btn").addEventListener('click', () => {
		resetStorage(); // cancella variabili globali che non servono più
		resetTable(); // cancella elementi che non servono più
		makeGet("getCorsi", showCorsi); // richiesta per ritornare alla home
	});

	// Aggiungo listener al bottone logout
	document.getElementById("logout-btn").addEventListener('click', () => {
		delete myApp; // cancella tutte le variabili
	});
	
	// e aggiungo listener ai bottoni pubblica e verbalizza
	document.getElementById("bottonePubblica").addEventListener('click', () => {
		pubblicaVoti();
	});
	document.getElementById("bottoneVerbalizza").addEventListener('click', () => {
		verbalizzaVoti();
	});

	// aggiungo listener anche alle label della tabella
	setLabelListeners();


	// Prendo l'elemento del loader
	myApp.loaderDiv = document.getElementById("loader");	

	// Start del loader 
	myApp.loaderDiv.style.display = "block";

	//makeGet("getInfoUtente", showInfo); // Chiamata alla servlet che restituisce i dati della sessione
	makePost("getInfoUtente", showInfo); // Chiamata alla servlet che restituisce i dati della sessione
	makeGet("getCorsi", showCorsi); // Dati dei corsi disponibili
}

function showInfo(request) {
    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) sostituisco informazioni utente nella sidebar
	if (request.readyState == 4 && request.status == 200 ) {
		let utente = JSON.parse(request.responseText);
		myApp.ruoloUtente = utente.ruolo;

		document.getElementById("immagine").src = utente.image;
		document.getElementById("nome").textContent = utente.nome + ' ' + utente.cognome;
		document.getElementById("mail").textContent = utente.email.split("@")[0]+'\n@'+utente.email.split("@")[1];
		document.getElementById("matricola").textContent = utente.matricola;
	}
}

function showCorsi(request) {
	myApp.loaderDiv.style.display = "none";

	document.getElementById("content").innerHTML = ""; // reset della pagina
	document.getElementById("titleText").textContent = "I tuoi corsi"; // aggiorno jumbotron

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append dei corsi ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		let corsi = JSON.parse(request.responseText);

		if(corsi != null){
			let accordionDiv = createDiv("panel-group", "accordion", "tablist"); // div accordion

			for(let i = 0; i < corsi.length; i++){				
				let txt = createText("h4", "panel-title", "pTitle"+i); // testo con nome del corso
				let btn = createBtn("a", "menu", corsi[i].nome); // bottone per selezionare corso
				btn.setAttribute("data-parent", "#accordion");
				btn.setAttribute("nome", corsi[i].nome);
				btn.addEventListener('click', (event) => {
					myApp.nomeCorso = event.target.getAttribute("nome");
					document.getElementById("accordion").innerHTML = "";
					getEsami(); // richiesta alla servlet per il corso selezionato

				});
				txt.appendChild(btn);

				let div = createDiv("panel panel-default", "pDefault"+i, ""); // div per elemento accordion
				div.appendChild(createDiv("panel-heading", "pHead"+i, "tab").appendChild(txt)); // div per titolo
				accordionDiv.appendChild(div);
			}
			document.getElementById("content").appendChild(accordionDiv);
		} else{
			//TODO controllare se funzioan
			let div = createDiv("", "no-results-msg", ""); // div per no results
			let txt = document.createElement("h4");
			myApp.ruoloUtente == "teacher" ? txt.textContent = "Non sei il docente di nessun corso al momento!" : txt.textContent = "Non sei iscritto a nessun corso al momento!";
			div.append(txt);
			document.getElementById("content").appendChild(div);
			/*
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
			*/
		}
	}
}

function getEsami() {
	myApp.loaderDiv.style.display = "block";

	document.getElementById("content").innerHTML = ""; // reset della pagina

	document.getElementById("titleText").textContent = "I tuoi esami"; // aggiorno jumbotron e back button (torna a HOME)	
	let btn = createBtn("a", "", ""); // creo back button
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
		
		let corsiEsami = JSON.parse(request.responseText);

		let accordionDiv = createDiv("panel-group", "accordion", "tablist"); // div accordion

		for(let i = 0; i < corsiEsami[0].length; i++){

			let txt = createText("h4", "panel-title", "pTitle"+i); // testo con nome del corso

			let btn = createAccordionBtn(corsiEsami[0][i].nome+' '+corsiEsami[0][i].anno, i);
    		txt.appendChild(btn); // bottone per corso-anno 

			let txt2;

			if(corsiEsami[1] != null && corsiEsami[1][i].length > 0){
				txt2 = createAccordionText("p");
				
				for(let j = 0; j < corsiEsami[1][i].length; j++){			
					let btn2 = createBtn("button", "a-btn", "Appello "+  formatDate(corsiEsami[1][i][j].dataAppello)); // bottone per specifico esame
					btn2.setAttribute("idEsame", corsiEsami[1][i][j].id);
					btn2.addEventListener('click', (event) => {
						myApp.idEsame = event.target.getAttribute("idEsame");
						document.getElementById("accordion").innerHTML = "";
						getRisultati(); // richiesta alla servlet per il corso selezionato
					});
					txt2.appendChild(btn2);
				}
		 	} else{
				txt2 = document.createElement("p");
				myApp.ruoloUtente == "teacher" ? txt2.textContent = "Non sono presenti appelli per questo corso." : txt2.textContent = "Non sei iscritto/a a nessun appello di questo corso.";
			}

			let div = createDiv("panel panel-default", "pDefault"+i, ""); // div per elemento accordion
			let headDiv = createDiv("panel-heading", "pHead"+i, "tab"); // div per head accordion
			headDiv.append(txt) // aggiungo corso-anno
			div.appendChild(headDiv); // aggiungo head a elemento

			let collapseDiv = createLabelledDiv("panel-collapse collapse", "collapse"+i, "tabpanel", "pHead"+i); // div per parte nascosta
			let elencoDiv = createDiv("panel-body", "appelli"+i, ""); // div per elenco appelli
			elencoDiv.append(txt2); // aggiungo testo e bottoni appelli
			collapseDiv.appendChild(elencoDiv); // aggiungo al div
			div.appendChild(collapseDiv); // aggiungo all'elemento

			accordionDiv.appendChild(div); // aggiungo elemento all'accordion
		}
		document.getElementById("content").appendChild(accordionDiv); // aggiungo accordion al DOM
	}
}

function getRisultati() {
	document.getElementById("content").innerHTML = "";

	myApp.loaderDiv.style.display = "block";

	// aggiorno anche il titolo della pagina e il back button (torna a ESAMI)
	document.getElementById("sottotitolo").innerHTML = ""; // aggiorno jumbotron e back button (torna a HOME)	
	
	document.getElementById("titleText").textContent = "Esito"; // aggiorno jumbotron e back button (torna a HOME)	
	let btn = createBtn("a", "", ""); // creo back button
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

	myApp.loaderDiv.style.display = "none";

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append di tutti i corsi + esami ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		let risposta = JSON.parse(request.responseText);

		let risultati = risposta.risultati;
		let arePubblicabili = risposta.pubblicabili;
		let areVerbalizzabili = risposta.verbalizzabili;

		if(myApp.ruoloUtente == "teacher"){
			showRisultatiProf(risultati, arePubblicabili, areVerbalizzabili, risposta);
		} else {
			showRisultatiStudente(risultati);
	}
}

function showRisultatiProf(risultati, arePubblicabili, areVerbalizzabili, risposta){
	// prendo la lista degli studenti e la salvo (servirà dopo)
	let studenti = [];
	risultati.forEach(e => {
		studenti.push(e.studente);
	});
	myApp.studenti = JSON.stringify(studenti);

	// prendo la lista dei risultati con voto non inserito
	let risultatiModal = [];
	risultati.forEach(e => {
		if(e.stato == "non inserito") // per ogni voto non inserito aggiungo una riga di modifica nel modal
			risultatiModal.push(e);
	});

	riempiModal(risultatiModal);
	riempiTabellaRisultati(risultati, arePubblicabili, areVerbalizzabili, risposta);
}


function riempiModal(risultatiModal){
	if(risultatiModal.length == 0){ // se non ci sono voti da modificare
		document.getElementById("headTabellaModal").style.display = "none";
		$("#bottoneInserimento").attr("disabled", "disabled"); 

		document.getElementById("tabellaModal").textContent = "<br>Non ci sono voti da inserire.";
		$("#bottoneInvia").attr("disabled", "disabled"); // disabilito bottone
	}
	else {
		$("#bottoneInserimento").removeAttr("disabled");
		document.getElementById("headTabellaModal").style.display = "";
		document.getElementById("tabellaModal").innerHTML = "";

		document.getElementById("checkAll").addEventListener('click', () => {
			selezionaTutto();
		});

		document.getElementById("bottoneInvia").addEventListener('click', () => {
				inserimentoMultiplo();
		});

		// riempio il modal con i dati appena ricevuti, sono sempre quelli più aggiornati
		for(let i = 0; i < risultatiModal.length; i++){
			let row = document.createElement("tr"); // riga della tabella

			let checkbox = createCheckbox("check"+i); // creazione checkbox
			checkbox.addEventListener('change', (event) => {
				selezionaRiga(event.target.id); //gestione selezione della checkbox
			});
			let cell = document.createElement("td"); 
			cell.append(checkbox);
			row.appendChild(cell); //cella checkbox

			let hiddenInput = createHidden();
			hiddenInput.setAttribute("value", risultatiModal[i].studente.matricola);
			hiddenInput.setAttribute("name", "matricola");

			cell = createCell(risultatiModal[i].studente.matricola);

			cell.append(hiddenInput);
			row.appendChild(cell); //cella matricola

			row.appendChild(createCell(risultatiModal[i].studente.cognome)); // cella cognome
			row.appendChild(createCell(risultatiModal[i].studente.nome)); // cella nome
			row.appendChild(createCell(risultatiModal[i].studente.email.split("@")[0]+'<br/>@'+risultatiModal[i].studente.email.split("@")[1])); // cella mail
			row.appendChild(createCell(risultatiModal[i].studente.cdl)); // cella cdl

			let select = createSelect("sel"+i, true); // creazione select
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
}


function riempiTabellaRisultati(risultati, arePubblicabili, areVerbalizzabili, risposta){
	// rendo visibile il div principale
	document.getElementById("accordion2").style.display = "block";

	// rendo visibile il div della tabella prof e nascondo quella studente
	document.getElementById("divTabellaProf").style.display = "block";
	document.getElementById("divTabellaStud").style.display = "none";

	// aggiorno titolo e data dell'appello 
	document.getElementById("titoloCorso").textContent = myApp.nomeCorso + " - " + formatDate(risposta.esame.dataAppello);				

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

		for(let i = 0; i < risultati.length; i++){
			let row = document.createElement("tr"); // riga della tabella

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
				let modButton = createBtn("a", "modifica-btn", "Modifica"); // creazione bottone modifica
				modButton.setAttribute("matricola", risultati[i].studente.matricola);
				modButton.setAttribute("currentVoto", risultati[i].voto);
				modButton.addEventListener('click', (event) => {
					resetTable();
					modificaVoti(parseInt(event.target.getAttribute("matricola")), event.target.getAttribute("currentVoto"));
				});	
				let cell = document.createElement("td"); 
				cell.append(modButton);
				row.appendChild(cell); //cella modifica
			}
			document.getElementById("tabellaVotiProf").append(row);
		}
	}
	else{
		document.getElementById("headTabellaProf").style.display = "none";
		document.getElementById("tabellaVotiProf").textContent = "Nessuno studente iscritto all'appello."; 

		// nascondo i bottoni pubblica e verbalizza e ins multiplo
		document.getElementById("bottonePubblica").style.display = "none";
		document.getElementById("bottoneVerbalizza").style.display = "none";
		document.getElementById("bottoneInserimento").style.display = "none";
	}
}

function showRisultatiStudente(risultati){
	// rendo visibile il div principale
	document.getElementById("accordion2").style.display = "block";

	// rendo visibile il div della tabella studente e nascondo quella del prof
	document.getElementById("divTabellaStud").style.display = "block";
	document.getElementById("divTabellaProf").style.display = "none";

	// aggiorno titolo
	document.getElementById("titoloCorso").textContent = myApp.nomeCorso;

	// controllo se il voto non è ancora definito
	if(risultati == null){
		document.getElementById("headTabellaStud").style.display = "none";
		document.getElementById("tabellaVotiStud").textContent = "Voto non ancora definito.";
	}
	else{
		document.getElementById("headTabellaStud").style.display = "";
		document.getElementById("tabellaVotiStud").innerHTML = "";

		for(let i = 0; i < risultati.length; i++){
			let row = document.createElement("tr"); // riga della tabella

			row.appendChild(createCell(risultati[i].studente.matricola)); // cella matricola
			row.appendChild(createCell(risultati[i].studente.nome + ' ' + risultati[i].studente.cognome)); // cella nome e cognome
			row.appendChild(createCell(formatDate(risultati[0].esame.dataAppello))); // cella data

			if(risultati[i].voto == null)
				row.appendChild(createCell("")); // cella voto (vuota)
			else
				row.appendChild(createCell(risultati[i].voto)); // cella voto 

			row.appendChild(createCell(risultati[i].stato)); // cella stato

			let rifButton;
			if(risultati[i].rifiutabile){	
				rifButton = createBtn("a", "rifiuta-btn", "Rifiuta"); // creazione bottone rifiuta
				rifButton.addEventListener('click', (event) => {	
					rifiutaVoto();
				});	
				let cell = document.createElement("td"); 
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
	makePostForm("modalForm","inserimentoMultiplo",showRisultati);
}


function modificaVoti(matricola, voto) {
	document.getElementById("titleText").textContent = "Modifica Voto"; // aggiorno jumbotron e back button (torna a RISULTATI)	
	let btn = createBtn("a", "", ""); // creo back button
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

	let tabella = createTabella(studente, voto);
	let form = createForm();

	let hiddenId = createHidden();
	hiddenId.setAttribute("name", "idEsame");
	hiddenId.setAttribute("value", myApp.idEsame);

	let hiddenMatr = createHidden();
	hiddenMatr.setAttribute("name", "matricola");
	hiddenMatr.setAttribute("value", matricola);

	form.appendChild(tabella);
	form.appendChild(hiddenId);
	form.appendChild(hiddenMatr);

	let modificaDiv = createDiv("col-xs-10", "", "");
	modificaDiv.appendChild(form);

	document.getElementById("content").appendChild(createDiv("col-xs-1", "", "")); // layout
	document.getElementById("content").appendChild(modificaDiv); // aggiungo form al DOM
	document.getElementById("content").appendChild(createDiv("col-xs-1", "", "")); // layout
}

function inserisciVoti(){
	makePostForm("modificaForm", "inserisciVoti", showRisultati);

	// aggiorno anche il titolo della pagina e il back button (torna a ESAMI)
	document.getElementById("titleText").textContent = "Esito"; // aggiorno jumbotron e back button (torna a HOME)	
	let btn = createBtn("a", "", ""); // creo back button
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
	}, 1500);
	makeGetParameters("rifiutaVoti",showRisultati,["idEsame", myApp.idEsame]);
}

function pubblicaVoti(){
	myFadeIn("pubblicaToast");
	setTimeout(() => {
		myFadeOut("pubblicaToast");
	}, 1500);
	makeGetParameters("pubblicaVoti",showRisultati,["idEsame", myApp.idEsame]);
}

function verbalizzaVoti(){
	myFadeIn("verbalizzaToast");
	setTimeout(() => {
		myFadeOut("verbalizzaToast");
	}, 1500);
	makeGetParameters("verbalizzaVoti",showVerbale,["idEsame", myApp.idEsame]);
}

function showVerbale(request){
	resetTable();

	myApp.loaderDiv.style.display = "none";

	document.getElementById("content").innerHTML = ""; // rimuovo gli elementi che ci sono ora nella pagina, non servono piu

    // Se 400 (Bad request) o 401 (Unauthorized) loggo l'errore
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	console.log(JSON.parse(request.responseText)["errorMessage"]);

    // Se 200 (OK) append dei corsi ricevuti al div corrispondente
	if (request.readyState == 4 && request.status == 200 ) {
		let verbale = JSON.parse(request.responseText);
		
		// setto il titolo con anche il bottone per tornare indietro (alla pagina Esito)
		document.getElementById("titleText").textContent = "Verbale n° " + verbale.id; // aggiorno jumbotron e back button (torna a HOME)	
		let btn = createBtn("a", "", ""); // creo back button
		btn.addEventListener('click', () => {
			getRisultati(); // richiesta alla servlet per tutti i corsi, callback per re-render
		});
		btn.appendChild(createIcon("fas fa-chevron-left back-btn")); // append dell'icona al bottone
		document.getElementById("titleText").prepend(btn); // prepend del bottone al testo

		let text1 = createText("h3","","");
		text1.textContent = "Data: " + formatDateAndTime(verbale.dataVerbale);
		let text2 = createText("h4","","");
		text2.textContent = verbale.risultati[0].corso.nome + " - Appello del " + formatDate(verbale.risultati[0].esame.dataAppello);
		document.getElementById("sottotitolo").appendChild(text1); // append h3
		document.getElementById("sottotitolo").appendChild(text2); // append h4

		// creo la tabella del verbale
		let tabella = createTabellaVerbale();
		let tbody = document.createElement("tbody");

		for(let i = 0; i < verbale.risultati.length; i++){
			let row = document.createElement("tr"); // riga della tabella

			row.appendChild(createCell(verbale.risultati[i].studente.matricola));
			row.appendChild(createCell(verbale.risultati[i].studente.cognome));
			row.appendChild(createCell(verbale.risultati[i].studente.nome));
			row.appendChild(createCell(verbale.risultati[i].voto));

			tbody.appendChild(row);
		}		
		
		tabella.appendChild(tbody);

		let verbaleDiv = createDiv("col-xs-10", "", "");
		verbaleDiv.appendChild(tabella);

		document.getElementById("content").appendChild(createDiv("col-xs-1", "", "")); // layout
		document.getElementById("content").appendChild(verbaleDiv); // aggiungo form al DOM
		document.getElementById("content").appendChild(createDiv("col-xs-1", "", "")); // layout
	}
}

function myFadeIn(element){
	if (myApp.toastOpacity < 1 && myApp.toastOffset > 30) {
		myApp.toastOffset -= 0.8;
		myApp.toastOpacity += .1;
      	setTimeout(function(){myFadeIn(element)},40);
   	}
   	document.getElementById(element).style.opacity = myApp.toastOpacity;
	document.getElementById(element).style.bottom = myApp.toastOffset + "px";
}

function myFadeOut(element){
	if (myApp.toastOpacity > 0 && myApp.toastOffset < 40) {
		myApp.toastOffset += 0.8;
		myApp.toastOpacity -= .1;
	   	setTimeout(function(){myFadeOut(element)},40);
	}
	document.getElementById(element).style.opacity = myApp.toastOpacity;
	document.getElementById(element).style.bottom = myApp.toastOffset + "px";
 }
 
/* Animazione Navbar con scorrimento - jQuery */
$(window).scroll(function(){
    if($(document).scrollTop() > 80){
      $('nav').addClass('animate');
      $('h4').addClass('animate-side');
      $('img').addClass('animate-img');
    } else{
      $('nav').removeClass('animate');
      $('h4').removeClass('animate-side');
      $('img').removeClass('animate-img');
    }
});