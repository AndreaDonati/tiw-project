var request;

function init() {
// Rimpiazza la gestione del browser
// Event listener sul bottone di login
    document.getElementById("loginButton").addEventListener('click', (event) => {    
        event.preventDefault(); // Evita il submit
        makePost(); // Chiamata asincrona 
    });
}

function makePost() {	
	// Richiesta asincrona non cambia la pagina
	request = new XMLHttpRequest(); // Nuova richiesta
	var url = 'Login'; // URL della Servlet
	var formElement = document.querySelector("form"); // Prendo parametri dal form
	var formData = new FormData(formElement);
    
	request.onreadystatechange = showResults; // Chiamata al callback
	request.open("POST", url); // Richiesta POST all'URL
	request.send(formData); // Mando dati del form
}

// CALLBACK: presenta risultato nella pagina
function showResults() {
    // Se 400 (Bad request) o 401 (Unauthorized) modifico il testo del <p> errorMessage
	if (request.readyState == 4 && (request.status == 400 || request.status == 401)) 
	 	document.getElementById("errorMessage").textContent = JSON.parse(request.responseText)["errorMessage"];

    // Se 200 (OK) reindirizzo l'utente alla home (mandata con un sendRedirect dalla Servlet)
	if (request.readyState == 4 && request.status == 200 ) 
		window.location = request.responseURL;

}