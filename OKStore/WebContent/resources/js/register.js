document.getElementById("formAddUser:registerBtn").addEventListener("click", function(event){
	
	var username = document.getElementById("formAddUser:usernameRegister").value;
	var pass = document.getElementById("formAddUser:passwordRegister").value;
	var usernameLength = username.length;
	var minUsernameLength = 12;
	var passLength = pass.length;
	var minPassLength = 15;
	
	var e = document.querySelector('[id$="langMenu"]');
	var language = e.options[e.selectedIndex].value;
	
	var usernameMessageElement = document.querySelector('[id$="usernameRegisterMessage"]');
	usernameMessageElement.innerHTML = "";
	usernameMessageElement.style.color = "red";
	
	if(usernameLength < minUsernameLength) {
		switch (language) {
			case "sr":
				usernameMessageElement.innerHTML = "Du\u017Eina mora biti najmanje " + minUsernameLength + " karaktera";			
				break;
			case "en":
				usernameMessageElement.innerHTML = "Username length must be at least " + minUsernameLength + " characters long";			
				break;
		}
		event.preventDefault();
	} else if(username.includes("@")) {
		switch (language) {
			case "sr":
				usernameMessageElement.innerHTML = "Zabranjen je karakter @";			
				break;
			case "en":
				usernameMessageElement.innerHTML = "Username cann't contain character @";			
				break;
		}
		event.preventDefault();
	} else if(username.includes("#")) {
		switch (language) {
			case "sr":
				usernameMessageElement.innerHTML = "Zabranjen je karakter #";			
				break;
			case "en":
				usernameMessageElement.innerHTML = "Username cann't contain character #";			
				break;
		}
		event.preventDefault();
	} else if(username.includes("/")) {
		switch (language) {
			case "sr":
				usernameMessageElement.innerHTML = "Zabranjen je karakter /";			
				break;
			case "en":
				usernameMessageElement.innerHTML = "Username cann't contain character /";			
				break;
		}
		event.preventDefault();
	}

//	var language = document.getElementById("j_idt13:langMenu").options[e.selectedIndex].value;
	var passMessageElement = document.querySelector('[id$="passwordRegisterMessage"]');
	passMessageElement.innerHTML = "";
	passMessageElement.style.color = "red";

	if(passLength < minPassLength) {
		switch (language) {
			case "sr":
				passMessageElement.innerHTML = "Du\u017Eina lozinke mora biti najmanje " + minPassLength + " karaktera";			
				break;
			case "en":
				passMessageElement.innerHTML = "Password length must be at least " + minPassLength + " characters long";			
				break;
		}
		event.preventDefault();
	} else if (!(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/.test(pass))) {
		switch (language) {
			case "sr":
				passMessageElement.innerHTML = "Lozinka mora sadr\u017Eati bar jedno veliko slovo, " +
						"bar jedno malo slovo i bar jedan broj";			
				break;
			case "en":
				passMessageElement.innerHTML = "Password must contain at least one upper case letter, " +
						"one lower case letter and one number";			
				break;
		}
		event.preventDefault();
	}
	
	
});
