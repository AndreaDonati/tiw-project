/* Gestione dell'ordinamento della tabella, principalmente JQuery per comodità */
function ordinaTabella(n) {
	// aggiorno le frecce
	let i;
	for(i = 0; i < 7; i++){
		if(i != n){
			$("#freccia"+i).removeClass('fas fa-chevron-up sort-i');
			$("#freccia"+i).removeClass('fas fa-chevron-down sort-i');
		}
	}
	$("#freccia"+n).removeClass('fas fa-chevron-down sort-i');
	$("#freccia"+n).addClass('fas fa-chevron-up sort-i');

	// bubble sort :P
	let tabella, rows, switching, x, y, shouldSwitch, ordine, switchDone;
	tabella = document.getElementById("tabellaVotiProf");
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
	let ordinamentoVoti = ["","assente","rimandato","riprovato","18","19","20","21","22","23","24","25","26","27","28","29","30","30 e Lode"];
	if (ordine == "ASC") 
		return ordinamentoVoti.indexOf(voto1) > ordinamentoVoti.indexOf(voto2);
	else 
		return ordinamentoVoti.indexOf(voto1) < ordinamentoVoti.indexOf(voto2);
}

function compareRows(x, y, ordine, n) {
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