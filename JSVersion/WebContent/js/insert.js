/* Gestione dell'inserimento multiplo, principalmente JQuery per comodità */

var linkedBox = []; //rappresenta tutte le box selezionate
var selectedPos = 0; //rappresenta l'option selezionata della select 

/* Gestione della selezione della checkbox principale */
function selezionaTutto(){
	// seleziona tutte le righe della tabella
    rows = document.getElementById("tabellaVotiProf").rows;
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

/* Gestione della selezione di una singola checkbox */
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

/* Funzione di controllo della selezione */
function areAllSelected(){
    rows = document.getElementById("tabellaModal").rows;

    for (i = 0; i < rows.length; i++){
		if(!$("#check"+i).prop("checked"))
            return false;
    }
    return true;
}

/* Gestione della selezione di una option */
function handleSelection(sel){
    // prendo la riga del select
    index = parseInt((sel.id).charAt((sel.id).length-1));

    if(linkedBox.indexOf(index) != -1){ //se la checkbox è presente nella lista
        // prendo la posizione dell'option selezionata
        selectedPos = $("#"+sel.id).prop('selectedIndex');

        // se la riga del select è in linkedBox allora devo aggiornare tutte le select collegate, altrimenti nulla
        linkedBox.forEach(i => {
            $("#sel"+i).prop('selectedIndex', selectedPos);
        });
        selectedPos = 0;
    }
}