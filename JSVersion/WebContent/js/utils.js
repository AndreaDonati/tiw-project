/* Funzione per creare un DIV con attributi e aggiungerlo alla pagina */
function createDiv(classes, id, role){
    let div = document.createElement("div");
    div.className = classes;
	div.id = id;
    div.setAttribute("role", role);

    return div;
}

/* Funzione per creare un DIV LABELLED (solo per collapse accordion) con attributi e aggiungerlo alla pagina */
function createLabelledDiv(classes, id, role, labelledby){
    let div = createDiv(classes, id, role);
    div.setAttribute("aria-labelledby", labelledby);

    return div;
}

/* Funzione per creare un elemento di testo (in base al parametro TAG) con attributi e aggiungerlo alla pagina */
function createText(tag, classes, id){
    let txt = document.createElement(tag);
    txt.className = classes;	
	txt.id = id;

    return txt;
}

/* Funzione per creare un elemento di testo dell'accordion (in base al parametro TAG) con attributi e testo e aggiungerlo alla pagina */
function createAccordionText(tag){
    let txt = document.createElement(tag);
    txt.innerHTML = "Clicca sull\'appello per visualizzare i dati:";

    return txt;
}

/* Funzione per creare un bottone (in base al parametro TAG) con attributi */
function createBtn(tag, classes, text){
    let btn = document.createElement(tag);
    btn.className = classes;
    btn.innerHTML = text;
    return btn;
}

/* Funzione per creare un A dell'accordion con attributi specifici */
function createAccordionBtn(text, i){
    let btn = document.createElement("a");
    btn.setAttribute("role", "button");
    btn.setAttribute("data-toggle", "collapse");
    btn.setAttribute("data-parent", "#accordion");
    btn.setAttribute("href", "#collapse"+i);
    btn.setAttribute("aria-expanded", "false");
    btn.setAttribute("aria-controls", "collapse"+i);
    btn.className = "collapsed";
    btn.innerHTML = text;
    return btn;    
}

/* Funzione per creare un I, serve principalmente per il back button */
function createIcon(classes){
    let icon = document.createElement("i");
    icon.className = classes;
    return icon;
}

/* Funzione per creare le opzioni della select, restituisce una lista */
function createOptions(empty){
    let text = ["", "assente", "rimandato", "riprovato"];

    for (let index = 18; index < 31; index++) {
        text.push("" + index);
    }

    text.push("30 e Lode");

    empty ? null : text.shift();

    return text;
}

/* Funzione per creare select usando la lista di createOptions */
function createSelect(id, empty){
    let sel = document.createElement("select");
    sel.setAttribute("name", "voto");
    sel.setAttribute("id", id);

    createOptions(empty).forEach(element => {
        let opt = document.createElement("option");
        opt.innerHTML = element;
        sel.appendChild(opt);
    });

    return sel;
}

/* Funzione per creare una cella della tabella con contenuto passato come parametro */
function createCell(text){
    let td = document.createElement("td");
    td.innerHTML = text;
    return td;
}

/* Funzione per creare una cella dell'header della tabella con contenuto passato come parametro */
function createHeaderCell(text){
    let td = document.createElement("th");
    td.innerHTML = text;
    return td;
}

/* Funzione per creare una checkbox (Modal) con id passato come parametro */
function createCheckbox(id){
    let input = document.createElement("input");
    input.className = "form-check-input";
    input.setAttribute("type", "checkbox");
    input.setAttribute("value", "");
    input.setAttribute("id", id);
    input.style.cursor = "pointer";

    return input;
}

/* Funzione per creare hidden input */
function createHidden(){
    let input = document.createElement("input");
    input.setAttribute("type", "hidden");

    return input;
}

/* Funzione per creare form di modifica voto */
function createForm(){
    let form = document.createElement("form");
    form.setAttribute("action", "#");
    form.setAttribute("method", "POST");
    form.id = "modificaForm"

    return form;
}

/* Funzione per creare tabella per modifica voto */
function createTabella(studente, voto){
    
    let t = document.createElement("table");
    t.className = "table my-table";
    let thead = document.createElement("thead");
    let tbody = document.createElement("tbody");

    let row = document.createElement("tr"); // riga della tabella

    row.appendChild(createHeaderCell("Matricola"));
    row.appendChild(createHeaderCell("Cognome"));
    row.appendChild(createHeaderCell("Nome"));
    row.appendChild(createHeaderCell("E-mail"));
    row.appendChild(createHeaderCell("Corso di Laurea"));
    row.appendChild(createHeaderCell("Voto"));
    row.appendChild(createHeaderCell(""));

    thead.appendChild(row);
    t.appendChild(thead);

    row = document.createElement("tr"); // riga della tabella
		
    row.appendChild(createCell(studente.matricola)); //cella matricola
    row.appendChild(createCell(studente.cognome)); // cella cognome
    row.appendChild(createCell(studente.nome)); // cella nome
    row.appendChild(createCell(studente.email.split("@")[0]+'<br/>@'+studente.email.split("@")[1])); // cella mail
    row.appendChild(createCell(studente.cdl)); // cella cdl

    let select = createSelect("voto", false); // creazione select
        
    selectedIndex = createOptions(false).indexOf(voto);
    if(selectedIndex == -1)
        select.getElementsByTagName('option')[0].selected = "selected";
    else
        select.getElementsByTagName('option')[selectedIndex].selected = "selected";

    let cell = document.createElement("td"); 
	cell.append(select);
	row.appendChild(cell); //cella select

    let btn = createBtn("input", "modifica-btn", "");
    btn.id = "applicaButton";
    btn.setAttribute("value", "Applica");
    btn.addEventListener('click', () => {
		inserisciVoti(); // richiesta alla servlet per inserire voto
	});
    cell = document.createElement("td"); 
    cell.append(btn);
	row.appendChild(cell); //cella applica

    tbody.appendChild(row);
    t.appendChild(tbody);

    return t;
}


/* Funzione per creare tabella per verbale */
function createTabellaVerbale(){
    
    // creo tabella e thead
    let t = document.createElement("table");
    t.className = "table my-table";
    let thead = document.createElement("thead");

    let row = document.createElement("tr"); // riga della tabella

    row.appendChild(createHeaderCell("Matricola"));
    row.appendChild(createHeaderCell("Cognome"));
    row.appendChild(createHeaderCell("Nome"));
    row.appendChild(createHeaderCell("Voto"));

    thead.appendChild(row);
    t.appendChild(thead);

    return t;
}