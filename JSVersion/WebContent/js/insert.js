/* Gestione dell'inserimento multiplo, principalmente JQuery per comodità */
myApp.linkedBox = []; //rappresenta tutte le box selezionate
myApp.selectedPos = 0; //rappresenta l'option selezionata della select 

/* Gestione della selezione della checkbox principale */
function selezionaTutto(){
	// seleziona tutte le righe della tabella
    let rows = document.getElementById("tabellaVotiProf").rows;
    myApp.linkedBox = [];

    for (let i = 0; i < rows.length; i++){
		if($("#checkAll").prop("checked")){
            // selezionate tutte, pusho tutto nella lista e aggiorno anche i voti
            $("#check"+i).prop("checked", true);
            myApp.linkedBox.push(i);
            $("#sel"+i).prop('selectedIndex', myApp.selectedPos);
        } else{
            // deselezionate tutte
            $("#check"+i).prop("checked", false);
            myApp.linkedBox.splice(myApp.linkedBox.indexOf(i), 1);
        }
    }
}

/* Gestione della selezione di una singola checkbox */
function selezionaRiga(id){
    let index = parseInt(id.charAt(id.length-1));

    if($("#check"+index).prop("checked")) {// se non è gia nella lista
        myApp.linkedBox.push(index);
        $("#sel"+index).prop('selectedIndex', myApp.selectedPos);

        // ora controllo se METTERE il check a "checkAll" oppure no
        if(areAllSelected()){
            $("#checkAll").prop("checked", true);
        }

    } else{
        myApp.linkedBox.splice(myApp.linkedBox.indexOf(index), 1);
        // ora controllo se TOGLIERE il check a "checkAll" oppure no
        if(!areAllSelected())
            $("#checkAll").prop("checked", false);

    }   
}

/* Funzione di controllo della selezione */
function areAllSelected(){
    let rows = document.getElementById("tabellaModal").rows;

    for (let i = 0; i < rows.length; i++){
		if(!$("#check"+i).prop("checked"))
            return false;
    }
    return true;
}

/* Gestione della selezione di una option */
function handleSelection(sel){
    // prendo la riga del select
    let index = parseInt((sel.id).charAt((sel.id).length-1));

    if(myApp.linkedBox.indexOf(index) != -1){ //se la checkbox è presente nella lista
        // prendo la posizione dell'option selezionata
        myApp.selectedPos = $("#"+sel.id).prop('selectedIndex');

        // se la riga del select è in linkedBox allora devo aggiornare tutte le select collegate, altrimenti nulla
        myApp.linkedBox.forEach(i => {
            $("#sel"+i).prop('selectedIndex', myApp.selectedPos);
        });
        myApp.selectedPos = 0;
    }
}